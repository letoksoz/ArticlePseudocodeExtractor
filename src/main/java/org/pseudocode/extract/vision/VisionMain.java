package org.pseudocode.extract.vision;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VisionMain {

    private static String path = "/Volumes/T7/data";
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        String fileName = "";
        File directory = new File(path);
        var list = Arrays.stream(directory.listFiles()).sorted().toList();
        for (var subDirectory : list) {
            if (!subDirectory.isDirectory()) {
                continue;
            }
            list = Arrays.stream(subDirectory.listFiles()).sorted().toList();
            for (var pdfFile : list) {
                try {
                    fileName = pdfFile.getName();
                    if (fileName.startsWith("._") || !fileName.endsWith(".pdf")) {
                        continue;
                    }
                    String pdfName = fileName.substring(0, fileName.length() - 4);
                    Runnable articleProcessor = new ArticleProcessor(pdfFile.getParent() + File.separator +  pdfName);
                    executor.execute(articleProcessor);
                    // processArticle(pdfFile.getParent() + File.separator +  pdfName);
                } catch (Exception ex) {
                    System.out.println("EXCEPTION:: " + ex.getMessage());
                }
            }
        }
    }

    public static void processArticle(String pdfName) throws Exception {
        var list = ConvertPDFToImage.convert(pdfName);
        // System.out.println(list);

        TeseractExtractor.executeTesseractCommand(list);

        TeseractExtractor.replaceDTD(list);

        var ignoreSet = PseudoCodeImageExtractor.extract(list);

        deleteUnneededFiles(list, ignoreSet);

        // convert pseudocode image to latex

        // run grobit for the article or use from map

        // index to ES

        // comnvert latext to python code
    }

    private static void deleteUnneededFiles(List<String> list, Set<String> ignoreSet) {
        for(var fileName : list) {
            if (!ignoreSet.contains(fileName)) {
                new File(fileName + ".jpeg").delete();
            }
            new File(fileName + ".hocr").delete();
        }
    }
}

class ArticleProcessor implements  Runnable {

    private final String fileToProcess;

    ArticleProcessor(String fileToProcess) {
        this.fileToProcess = fileToProcess;
    }

    @Override
    public void run() {
        try {
            VisionMain.processArticle(fileToProcess);
        } catch (Exception e) {
            System.out.println(fileToProcess);
            e.printStackTrace();
        }
    }
}
