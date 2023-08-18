package org.pseudocode.extract.other;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFToImageConverter {

    // Provide the directory path
    private static String directoryPath = "/Users/Levent/Desktop/pseudo_code_data/latex_and_pdf_4/";
    public static void main(String[] args) {

        // Create a File object for the directory
        File baseDirectory = new File(directoryPath);

        // Verify that the directory exists and is a directory
        if (baseDirectory.exists() && baseDirectory.isDirectory()) {

            int counter = 0;
            for (File dir : Objects.requireNonNull(baseDirectory.listFiles())) {
                if (dir.isDirectory()) {
                    for (var file : Objects.requireNonNull(dir.listFiles())) {
                        if (file.isFile()) {
                            if (file.getName().endsWith(".pdf")) {
                                counter++;
                                convertToImage(file);
                            }
                        }
                    }
                }
            }
            System.out.println("Converted pdf counter is " + counter);
        } else {
            System.out.println("Directory not found.");
        }
    }


    private static void convertToImage(File file) {

        try {
            // Load the PDF document
            PDDocument document = PDDocument.load(file);
            String canonicalPath = file.getCanonicalPath();
            canonicalPath = canonicalPath.substring(0, canonicalPath.indexOf(".pdf"));

            // Create the PDFRenderer object
            PDFRenderer renderer = new PDFRenderer(document);

            // Iterate over each page and convert it to an image
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                // Render the page as an image
                BufferedImage image = renderer.renderImageWithDPI(pageIndex, 300);

                // Specify the output file path
                String outputFilePath = canonicalPath + "_page_" + (pageIndex + 1) + ".png";
                System.out.println(outputFilePath);

                // Save the image to the output file
                ImageIO.write(image, "png", new File(outputFilePath));

                // System.out.println("Page " + (pageIndex + 1) + " converted to image.");
            }

            // Close the document
            document.close();

            // System.out.println("PDF to image conversion completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
