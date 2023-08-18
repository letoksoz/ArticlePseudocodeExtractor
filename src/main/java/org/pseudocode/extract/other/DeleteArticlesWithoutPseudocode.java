package org.pseudocode.extract.other;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DeleteArticlesWithoutPseudocode {

    private static final String directory = "/Volumes/T7/data/";

    public static void main(String[] args) {
        Path filePath = Paths.get("/Users/Levent/Desktop/output.txt");
        Charset charset = StandardCharsets.UTF_8;
        try {
            List<String> lines = Files.readAllLines(filePath, charset);
            int counter = 0;
            int deletedCounter = 0;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                try {
                    counter++;
                    var parts = line.split("@-@");
                    if (parts[3].equals("false")) {
                        File file = new File(directory + parts[2] + "/" + parts[0] + ".pdf");
                        if (file.exists()) {
                            file.delete();
                            deletedCounter++;
                        } else {
                            System.out.println("YOK: " + file.getAbsolutePath());
                        }
                    }
                    if (counter % 1000 == 0) {
                        System.out.println("Lines: " + counter + " deleted: " + deletedCounter);
                    }
                } catch (Exception ex) {
                    System.out.println("Line: " + line);
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }
    }
}
