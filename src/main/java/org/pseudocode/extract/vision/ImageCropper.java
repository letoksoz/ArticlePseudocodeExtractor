package org.pseudocode.extract.vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageCropper {
    public static void crop(String imageName, String outputImageName,
                            int x1, int y1, int x2, int y2) throws IOException {

        // Reading original image from local path by
        // creating an object of BufferedImage class
        BufferedImage originalImg = ImageIO.read(
                new File(imageName));

        // Fetching and printing alongside the
        // dimensions of original image using getWidth()
        // and getHeight() methods
        System.out.println("Original Image Dimension: "
                + originalImg.getWidth()
                + "x"
                + originalImg.getHeight());

        // Creating a subimage of given dimensions
        BufferedImage SubImg
                = originalImg.getSubimage(x1, y1, x2 - x1, y2 - y1);

        // Printing Dimensions of new image created
        System.out.println("Cropped Image Dimension: "
                + SubImg.getWidth() + "x"
                + SubImg.getHeight());

        // Creating new file for cropped image by
        // creating an object of File class
        File outputfile = new File(outputImageName);

        // Writing image in new file created
        ImageIO.write(SubImg, "jpeg", outputfile);

        // Display message on console representing
        // proper execution of program
        System.out.println(
                "Cropped Image created successfully");
    }
}
