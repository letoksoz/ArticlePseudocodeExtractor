package org.pseudocode.extract.other;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class OpenAllGzips {

    private static final String baseDirectory = "/Volumes/T7/data";
    // private static final String baseDirectory = "/Users/Levent/Desktop/pseudocode/";

    public static void main(String[] args) throws IOException, ArchiveException {
        File directory = new File(baseDirectory);
        open(directory);
    }

    public static void open(File inputFile) throws IOException, ArchiveException {
        if (inputFile.isDirectory()) {
            var list = Arrays.stream(inputFile.listFiles()).sorted().toList();
            for (var file : list) {
                open(file);
            }
        } else if (inputFile.getName().endsWith(".gz")) {

            try {
                unGzip(inputFile, new File(inputFile.getParent()));

                String path = inputFile.getAbsolutePath();
                File outputFile = new File(path.substring(0, path.length() - 3));
                File outputTarFile = new File(path.substring(0, path.length() - 3) + ".tar");
                outputFile.renameTo(outputTarFile);
                outputFile.mkdirs();
                unTar(outputTarFile, outputFile);
                outputTarFile.delete();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Untar an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.tar' extension.
     *
     * @param inputFile the input .tar file
     * @param outputDir the output directory file.
     * @return The {@link List} of {@link File}s with the untared content.
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ArchiveException
     */
    private static List<File> unTar(final File inputFile, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {

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

    /**
     * Ungzip an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.gz' extension.
     *
     * @param inputFile the input .gz file
     * @param outputDir the output directory file.
     * @return The {@File} with the ungzipped content.
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static File unGzip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {

        final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

        final GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
        final FileOutputStream out = new FileOutputStream(outputFile);

        IOUtils.copy(in, out);

        in.close();
        out.close();

        return outputFile;
    }
}
