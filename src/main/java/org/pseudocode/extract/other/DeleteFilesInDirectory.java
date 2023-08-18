package org.pseudocode.extract.other;

import java.io.File;
import java.util.Objects;

public class DeleteFilesInDirectory {
    public static void main(String[] args) {
        // Provide the directory path
        String directoryPath = "/Users/Levent/Desktop/pseudo_code_data/latex_and_pdf_4/";

        // Create a File object for the directory
        File baseDirectory = new File(directoryPath);

        // Verify that the directory exists and is a directory
        if (baseDirectory.exists() && baseDirectory.isDirectory()) {

            int counter = 0;
            int pdfCounter = 0;
            int latexCounter = 0;
            int outCounter = 0;
            int auxCounter = 0;
            int logCounter = 0;


            // Delete each file
            for (File dir : Objects.requireNonNull(baseDirectory.listFiles())) {
                if (dir.isDirectory()) {
                    for (var file : Objects.requireNonNull(dir.listFiles())) {

                        counter++;
                        if (file.getName().endsWith(".pdf")) {
                            pdfCounter++;
                            continue;
                        }
                        if (file.getName().endsWith(".tex")) {
                            latexCounter++;
                            continue;
                        }
                        if (file.getName().endsWith(".out")) {
                            outCounter++;
                            // continue;
                        }
                        if (file.getName().endsWith(".aux")) {
                            auxCounter++;
                            // continue;
                        }
                        if (file.getName().endsWith(".log")) {
                            logCounter++;
                            // continue;
                        }
                        // file.delete();
                        // System.out.println("Deleted file: " + file.getName());
                    }
                }
            }

            System.out.println("All files deleted successfully. counter: " + counter);
            System.out.println("All files counter: " + counter);
            System.out.println("pdfCounter: " + pdfCounter);
            System.out.println("latexCounter " + latexCounter);
            System.out.println("outCounter " + outCounter);
            System.out.println("auxCounter " + auxCounter);
            System.out.println("logCounter " + logCounter);
        } else {
            System.out.println("Directory not found.");
        }
        // 2103.12151_2.out
        // 2103.12151_2.aux
        // 2103.12151_2.log
        // 2103.11982_1.pdf

//        All files counter: 93511
//        pdfCounter: 12372
//        latexCounter 81139
    }
}
