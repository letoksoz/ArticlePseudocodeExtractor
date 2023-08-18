package org.pseudocode.extract.other;

import org.apache.commons.compress.archivers.ArchiveException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExportPseudocodeFromLatexWithoutReferences {

    private static final String baseDirectory = "/Volumes/T7/data/";
    private static int id = 1;

    public static void main(String[] args) throws IOException, ArchiveException {
        File directory = new File(baseDirectory);
        export(directory);
    }

    private static void export(File inputFile) throws IOException, ArchiveException {
        var list = Arrays.stream(inputFile.listFiles()).sorted().toList();
        for (var mountFile : list) {
            // System.out.println(mountFile.getName());
            if (mountFile.isDirectory()) {
                var fileList = Arrays.stream(mountFile.listFiles()).sorted().toList();
                for (var latexDirectory : fileList) {
                    if (latexDirectory.isDirectory()) {
                        var latexDirectoryList = Arrays.stream(latexDirectory.listFiles()).sorted().toList();
                        for (var latexFile : latexDirectoryList) {
                            if (!latexFile.isDirectory() && latexFile.getName().endsWith(".tex")) {
                                exportPseudocode(latexDirectory.getName(), latexFile);
                            }

                        }
                    }
                }
            }

            // System.out.println("labelFound " + labelFound + " labelNotFound " + labelNotFound);
            // System.out.println("NUMBER_OF_PSEUDOCODE: " + id);
        }
    }

    private static void exportPseudocode(String latexDirectoryName, File latexFile) throws IOException {

        String fileContent = null;
        try {
            fileContent = Files.readString(latexFile.toPath());
            fileContent = clearComments(fileContent);

            int currentIndex = 0;
            while (true) {
                StringBuilder algorithm = new StringBuilder();
                currentIndex = findAlgorithm(fileContent, algorithm, currentIndex);
                if (currentIndex == -1) {
                    break;
                }
                String pseudocode = algorithm.toString();

                System.out.println("ARTICLE_ID:: " + latexDirectoryName);
                System.out.println("PSEUDOCODE_LATEX:: \n" + pseudocode);
                System.out.println();

            }
        } catch (MalformedInputException ex) {
            // Nothing
        } catch (Exception ex) {
            System.err.println("EXCEPTION:: " + fileContent);
            ex.printStackTrace();
        }
    }

    private static String clearComments(String fileContent) {
        StringBuilder sb = new StringBuilder(fileContent);
        int currentIndex = 0;
        while(true) {
            int index = sb.indexOf("%", currentIndex);
            if (index == -1) {
                break;
            }
            if (0 <= index - 1 && sb.charAt(index - 1) == '\\') {
                currentIndex = index + 1;
                continue;
            }
            int lineFeedIndex =  sb.indexOf("\n", index + 1);
            if (lineFeedIndex == -1) {
                sb.delete(index, sb.length() - 1);
                break;
            } else {
                sb.delete(index, lineFeedIndex + 1);
                currentIndex = index;
            }
        }

        return sb.toString();
    }

    private static int findAlgorithm(String fileContent, StringBuilder algorithm, int currentIndex) {
        StringBuilder sb = new StringBuilder(fileContent);
        var startMatcher = findMatcher(fileContent, "begin[ ]*\\{algorithm\\}", currentIndex);
        if (startMatcher == null) {
            return -1;
        }

        var endMatcher = findMatcher(fileContent, "end[ ]*\\{algorithm\\}", startMatcher.end());
        if (endMatcher == null) {
            return -1;
        }

        algorithm.append(fileContent.substring(startMatcher.start() - 1, endMatcher.end()));
        algorithm.append("\n\n\n\n");

        return endMatcher.end();

    }

    public static Matcher findMatcher(String text, String regex, int currentIndex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            if (matcher.start() > currentIndex) {
                return matcher;
            }
        }
        return null;
    }

}
