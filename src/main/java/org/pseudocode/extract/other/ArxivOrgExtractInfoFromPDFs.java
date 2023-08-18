package org.pseudocode.extract.other;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class ArxivOrgExtractInfoFromPDFs {

    // fıle name, article name, year_month, main topic, subtopic include pseudocode,
    // 48.27 GB available space on T7



    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }

    private static String[] algorithms = {"Algorithm 1", "algorithm 1", "Algorithm-1", "algorithm-1", "Algorithm:", "algorithm:"};
    private static String[] pseudocode = {"Pseudocode", "pseudocode", "Pseudo-code", "pseudo-code"};

    private static String seperator = "@-@";

    public static void main(String[] args) {
        String directoryPath = "/Volumes/T7/data";
        String fileName = "";
        File directory = new File(directoryPath);
        var list = Arrays.stream(directory.listFiles()).sorted().toList();
        for (var subDirectory : list) {
            if (!subDirectory.isDirectory()) {
                continue;
            }
            list = Arrays.stream(subDirectory.listFiles()).sorted().toList();
            for (var pdfFile : list) {
                try {
                    fileName = pdfFile.getName();
                    if (fileName.startsWith("._")) {
                        continue;
                    }
                    fileName = fileName.substring(0, fileName.length() - 4);
                    String version = fileName.substring(fileName.length() - 2);
                    PDDocument document = PDDocument.load(pdfFile);
                    String firstPageContent = readFirstPage(document);

                    String articleName = extractArticleName(firstPageContent);
                    String topic = extractTopic(firstPageContent, fileName);
                    String[] topicArray = topic.split("\\.");
                    String yearMonth = fileName.split("\\.")[0];

                    boolean pseudocode = isIncludePseudocode(readAllContent(document));

                    System.out.println(fileName + seperator + version + seperator + yearMonth + seperator + pseudocode + seperator
                            + topicArray[0] + seperator+ (topicArray.length < 2 ? "" : topicArray[1])
                            + seperator + articleName + "," + new Date());
                    fileName = "";
                } catch (IOException e) {
                    System.out.println(fileName);
                    e.printStackTrace();
                } catch (Exception ex) {
                    System.out.println("EXCEPTION:: " + ex.getMessage());
                }
            }
        }

    }

    private static String extractArticleName(String firstPageContent) {
        var array = firstPageContent.split("\\r?\\n");
        if (array.length == 0) {
            return "";
        }
        return array[0];
    }

    private static String extractTopic(String firstPageContent, String fileName) {
        firstPageContent = String.join("",  firstPageContent.split("\\r?\\n"));
        int fileNameIndex = firstPageContent.lastIndexOf(fileName);
        if (fileNameIndex == -1 ) {
            return "";
        }

        int i1 = firstPageContent.indexOf('[', fileNameIndex);
        if (i1 == -1 ) {
            return "";
        }
        int i2 = firstPageContent.indexOf(']', i1);
        if (i2 == -1 || i2 <= i1) {
            return "";
        }
        return firstPageContent.substring(i1 + 1, i2);
    }

    private static String readFirstPage(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(1);
        return stripper.getText(document);
    }

    private static String readAllContent(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }

    private static boolean isIncludePseudocode(String pdfContent) {
        return contains(pdfContent, algorithms) || contains(pdfContent, pseudocode);
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
