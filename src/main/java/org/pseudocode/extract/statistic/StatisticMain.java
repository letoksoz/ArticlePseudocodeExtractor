package org.pseudocode.extract.statistic;

import org.apache.commons.io.FileUtils;
import org.pseudocode.extract.pipeline.Constants;
import org.pseudocode.extract.pipeline.YearMonthData;
import org.pseudocode.extract.pipeline.model.ArticleFile;
import org.pseudocode.extract.pipeline.fileOperation.FileType;
import org.pseudocode.extract.pipeline.fileOperation.JsonOperation;
import org.pseudocode.extract.pipeline.fileOperation.PseudocodeResourceType;
import org.pseudocode.extract.pipeline.model.Article;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.pseudocode.extract.pipeline.fileOperation.FileType.*;

public class StatisticMain {
    public static void main(String[] args) throws Exception {

        printStatisticData();

        // analyzeOutputFiles();

        // countTar();
    }

    private static void countTar() throws IOException {
        for (int i = 0; i < YearMonthData.YEARS_MONTHS_COMPLETED.length; i++) {
            int year = YearMonthData.YEARS_MONTHS_COMPLETED[i][0];
            int month = YearMonthData.YEARS_MONTHS_COMPLETED[i][1];

            List<Article> list = JsonOperation.readArticlesFromFile(year, month);
            long count = list.stream()
                    .flatMap(a -> a.getArticleFiles().stream())
                    .filter(af -> af.getPath().endsWith(".tar"))
                    .count();
            System.out.println(count);


        }
    }

    private static void analyzeOutputFiles() throws IOException, ParseException {
        List<String> lines = readAllLinesOfOutputs();
        lines = lines.stream()
                .filter(l -> l.startsWith("START: ") || l.startsWith("END: ")
                        || l.startsWith("Start ") || l.startsWith("End ")
                        || l.startsWith("Total number of files and directories = ")
                        || l.startsWith("Total articles ="))
                .toList();

        List<OutputFileData> list = new ArrayList<>();
        OutputFileData data = null;
        for (var line : lines) {
            if (line.startsWith("START:")) {
                //START: Thu Aug 10 16:13:04 TRT 2023 for year: 2002 and month: 1
                Date startDate = toDate(findBetween(line, "START: ", " for year: "));
                int year = Integer.parseInt(findBetween(line, " for year: ", " and month: "));
                int month = Integer.parseInt(findRemain(line, " and month: "));
                data = new OutputFileData(year, month, startDate);
            } else if (line.startsWith("END:")) {
                //END: Thu Aug 10 16:27:32 TRT 2023 for year: 2002 and month: 1
                Date endDate = toDate(findBetween(line, "END: ", " for year: "));
                int year = Integer.parseInt(findBetween(line, " for year: ", " and month: "));
                int month = Integer.parseInt(findRemain(line, " and month: "));
                data.setEnd(endDate);
                list.add(data);
                data = null;
            } else if (line.startsWith("Start copying")) {
                //Start copying files Thu Aug 10 16:13:04 TRT 2023
                Date start = toDate(findRemain(line, "Start copying files "));
                data.setStartCopying(start);
            } else if (line.startsWith("End copying")) {
                //End copying files Thu Aug 10 16:15:22 TRT 2023
                Date end = toDate(findRemain(line, "End copying files "));
                data.setEndCopying(end);
            } else if (line.startsWith("Start extracting")) {
                //Start extracting files Thu Aug 10 16:15:22 TRT 2023
                Date start = toDate(findRemain(line, "Start extracting files "));
                data.setStartExtracting(start);
            } else if (line.startsWith("End extracting")) {
                //End extracting files Thu Aug 10 16:19:06 TRT 2023
                Date end = toDate(findRemain(line, "End extracting files "));
                data.setEndExtracting(end);
            } else if (line.startsWith("Start detect")) {
                //Start detect pseudocode Thu Aug 10 16:19:11 TRT 2023
                Date start = toDate(findRemain(line, "Start detect pseudocode "));
                data.setStartDetection(start);
            } else if (line.startsWith("End detect")) {
                //End detect pseudocode Thu Aug 10 16:27:10 TRT 2023
                Date end = toDate(findRemain(line, "End detect pseudocode "));
                data.setEndDetection(end);
            } else if (line.startsWith("Total number of files and directories")) {
                //Total number of files and directories = 36191
                int totalFiles = Integer.parseInt(findRemain(line, "Total number of files and directories = "));
                data.setTotalNumberOfFilesAndDirectories(totalFiles);
            } else if (line.startsWith("Total articles")) {
                //Total articles =   2716
                try {
                    int totalArticles = Integer.parseInt(findRemain(line, "Total articles =").trim());
                    data.setTotalArticles(totalArticles);
                } catch (Exception e) {
                    // ignore
                }
            }

        }

        list = list.stream().sorted().toList();
        list.forEach(System.out::println);
        System.out.println();

        var yearlyList = new ArrayList<OutputFileDataYearly>();
        OutputFileDataYearly outputDataYearly = null;
        for (var outputData : list) {
            if (outputDataYearly == null || outputDataYearly.getYear() != outputData.getYear()) {
                outputDataYearly = new OutputFileDataYearly(outputData.getYear());
                yearlyList.add(outputDataYearly);
            }

            outputDataYearly.addData(outputData);
        }

        System.out.println("\n\n\n\n====================YEARLY DATA===================\n");
        yearlyList.forEach(System.out::println);

    }

    private static Date toDate(String dateStr) throws ParseException {
        // Thu Aug 10 16:27:32 TRT 2023
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        return sdf.parse(dateStr);
    }

    private static String findRemain(String line, String start) {
        int startIndex = line.indexOf(start) + start.length();
        return line.substring(startIndex);
    }
    private static String findBetween(String line, String start, String end) {
        int startIndex = line.indexOf(start) + start.length();
        int endIndex = line.indexOf(end);
        return line.substring(startIndex, endIndex);
    }

    private static List<String> readAllLinesOfOutputs() throws IOException {
        File resultDirectory = new File(Constants.t7GResultPath);
        var outputFilesList = Arrays.stream(resultDirectory.listFiles())
                .filter(f -> f.isFile() && f.getName().startsWith("_") && f.getName().endsWith(".txt"))
                .toList();
        outputFilesList.forEach(System.out::println);
        List<String> list = new ArrayList<>();
        for (var file : outputFilesList) {
            var lines = FileUtils.readLines(file);
            list.addAll(lines);
        }
        return list;
    }

    private static void printStatisticData() throws IOException {
        List<StatisticDataYearly> listDataYearly = new ArrayList<>();
        StatisticDataYearly dataYearly = null;
        for (int i = 0; i < YearMonthData.YEARS_MONTHS_COMPLETED.length; i++) {
            int year = YearMonthData.YEARS_MONTHS_COMPLETED[i][0];
            int month = YearMonthData.YEARS_MONTHS_COMPLETED[i][1];
            if (i == 0) {
                dataYearly = new StatisticDataYearly(year);
            } else if (YearMonthData.YEARS_MONTHS_COMPLETED[i - 1][0] != YearMonthData.YEARS_MONTHS_COMPLETED[i][0]) {
                listDataYearly.add(dataYearly);
                dataYearly = new StatisticDataYearly(year);
            }
            StatisticData data = new StatisticData(year, month);
            System.out.println("\n");
            System.out.println("STATISTIC FOR YEAR: " + year + " MONTH: " + month);
            System.out.println("START " + new Date());
            List<Article> list = JsonOperation.readArticlesFromFile(year, month);
            statisticForMonth(data, list);
            System.out.println(data);
            System.out.println("END " + new Date());
//        printArticleIds(list);
//        printUniqueFilePaths(list);
//        printUniqueFileNames(list);
            dataYearly.addStatisticData(data);

        }
        listDataYearly.add(dataYearly);
        printDataYearly(listDataYearly);
        System.out.println("\n\n\n\n====================TOTAL DATA===================\n");
        System.out.println(new StatisticDataTotal(listDataYearly));
    }

    private static void printDataYearly(List<StatisticDataYearly> listDataYearly) {
        System.out.println("\n\n\n\n====================YEARLY DATA===================\n");
        listDataYearly.forEach(System.out::println);
    }

    private static void printUniqueFileNames(List<Article> list) {
        var set = list.stream().flatMap(a -> a.getArticleFiles().stream()).map(af ->
                af.getPath().substring(
                        af.getPath().lastIndexOf('/'))
        ).collect(Collectors.toSet());
        // System.out.println(set);
        System.out.println(set.size());
    }

    private static void printUniqueFilePaths(List<Article> list) {
        var set = list.stream().flatMap(a -> a.getArticleFiles().stream()).map(ArticleFile::getPath
        ).collect(Collectors.toSet());
        // System.out.println(set);
        System.out.println(set.size());
    }

    private static void printArticleIds(List<Article> list) {
        var set = list.stream().map(Article::getArxivId).collect(Collectors.toSet());
        System.out.println(set);
        System.out.println(set.size());
    }

    private static void statisticForMonth(StatisticData data, List<Article> list) throws IOException {
        data.setNumOfArticles(list.size());

        for (var article : list) {
            data.addTopics(article.getTopic());
        }

        FileCounterData fileCounterData = pdfLatexFileCounter(list);
        data.setArticlesFileCounterData(fileCounterData);

        list = list.stream().filter(Article::isHasPseudocode).toList();

        for (var article : list) {
            data.addPseudocodeTopicsMap(article.getTopic());
        }

        // System.out.println();
        // System.out.println("Number of articles has pseudocode is " + list.size());
        data.setNumOfPseudocodeArticles(list.size());

        Map<PseudocodeResourceType, Long> map = list.stream()
                .map(Article::getPseudocodeSourceType)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        for (var entity : map.entrySet()) {
            // System.out.print(entity.getKey() + " - "  + entity.getValue() + " / ");
            data.addPseudocodeSource(entity.getKey(), entity.getValue().intValue());
        }
        System.out.println();

        int pseudocodeHasHtmlCounter = (int) list.stream().filter(a -> a.getArticleFiles().stream().anyMatch(af -> af.getPath().contains("/html/"))).count();
        data.setNumOfPseudocodeArticlesHasHtml(pseudocodeHasHtmlCounter);
        // System.out.println("Number of article with pseudocode has html is: " + pseudocodeHasHtmlCounter);

        fileCounterData = pdfLatexFileCounter(list);
        data.setPseudocodeArticlesFileCounterData(fileCounterData);
    }

    private static FileCounterData pdfLatexFileCounter(List<Article> list) {
        long countPdfAndLatex = list.stream().filter(a -> {
            var set = findFileTypeSet(a);
            return set.contains(PDF) &&
                    (set.contains(LATEX) || set.contains(TEX) || set.contains(LATEX_WITHOUT_EXTENSION));
        }).count();
        // System.out.println("Number of article has pdf and latex: " + countPdfAndLatex);

        long countNotPdfAndNotLatex = list.stream().filter(a -> {
            var set = findFileTypeSet(a);
            return !set.contains(PDF) &&
                    (!set.contains(LATEX) && !set.contains(TEX) && !set.contains(LATEX_WITHOUT_EXTENSION));
        }).count();
        // System.out.println("Number of article has not pdf and latex: " + countNotPdfAndNotLatex);

        long countPdfAndNotLatex = list.stream().filter(a -> {
            var set = findFileTypeSet(a);
            return set.contains(PDF) &&
                    (!set.contains(LATEX) && !set.contains(TEX) && !set.contains(LATEX_WITHOUT_EXTENSION));
        }).count();
        // System.out.println("Number of article has pdf but has not latex: " + countPdfAndNotLatex);

        long countNotPdfButHasLatex = list.stream().filter(a -> {
            var set = findFileTypeSet(a);
            return !set.contains(PDF) &&
                    (set.contains(LATEX) || set.contains(TEX) || set.contains(LATEX_WITHOUT_EXTENSION));
        }).count();
        // System.out.println("Number of article has not pdf but has latex: " + countNotPdfAndLatex);

        return new FileCounterData((int) countPdfAndLatex, (int) countNotPdfAndNotLatex,
                (int) countPdfAndNotLatex, (int) countNotPdfButHasLatex);
    }

    private static Set<FileType> findFileTypeSet(Article article) {
        return article.getArticleFiles()
                .stream()
                .map(ArticleFile::getType)
                .collect(Collectors.toSet());
    }
}
