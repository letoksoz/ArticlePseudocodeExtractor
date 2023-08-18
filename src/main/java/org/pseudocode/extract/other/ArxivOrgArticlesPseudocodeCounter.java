package org.pseudocode.extract.other;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class ArxivOrgArticlesPseudocodeCounter {

    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }
    /*-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.NoOpLog`*/
    public static String[] algorithms = {"Algorithm 1", "algorithm 1", "Algorithm-1", "algorithm-1", "Algorithm:", "algorithm:"};
    public static String[] pseudocode = {"Pseudocode", "pseudocode", "Pseudo-code", "pseudo-code"};
    public static void main(String[] args) throws IOException {
        String directoryPath = "/Users/Levent/Desktop/ACL_Data/";
        String exportDirectoryName = "pseudocode/";
        File exportDirectory = new File(directoryPath + exportDirectoryName);
        if (!exportDirectory.exists()) {
            exportDirectory.mkdir();
        }

        File directory = new File(directoryPath);
        for (var subDirectory : directory.listFiles()) {
            if (!subDirectory.isDirectory()) {
                continue;
            }

            File exportSubDirectory = new File(directoryPath + exportDirectoryName + subDirectory.getName());
            if (!exportSubDirectory.exists()) {
                exportSubDirectory.mkdir();
            }

            int algorithmCounter = 0;
            int pseudocodeCounter = 0;
            int intersection = 0;
            int fileCounter = 0;
            var list = Arrays.stream(subDirectory.listFiles()).sorted().toList();
            for (var file : list) {
                if (!file.getName().endsWith(".pdf")) {
                    continue;
                }
                try {
                    PDDocument document = PDDocument.load(file);

                    if (!document.isEncrypted()) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        String content = stripper.getText(document);

                        fileCounter++;
                        boolean algo = contains(content, algorithms);
                        boolean pseudo = contains(content, pseudocode);
                        if (algo) {
                            algorithmCounter++;
                        }

                        if (pseudo) {
                            pseudocodeCounter++;
                        }

                        if (algo && pseudo) {
                            intersection++;
                        }

                        if (algo || pseudo) {
                            document.save(exportSubDirectory.getAbsolutePath() + "/" + file.getName());
                        }

                        if (fileCounter % 100 == 0) {
                            System.out.println("fileCounter: " + fileCounter);
                            System.out.println("algorithmCounter: " + algorithmCounter);
                            System.out.println("pseudocodeCounter: " + pseudocodeCounter);
                            System.out.println("intersection: " + intersection);
                        }
                    }
                    document.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("fileCounter: " + fileCounter);
            System.out.println("algorithmCounter: " + algorithmCounter);
            System.out.println("pseudocodeCounter: " + pseudocodeCounter);
            System.out.println("intersection: " + intersection);
        }


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
