package org.pseudocode.extract.other;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DeleteImageFilesInDirectory {
    public static void main(String[] args) throws IOException {
        // Provide the directory path
        String directoryPath = "/Volumes/T7/data";

        // Create a File object for the directory
        File baseDirectory = new File(directoryPath);

        // Verify that the directory exists and is a directory
        if (baseDirectory.exists() && baseDirectory.isDirectory()) {

            int counter = 0;
            int pdfCounter = 0;
            int imageCounter = 0;

            // Delete each file
            for (File dir : Objects.requireNonNull(baseDirectory.listFiles())) {
                if (dir.isDirectory()) {
                    System.out.println(dir.getCanonicalPath());
                    for (var file : Objects.requireNonNull(dir.listFiles())) {

                        counter++;
                        if (file.getName().endsWith(".pdf")) {
                            pdfCounter++;
                            continue;
                        }
                        if (file.getName().endsWith(".jpeg")) {
                            imageCounter++;
                        }
                        file.delete();
                        // System.out.println("Deleted file: " + file.getName());
                    }
                }
            }

            System.out.println("All files counter: " + counter);
            System.out.println("pdfCounter: " + pdfCounter);
            System.out.println("imageCounter " + imageCounter);
        } else {
            System.out.println("Directory not found.");
        }
    }
}
