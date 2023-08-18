package org.pseudocode.extract.other;

import org.apache.commons.compress.archivers.ArchiveException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExportPseudocodeFromLatex {

    private static final String baseDirectory = "/Volumes/T7/data/";
    private static int id = 1;

    static int labelFound = 0;
    static int labelNotFound = 0;

    public static final String DELIMITER = "@DELIMITER@";

    static String patterForRef =
            "\\\\([a-z]|[A-Z])*ref\\{(:|_|-|[a-z]|[A-Z]|[0-9]| |\\+|\\)|\\(|\\.|\\*|;|,|=|\\||\\^|\\{|\\[|\\]|<|>|'|\\n|\\\\|\\/)*}";
    static String patterForFindingRef = "\\\\([a-z]|[A-Z])*ref\\{([a-z]|[A-Z])*:LABEL}";
    static String patterForLabel = "\\\\label\\{(([a-z]|[A-Z])*:LABEL|LABEL)\\}";
    static String patterForFindingLabel = "\\\\label\\{(([a-z]|[A-Z])*:([a-z]|[A-Z])*)\\}";


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
                // System.out.println(algorithm);
                String equations = updateAlgorithmWithEquations(fileContent, algorithm);
                if (algorithm.length() == 0) {
                    break;
                }
                String pseudocode = algorithm.toString();
                String converterPseudocode = LatexPseudocodeToPlainTextConverter.convert(algorithm.toString(), latexDirectoryName);
                String references = findReferences(fileContent, algorithm.toString());
                String keyWords = findKeyWords(fileContent);

                // System.out.println("ID:: " + id++);
                System.out.println("ARTICLE_ID:: " + latexDirectoryName);
                System.out.println("KEY_WORDS:: " + keyWords);
                System.out.println("PSEUDOCODE_LATEX:: " + pseudocode);
                System.out.println("PSEUDOCODE_EQUATIONS:: " + equations);
                System.out.println("PSEUDOCODE_PLAIN_TEXT:: " + converterPseudocode);
                System.out.println("PSEUDOCODE_REFERENCES:: " + references);
                System.out.println();


            }
        } catch (MalformedInputException ex) {
            // Nothing
        } catch (Exception ex) {
            System.err.println("EXCEPTION:: " + fileContent);
            ex.printStackTrace();
        }
    }

    private static String findKeyWords(String fileContent) {

        int startIndex = fileContent.indexOf("\\keywords{");
        if (startIndex != -1) {
            int endIndex = fileContent.indexOf("}", startIndex);
            return fileContent.substring(startIndex + "\\keywords{".length(), endIndex).replaceAll("\\and", "and");
        }
        String[] keys = {"IEEEkeywords", "keyword", "keyword"};
        for (var key : keys) {
            var startMatcher = findMatcher(fileContent, "begin[ ]*\\{" + key + "\\}", 0);
            if (startMatcher == null) {
                continue;
            }

            var endMatcher = findMatcher(fileContent, "end[ ]*\\{" + key + "\\}", startMatcher.end());
            if (endMatcher == null) {
                continue;
            }
            return fileContent.substring(
                    fileContent.indexOf("}", startMatcher.start()) + 1,
                    fileContent.indexOf("\\end{", startMatcher.start()));
        }
        return "";
    }

    private static String findReferences(String fileContent, String algorithm) {

        StringBuilder sb = new StringBuilder();
        var matcher = findMatcher(algorithm, patterForFindingLabel, 0);

        if (matcher == null) {
            return "";
        }

        String labelTag = matcher.group();
        //System.out.println("LABEL_TAG:: " + labelTag);
        String label = findKey(labelTag);
        //System.out.println("LABEL:: " + label);

        int currentIndex = 0;
        while (true) {
            matcher = findMatcher(fileContent, patterForFindingRef.replaceAll("LABEL", label), currentIndex);
            if (matcher == null) {
                break;
            }
            String ref = matcher.group();
            //System.out.println("REF:: " + ref);


            int range = 200;
            int startIndex = Math.max(matcher.start() - range, 0);
            int endIndex = Math.min(matcher.end() + range, fileContent.length());

            String refBody = fileContent.substring(startIndex, endIndex);
            sb.append("... ");
            sb.append(refBody);
            sb.append(" ...");
            sb.append(DELIMITER);

            currentIndex = matcher.end();
        }

        return sb.toString();
    }

    private static String clearComments(String fileContent) {
        StringBuilder sb = new StringBuilder(fileContent);
        int currentIndex = 0;
        while (true) {
            int index = sb.indexOf("%", currentIndex);
            if (index == -1) {
                break;
            }
            if (0 <= index - 1 && sb.charAt(index - 1) == '\\') {
                currentIndex = index + 1;
                continue;
            }
            int lineFeedIndex = sb.indexOf("\n", index + 1);
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

    private static String updateAlgorithmWithEquations(String fileContent, StringBuilder algorithm) {

        Set<String> set = new HashSet<>();
        List<String> list = new LinkedList<>();
        int currentIndex = 0;
        while (true) {
            if (algorithm.length() >= 10_000) {
                algorithm.delete(0, algorithm.length() - 1);
                return "";
            }
            var algorithmString = algorithm.toString();
            var matcher = findMatcher(algorithmString, patterForRef, currentIndex);
            if (matcher == null) {
                break;
            }
            String ref = matcher.group();
            String label = findKey(ref);
            label = replaceSpecialChars(label);

            // System.out.println("REF:: " + ref + " ==> " + label);
            String equation = findEquationWithLabel(fileContent, label);
            // System.out.println("equation:: " + equation);
            // algorithm.delete(matcher.start(), matcher.end());

            // algorithm.insert(matcher.start(), equation);
            // System.out.println("algorithm:: " + algorithm);

            if (!set.contains(equation)) {
                set.add(equation);
                list.add(equation);
            }

            // labelCheck(fileContent, label);

            currentIndex = matcher.end();
        }

        StringBuilder sb = new StringBuilder();
        for (var equation : list) {
            sb.append(equation);
            sb.append(DELIMITER);
        }

        return sb.toString();
    }

    private static String replaceSpecialChars(String label) {
        label = label.replace("\\", "\\\\");
        label = label.replace("{", "\\{");
        label = label.replace("[", "\\]");
        label = label.replace("]", "\\]");
        label = label.replace("*", "\\*");
        label = label.replace("(", "\\(");
        label = label.replace(")", "\\)");
        return label;
    }

    private static String findKey(String tag) {
        int index = tag.indexOf('{');
        String label = tag.substring(index + 1, tag.length() - 1);
        if (label.lastIndexOf(':') != -1) {
            label = label.substring(label.lastIndexOf(':') + 1);
        }

        return label;
    }

    private static String findEquationWithLabel(String content, String label) {
        var matcher = findMatcher(content,
                patterForLabel.replaceAll("LABEL", label), 0);

        if (matcher == null) {
            return null;
        }
        int startIndex = content.lastIndexOf("\\begin{", matcher.start());
        int endIndex = content.indexOf("\\end{", matcher.end());

        if (startIndex != -1 && endIndex != -1) {
//            int startLabelIndex = content.lastIndexOf("label{", matcher.start());
//            int endLabelIndex = content.indexOf("label{", matcher.end());
//
//            if (startLabelIndex != -1) {
//                startIndex = Math.max(startIndex, startLabelIndex);
//            }
//
//            if (endLabelIndex != -1) {
//                endIndex = Math.min(endIndex, endLabelIndex);
//            }
//
//            int index = content.indexOf("}", startIndex);
//            if (index != -1) {
//                startIndex = index + 1;
//            }
            // return content.substring(startIndex, matcher.start()) + content.substring(matcher.end(), endIndex);

            int index = content.indexOf("}", endIndex);
            if (index != -1) {
                endIndex = index + 1;
            }
            return content.substring(startIndex, endIndex);
        }
        return null;

    }

    private static void labelCheck(String fileContent, String label) {
        int currentIndex = 0;
        String simplePatter = "\\\\label\\{(:|[a-z]|[A-Z])*" + label + "\\}";

        var matcher = findMatcher(fileContent, simplePatter, currentIndex);
        if (matcher == null) {
            labelNotFound++;
        } else {
            labelFound++;
        }
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
