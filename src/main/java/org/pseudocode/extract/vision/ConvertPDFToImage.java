package org.pseudocode.extract.vision;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConvertPDFToImage {

    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }

    public static List<String> convert(String pdfName) throws IOException {
        List<String> list = new ArrayList<>();
        String pdfFileName = pdfName + ".pdf";
        PDDocument pd = PDDocument.load(new File(pdfFileName));
        PDFRenderer pr = new PDFRenderer(pd);
        for (int page = 0; page < pd.getNumberOfPages(); ++page) {
            // BufferedImage bi = pr.renderImageWithDPI (0, 300);
            BufferedImage bi = pr.renderImageWithDPI(page, 300, ImageType.RGB);
            String imageFileName = pdfName + "_page_" + page;
            ImageIO.write(bi, "JPEG", new File(imageFileName + ".jpeg"));
            list.add(imageFileName);
        }
        return list;
    }
}
