package org.pseudocode.extract.other;

import java.io.*;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class CreateLatexToConvertToPdf {
    private static final String baseDirectory = "/Volumes/T7/data/";
    private static final String outputDirectory = "/Users/Levent/Desktop/pseudo_code_data/latex_and_pdf_4/";
    private static Set<String> yearSet = new HashSet<>();

    public static void main(String[] args) {
        File file = new File("/Users/Levent/Desktop/pseudo_code_data/" +
                "pseudocode_latex_with_references_and_equations_to_convert_pdf.txt");
        String fileContent = null;
        try {
            System.out.println("File read begin " + new Date());
            fileContent = Files.readString(file.toPath());
            System.out.println("File read end " + new Date());
            convertToPdf(fileContent);
        } catch (Exception ex) {
            System.err.println("EXCEPTION:: ");
            ex.printStackTrace();
        }
    }


    private static void convertToPdf(String fileContent) throws IOException, InterruptedException {
        String headerUp = """
                \\documentclass{article}
                                
                                
                \\usepackage{hyperref}
                \\usepackage{graphicx}
                \\usepackage{caption}
                \\usepackage{subcaption}
                """;
        String headerDown = """
                \\usepackage{amsmath}
                \\usepackage[capitalise]{cleveref}
                \\usepackage{todonotes}
                \\usepackage{enumitem}
                \\usepackage{eqnarray}
                                
                \\usepackage{array}
                \\usepackage{amsmath,amssymb,amsthm}
                \\usepackage{booktabs}
                \\usepackage{graphicx,stfloats}
                \\usepackage{lineno}
                \\usepackage{multirow}
                \\usepackage[version=4, arrows=pgf]{mhchem}
                \\usepackage{url}
                \\usepackage{xspace}
                \\usepackage{hyperref}
                \\usepackage{hyphenat}
                                
                \\usepackage{times}
                \\usepackage{latexsym}
                \\usepackage{mwe}
                                
                \\usepackage{booktabs}
                \\usepackage{comment}
                \\usepackage{microtype}
                \\usepackage{url}
                                 
                \\date{}
                                
                \\begin{document}
                """;
        String footer = "\\end{document}";
        String algorithmicxImport =
                "\\usepackage{algorithm,algorithmicx}\n" +
                        "\\usepackage{algpseudocode}";
        String algorithmicImport =
                "\\usepackage{algorithmic}\n" +
                        "\\usepackage{algorithm}";


        String[] algorithms = fileContent.split("ARTICLE_ID:: ");
        System.out.println("parsing begin " + new Date());
        System.out.println("size: " + algorithms.length);

        int counter = 0;
        int unprocesFileCounter = 0;
        int procesFileCounter = 0;
        Map<String, Integer> map = new HashMap<>();
        for (String algorithm : algorithms) {
            try {
                if (algorithm.length() == 0) {
                    continue;
                }
                var parts = algorithm.split("KEY_WORDS:: ");
                String articleId = parts[0].trim();
                parts = parts[1].split("PSEUDOCODE_LATEX:: ");
                String keyWords = parts[0].trim();
                parts = parts[1].split("PSEUDOCODE_EQUATIONS:: ");
                String pseudocodeLatex = parts[0].trim();
                parts = parts[1].split("PSEUDOCODE_PLAIN_TEXT:: ");
                String equations = parts[0].trim();

//
//            if (pseudocodeLatex.length() > 10000) {
//                counter++;
//                System.out.println("COUNTER: " + counter);
//                System.out.println(pseudocodeLatex);
//                System.out.println();
//                System.out.println();
//                System.out.println("-----------------------------------");
//                System.out.println();
//                System.out.println();
//            }


                if (pseudocodeLatex.contains("\\Kw") || pseudocodeLatex.length() < 100 || pseudocodeLatex.length() > 10000) {
                    unprocesFileCounter++;
                    continue;
                }

                int dotIndex = articleId.indexOf('.');
                String year = articleId.substring(0, dotIndex);
                File latexDirectory = new File(baseDirectory + year + '/' + articleId);
                boolean useAlgorithmicxTemplate = false;

                for (var latexFile : latexDirectory.listFiles()) {
                    if (latexFile.getName().endsWith(".tex") && !latexFile.getName().startsWith(".")) {
                        // System.out.println(latexFile.getName());
                        String latex = null;
                        try {
                            latex = Files.readString(latexFile.toPath());
                            if (latex.contains("algorithmicx") || latex.contains("algpseudocode")) {
                                useAlgorithmicxTemplate = true;
                                break;
                            }
                        } catch (MalformedInputException e) {
                            // Nothing
                        } catch (IOException e) {
                            System.err.println("EXCEPTION:: ");
                            e.printStackTrace();
                        }
                    }
                }

                StringBuilder document = new StringBuilder();
                document.append(headerUp);
                if (useAlgorithmicxTemplate) {
                    document.append(algorithmicxImport);
                } else {
                    document.append(algorithmicImport);
                }
                document.append(headerDown);
                document.append(pseudocodeLatex);
                document.append(equations.replaceAll(ExportPseudocodeFromLatex.DELIMITER, " "));
                document.append(footer);

                int sequance;
                if (map.containsKey(articleId)) {
                    sequance = map.get(articleId);
                    sequance++;
                } else {
                    sequance = 1;
                }
                map.put(articleId, sequance);

                if (!yearSet.contains(year)) {
                    File dir = new File(outputDirectory + year + "/");
                    if (!dir.exists()) {
                        boolean success = dir.mkdirs();
                        if (!success) {
                            throw new RuntimeException("Can not create directory: " + outputDirectory + year + "/");
                        }
                    }
                    yearSet.add(year);
                    System.out.println(year);
                }

                String fileName = outputDirectory + year + "/" + articleId + "_" + sequance + ".tex";
                File outputFile = new File(fileName);
                Files.writeString(outputFile.toPath(), document.toString(), StandardCharsets.UTF_8);
                procesFileCounter++;


//
//            String[] arguments = new String[] {"pdflatex", "-output-directory", "\"" + outputDirectory + "\"", "\"" + fileName + "\""};
//            Process proc = new ProcessBuilder(arguments).start();
//
////            String command = "/bin/bash /Library/TeX/texbin/pdflatex -output-directory  \"" + outputDirectory + "\" \"" + fileName + "\"";
////            var proc = Runtime.getRuntime().exec(command);
////            System.out.println(command);
//
//            BufferedReader stdInput = new BufferedReader(new
//                    InputStreamReader(proc.getInputStream()));
//
//            BufferedReader stdError = new BufferedReader(new
//                    InputStreamReader(proc.getErrorStream()));
//
//            // Read the output from the command
//            System.out.println("Here is the standard output of the command:\n");
//            String s = null;
//            while ((s = stdInput.readLine()) != null) {
//                System.out.println(s);
//            }
//
//            // Read any errors from the attempted command
//            System.out.println("Here is the standard error of the command (if any):\n");
//            while ((s = stdError.readLine()) != null) {
//                System.out.println(s);
//            }
//
//            while (proc.isAlive()) {
//                Thread.sleep(2000);
//            }
//            System.out.println(proc);
//            System.out.println(proc.exitValue());
//


                // System.out.println(articleId);
                // System.out.println(document);
                // lrwxr-xr-x  1 root  wheel         6 Dec 17  2020 pdflatex -> pdftex
                // lrwxr-xr-x  1 root  wheel         6 Dec 17  2020 pdflatex -> pdftex
            } catch (Exception e) {
                e.printStackTrace();
                unprocesFileCounter++;
            }

        }

        System.out.println(unprocesFileCounter);
        System.out.println(procesFileCounter);
    }
}
