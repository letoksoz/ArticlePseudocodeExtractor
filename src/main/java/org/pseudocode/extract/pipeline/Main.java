package org.pseudocode.extract.pipeline;

import org.apache.commons.compress.archivers.ArchiveException;
import org.pseudocode.extract.pipeline.model.Article;
import org.pseudocode.extract.pipeline.fileOperation.*;

import java.io.IOException;
import java.util.*;

public class Main {


    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }

    private static Map<String, Article> articles;

    public static void main(String[] args) throws IOException, ArchiveException {

        for (int i = 0; i < YearMonthData.YEARS_MONTHS.length; i++) {
            int year = YearMonthData.YEARS_MONTHS[i][0];
            int month = YearMonthData.YEARS_MONTHS[i][1];
            process(year, month);
        }

    }

    private static void process(int year, int month) throws IOException, ArchiveException {
        System.out.println("START: " + new Date() + " for year: " + year + " and month: " + month);
        articles = new TreeMap<>();

        // Copy file from big to T7
        copyFiles(year, month);

        // open tar(s) file
        extractFiles(year, month);

        processData(year, month);

        printFileTypeCounter();

        detectPseudoCodeForArticles();

        printDetectionResult();

        createJsonFile(year, month);

        // Delete all data for the month
        Deleter.deleteArticlesWithoutPseudocode(articles.values());

        System.out.println("END: " + new Date() + " for year: " + year + " and month: " + month);
    }


    private static void printDetectionResult() {
        var list = articles.values();
        int numberOfArticles = list.size();
        list = list.stream().filter(Article::isHasPseudocode).toList();
        int numberOfPseudocode = list.size();
        int numberOfPseudocodeFromPdf = (int) list.stream()
                .filter(a -> a.getPseudocodeSourceType() == PseudocodeResourceType.PDF).count();
        int numberOfPseudocodeFromLatexBeginAlgorithm = (int) list.stream()
                .filter(a -> a.getPseudocodeSourceType() == PseudocodeResourceType.LATEX_BEGIN_ALGORITHM).count();
        int numberOfPseudocodeFromLatex = (int) list.stream()
                .filter(a -> a.getPseudocodeSourceType() == PseudocodeResourceType.LATEX).count();
        System.out.println(String.format("Total articles = %6d" +
                        " Pseudocode articles = %6d" +
                        " Pseudocode (begin_algorithm) from latex = %6d" +
                        " Pseudocode from latex = %6d" +
                        " Pseudocode from pdf = %6d",
                numberOfArticles, numberOfPseudocode, numberOfPseudocodeFromLatexBeginAlgorithm,
                numberOfPseudocodeFromLatex, numberOfPseudocodeFromPdf));
    }

    private static void detectPseudoCodeForArticles() throws IOException {
        System.out.println("Start detect pseudocode " + new Date());
        for (var article : articles.values()) {
            PseudocodeDetector.detectPseudocode(article);
        }
        System.out.println("End detect pseudocode " + new Date());
    }


    private static void createJsonFile(int year, int month) throws IOException {
        var list = articles.values().stream().sorted(Comparator.comparing(Article::getArxivId)).toList();
        JsonOperation.writeArticlesToFile(year, month, list);
    }

    private static void printFileTypeCounter() {

        System.out.println("-------- KNOWN EXTENSION ------------");
        var fileTypes = FileType.values();
        int total = 0;
        for (int i = 0; i < fileTypes.length; i++) {
            System.out.print(fileTypes[i] + " = " + FileTypeDetector.fileTypeCounter[i] + "  ");
            total += FileTypeDetector.fileTypeCounter[i];
        }
        System.out.println();

        System.out.println("-------- UNKNOWN EXTENSION ------------");
        var list = FileTypeDetector.unknowExtentionCounterMap.entrySet()
                .stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).toList();

        for (var entity : list) {
            System.out.print(entity.getKey() + " = " + entity.getValue() + "  ");
            total += entity.getValue();
        }
        System.out.println();

        System.out.println("Total number of files and directories = " + total);

        int numberOfArticles = articles.values().size();
        System.out.println(String.format("Total articles = %6d", numberOfArticles));
    }

    private static void processData(int year, int month) throws IOException {
        DataCollector.collectS3Src(year, month, articles);
        DataCollector.collectGcloud(year, month, articles);
    }

    private static void copyFiles(int year, int month) throws IOException {
        System.out.println("Start copying files " + new Date());
        Copyer.copyFromS3Src(year, month);
        Copyer.copyFromGcloud(year, month);
        System.out.println("End copying files " + new Date());
    }

    private static void extractFiles(int year, int month) throws IOException, ArchiveException {
        System.out.println("Start extracting files " + new Date());
        Extractor.unTarS3Src(year, month);
        Extractor.unGzipS3(year, month);
        // Extractor.unGzipGcloud();
        System.out.println("End extracting files " + new Date());
    }

}
