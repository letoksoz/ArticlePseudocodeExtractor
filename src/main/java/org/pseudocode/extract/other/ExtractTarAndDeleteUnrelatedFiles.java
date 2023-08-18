package org.pseudocode.extract.other;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ExtractTarAndDeleteUnrelatedFiles {
    private static final String srcDirectory = "/Volumes/T7/data/src";
    private static Set<String> pseudoCodeFiles = new HashSet<>();
    private static Set<String> unpseudoCodeFiles = new HashSet<>();
    private static List<String> unmatchedList = new LinkedList<>();

    public static void main(String[] args) throws IOException, ArchiveException {
        load();
        File directory = new File(srcDirectory);
        var list = Arrays.stream(directory.listFiles()).sorted().toList();
        File outputFile = new File("/Volumes/T7/data/");
        for (var inputFile : list) {
            if (inputFile.getName().endsWith("tar")) {
                unTar(inputFile, outputFile);
                inputFile.delete();
            }
        }
    }

    private static void load() {
        Path filePath = Paths.get("/Users/Levent/Desktop/pseudocode/output.txt");
        Charset charset = StandardCharsets.UTF_8;
        try {
            List<String> lines = Files.readAllLines(filePath, charset);
            for (String line : lines) {
                if (line.indexOf("@-@") == -1) {
                    continue;
                }

                var parts = line.split("@-@");
                var fileName = parts[0].substring(0, parts[0].length() - 2);
                if (parts[3].equals("false")) {
                    unpseudoCodeFiles.add(fileName);
                } else if (parts[3].equals("true")) {
                    pseudoCodeFiles.add(fileName);
                } else {
                    throw new RuntimeException(line);
                }
            }
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
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
                var entryName = entry.getName()
                        .substring(entry.getName().indexOf('/') + 1,
                                entry.getName().lastIndexOf('.'));
                if (pseudoCodeFiles.contains(entryName)) {
                    final OutputStream outputFileStream = new FileOutputStream(outputFile);
                    IOUtils.copy(debInputStream, outputFileStream);
                    outputFileStream.close();
                } else if (!unpseudoCodeFiles.contains(entryName)) {
                    unmatchedList.add(entry.getName());
                }
            }
            untaredFiles.add(outputFile);
        }
        debInputStream.close();

        return untaredFiles;
    }
}
