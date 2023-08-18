package org.pseudocode.extract.pipeline.fileOperation;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.pseudocode.extract.pipeline.Constants;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.pseudocode.extract.pipeline.Constants.*;
import static org.pseudocode.extract.pipeline.util.Util.createYearMonthKey;

public class Extractor {


    public static void unGzipGcloud() throws IOException {
        File gcloudBaseDirectory = new File(t7GcloudPath);
        unGzipGcloud(gcloudBaseDirectory);

    }
    public static void unGzipGcloud(File dir) throws IOException {
        for (var file : dir.listFiles()) {
            if (file.isDirectory()) {
                unGzipGcloud(file);
            } else if (file.getName().endsWith(".gz")) {
                unGzip(file.getCanonicalPath());
            }
        }
    }

    public static void unGzip(String gzipFileName) throws IOException {
        if (!gzipFileName.endsWith(".gz")) {
            return;
        }
        File source = new File(gzipFileName);
        File target = new File(gzipFileName.substring(0, gzipFileName.length() - 3));

        try (GZIPInputStream gis = new GZIPInputStream(
                new FileInputStream(source));
             FileOutputStream fos = new FileOutputStream(target)) {

            // copy GZIPInputStream to FileOutputStream
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }

    public static void unGzipS3(int year, int month) throws IOException, ArchiveException {
        String yearMonthKey = createYearMonthKey(year, month);
        open(new File(Constants.t7S3SrcPath + yearMonthKey));
    }

    private static File unGzip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {

        final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

        final GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
        final FileOutputStream out = new FileOutputStream(outputFile);

        IOUtils.copy(in, out);

        in.close();
        out.close();

        return outputFile;
    }


    public static void unTarS3Src(int year, int month) throws IOException, ArchiveException {
        String yearMonthKey = createYearMonthKey(year, month);

        String filePrefix = "arXiv_src_" + yearMonthKey;
        String filePostfix = ".tar";

        File t7S3Src = new File(t7S3SrcPath);
        var list = t7S3Src.listFiles();
        for (var tarFile : list) {
            if (tarFile.getName().startsWith(filePrefix) && tarFile.getName().endsWith(filePostfix)) {
                unTar(tarFile, new File(t7S3SrcPath));
            }
        }

    }

    private static void unTar(final File inputFile, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {

        // System.out.println(String.format("Untaring %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        try(final InputStream is = new FileInputStream(inputFile);
                final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);) {
            TarArchiveEntry entry = null;
            while ((entry = (TarArchiveEntry) debInputStream.getNextEntry()) != null) {
                final File outputFile = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    // System.out.println(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath()));
                    if (!outputFile.exists()) {
                        // System.out.println(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath()));
                        if (!outputFile.mkdirs()) {
                            throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                        }
                    }
                } else {
                    try (final OutputStream outputFileStream = new FileOutputStream(outputFile)) {
                        IOUtils.copy(debInputStream, outputFileStream);
                    }
                }
            }
            debInputStream.close();
        }
    }

    public static void open(File inputFile) throws IOException, ArchiveException {
        if (inputFile.isDirectory()) {
            var array = inputFile.listFiles();
            if (array != null && array.length != 0) {
                var list = Arrays.stream(array).sorted().toList();
                for (var file : list) {
                    open(file);
                }
            }
        } else if (inputFile.getName().endsWith(".gz")) {
            if (inputFile.getName().startsWith(".")) {
                return;
            }
            String path = inputFile.getAbsolutePath();
            File outputFile = new File(path.substring(0, path.length() - 3));
            File outputTarFile = new File(path.substring(0, path.length() - 3) + ".tar");
            try {
                unGzip(inputFile, new File(inputFile.getParent()));
                outputFile.renameTo(outputTarFile);
                outputFile.mkdirs();
                unTar(outputTarFile, outputFile);
            } catch (IllegalArgumentException | IOException ex){
                outputFile.delete();
                outputTarFile.renameTo(outputFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (outputTarFile.exists()) {
                    outputTarFile.delete();
                }
            }
        }
    }

    private static List<File> unTar2(final File inputFile, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {

        System.out.println(String.format("Untaring %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        final List<File> untaredFiles = new LinkedList<File>();
        final InputStream is = new FileInputStream(inputFile);
        final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
        TarArchiveEntry entry = null;
        while ((entry = (TarArchiveEntry) debInputStream.getNextEntry()) != null) {
            final File outputFile = new File(outputDir, entry.getName());
            if (entry.isDirectory()) {
                // System.out.println(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath()));
                if (!outputFile.exists()) {
                    System.out.println(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath()));
                    if (!outputFile.mkdirs()) {
                        throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                    }
                }
            } else {
                final OutputStream outputFileStream = new FileOutputStream(outputFile);
                IOUtils.copy(debInputStream, outputFileStream);
                outputFileStream.close();
            }
            untaredFiles.add(outputFile);
        }
        debInputStream.close();

        return untaredFiles;
    }

    private static File unGzip2(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {

        final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

        final GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
        final FileOutputStream out = new FileOutputStream(outputFile);

        IOUtils.copy(in, out);

        in.close();
        out.close();

        return outputFile;
    }

}
