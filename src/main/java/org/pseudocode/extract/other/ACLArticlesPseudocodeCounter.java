package org.pseudocode.extract.other;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ACLArticlesPseudocodeCounter {
    public static void main(String[] args) throws IOException {
        String directoryPath = "/Users/Levent/Desktop/ACL_Data";
        String grobidPath = "/grobid_full_text/";

        File directory = new File(directoryPath + grobidPath);

        int algorithmCounter = 0;
        int pseudocodeCounter = 0;
        int intersection = 0;
        int fileCounter = 0;
        for (var fileName : directory.list()) {
            var content = Files.readString(Paths.get( directoryPath + grobidPath + fileName));
            fileCounter++;
            boolean algo = ArxivOrgArticlesPseudocodeCounter.contains(content, ArxivOrgArticlesPseudocodeCounter.algorithms);
            boolean pseudo = ArxivOrgArticlesPseudocodeCounter.contains(content, ArxivOrgArticlesPseudocodeCounter.pseudocode);
            if (algo) {
                System.out.println("Algorithm: " + fileName);
                algorithmCounter++;
            }

            if (pseudo) {
                System.out.println("Pseudocode: " + fileName);
                pseudocodeCounter++;
            }

            if (algo && pseudo) {
                intersection++;
            }

            if (fileCounter % 100 == 0) {
                System.out.println("fileCounter: " + fileCounter);
                System.out.println("algorithmCounter: " + algorithmCounter);
                System.out.println("pseudocodeCounter: " + pseudocodeCounter);
                System.out.println("intersection: " + intersection);
            }
        }
        System.out.println("fileCounter: " + fileCounter);
        System.out.println("algorithmCounter: " + algorithmCounter);
        System.out.println("pseudocodeCounter: " + pseudocodeCounter);
        System.out.println("intersection: " + intersection);
    }
}
