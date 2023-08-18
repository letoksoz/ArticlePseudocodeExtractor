package org.pseudocode.extract;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;


public class PdfPseudocodeTester {

    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }


    private static String[] algorithms = {"Algorithm 1", "algorithm 1", "Algorithm-1", "algorithm-1", "Algorithm:", "algorithm:"};
    private static String[] pseudocode = {"Pseudocode", "pseudocode", "Pseudo-code", "pseudo-code"};

    public static void main(String[] args) throws IOException {
        int counter = 0;
        File baseDirectory = new File("/Volumes/T7/data/");
        for (var dataDir : baseDirectory.listFiles()) {
            if (dataDir.isFile()) {
                continue;
            }
            for (var pdfFile : dataDir.listFiles()) {
                if (!pdfFile.isFile() || !pdfFile.getName().endsWith(".pdf")) {
                    continue;
                }
                counter++;
                if (counter % 100 == 0) {
                    System.out.println(counter);
                }
                if (pdfContainsAlgorithm(pdfFile) == false) {
                    System.err.println(pdfFile.getCanonicalPath());
                }
            }
        }
    }


    private static boolean pdfContainsAlgorithm(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            return isIncludePseudocode(readAllContent(document));
        } catch (IOException ex) {
            System.out.println("ERROR:: pdf could not read. PFD: " + file.getCanonicalPath() + " error message: " + ex.getMessage());
        }
        return false;
    }

    private static String readAllContent(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }

    private static boolean isIncludePseudocode(String content) {
        return contains(content, algorithms) || contains(content, pseudocode);
    }


    public static boolean contains(String content, String[] words) {
        for(var word : words) {
            if (content.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
