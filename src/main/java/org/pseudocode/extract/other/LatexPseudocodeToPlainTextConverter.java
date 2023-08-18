package org.pseudocode.extract.other;

import java.util.List;

public class LatexPseudocodeToPlainTextConverter {

    private static String[] ignoreLinesStart = {"\\begin{algorithm}", "\\end{algorithm}", "\\label{", "\\Set",
            "\\DontPrintSemicolon"};

    private static String[] blockStarter = {"\\if", "\\eif",  "\\uif",  "\\while",  "\\for", "\\switch", "\\case", "\\Function"};
    private static String[] blockFinalizer = {"\\endif", "\\endwhile",  "\\endfor", "\\endswitch", "\\endcase", "\\endfunction"};

    private static String[] cleanTag = {"\\text{", "\\textbf{", "\\textit{", "\\texttt{",};

    public static String convert(String pseudocode, String arxivName) {
        var lines = List.of(pseudocode.split("\n"));
        pseudocode = pseudocode.toLowerCase();

        StringBuilder sb = new StringBuilder();
        int lineCounter = 0;
        int indentCounter = 0;
        String convertedLine = "";
        boolean countBrackets = false;

        for (var line : lines) {

            line = line.trim();

            boolean converted = true;
            boolean intentFound = false;

            if (line.startsWith("\\caption{")) {
                convertedLine = line.substring(1);
            } else if (line.startsWith("\\begin{algorithmic}")) {
                //convertedLine = line.substring(1);
                continue;
            } else if (line.startsWith("\\end{algorithmic}")) {
                // convertedLine = line.substring(1);
                continue;
            } else if (isStartWithBlockStarter(line.toLowerCase())) {
                if (containsBlockFinalizer(pseudocode)) {
                    countBrackets = false;
                    intentFound = true;
                } else {
                    countBrackets = true;
                    intentFound = false;
                }
                convertedLine = line.substring(1);
                convertedLine = convertLine(convertedLine);
            } else if (isStartWithBlockFinalizer(line.toLowerCase())) {
                convertedLine = line.substring(1);
                if (!countBrackets) {
                    indentCounter--;
                }
            } else if (line.startsWith("\\State") || line.startsWith("\\STATE")) {
                convertedLine = line.substring(6);
                convertedLine = convertLine(convertedLine);
            } else if (line.startsWith("\\Kw")) {
                convertedLine = line.substring(3);
                convertedLine = convertLine(convertedLine);
            } else {
                if (isStartWithIgnoreLine(line)) {
                    converted = false;
                } else {
                    convertedLine = convertLine(line);
                }
            }

            if (countBrackets) {
                indentCounter += countBrackets(line);
            }

            if (converted) {
                /*
                sb.append(lineCounter++);
                sb.append("\t");
                sb.append(indentCounter);
                sb.append("\t");
                sb.append(arxivName);
                sb.append("\t");

                 */
                sb.append(convertedLine);
                sb.append("\n");
            }

            if (intentFound) {
                indentCounter++;
            }
        }
        return sb.toString();
    }

    private static int countBrackets(String line) {
        int count = 0;

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '{' && (i + 1 < line.length() && line.charAt(i + 1) != '%')) {
                count++;
            } else if (line.charAt(i) == '}' && (i - 1 >= 0 && line.charAt(i - 1) != '%')) {
                count--;
            }
        }

        return count;
    }

    private static boolean containsBlockFinalizer(String pseudocode) {
        for (var finalizer : blockFinalizer) {
            if (pseudocode.contains(finalizer)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isStartWithBlockFinalizer(String line) {
        for (var finalizer : blockFinalizer) {
            if (line.startsWith(finalizer)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isStartWithBlockStarter(String line) {
        for (var starter : blockStarter) {
            if (line.startsWith(starter)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isStartWithIgnoreLine(String line) {
        for (var ignoreLine : ignoreLinesStart) {
            if (line.startsWith(ignoreLine)) {
                return true;
            }
        }
        return false;
    }

    private static String convertLine(String convertedLine) {
        convertedLine = cleanTag(convertedLine);

        int currentPosition = 0;
        StringBuilder sb = new StringBuilder();
        while(true) {
            int startIndex = convertedLine.indexOf('$', currentPosition);
            if (startIndex == -1) {
                sb.append(convertedLine.substring(currentPosition));
                break;
            }
            if (startIndex != 0 && convertedLine.charAt(startIndex - 1) == '\\') {
                sb.append(convertedLine.substring(currentPosition, startIndex + 1));
                currentPosition = startIndex + 1;
                continue;
            }

            sb.append(convertedLine.substring(currentPosition, startIndex));

            int endIndex = convertedLine.indexOf('$', startIndex + 1);

            while (endIndex != -1 && convertedLine.charAt(endIndex - 1) == '\\') {
                endIndex = convertedLine.indexOf('$', endIndex + 1);
            }
            if (endIndex != -1) {
                String convertedEquation = convertEquations(convertedLine.substring(startIndex + 1, endIndex));
                sb.append(convertedEquation);
                currentPosition = endIndex + 1;
            } else {
                String convertedEquation = convertEquations(convertedLine.substring(startIndex + 1));
                sb.append(convertedEquation);
                break;
            }


        }

        replaceAll(sb, "\\;", ";");
        replaceAll(sb, "\\end{algorithm}", "");

        return sb.toString();

    }

    private static String convertEquations(String substring) {
        StringBuilder sb = new StringBuilder(substring);
/*
        int index;
        while ((index = sb.indexOf("\\")) != -1) {
            sb.setCharAt(index, ' ');
        }

 */

        return sb.toString();
    }

    private static String cleanTag(String line) {
        StringBuilder sb = new StringBuilder(line);
        for (var tag : cleanTag) {
            int index = sb.indexOf(tag);
            while (index != -1) {
                sb.delete(index, index + tag.length());
                index = sb.indexOf("}", index);
                if (index != -1) {
                    sb.deleteCharAt(index);
                }
                index = sb.indexOf(tag);
            }
        }
        return sb.toString();
    }

    public static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }
}
