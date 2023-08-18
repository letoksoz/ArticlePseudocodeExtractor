package org.pseudocode.extract.pipeline;

import org.pseudocode.extract.pipeline.model.Article;
import org.pseudocode.extract.pipeline.fileOperation.FileType;
import org.pseudocode.extract.pipeline.fileOperation.FileTypeDetector;
import org.pseudocode.extract.pipeline.model.ArticleFile;
import org.pseudocode.extract.pipeline.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class DataCollector {
    public static void collectS3Src(int year, int month, Map<String, Article> articles) throws IOException {
        String yearMonthKey = Util.createYearMonthKey(year, month);
        File baseDirectory = new File(Constants.t7S3SrcPath + yearMonthKey);
        if (!baseDirectory.exists()) {
            return;
        }
        var list = Arrays.stream(baseDirectory.listFiles()).sorted().toList();
        for (var item : list) {
            collectInfoForFileOrDir(year, month, articles, yearMonthKey, item, "");
        }
    }

    private static void collectInfoForFileOrDir(int year, int month, Map<String, Article> articles, String yearMonthKey, File item, String idPrefix) throws IOException {
        if (item.getName().startsWith(".") || !item.getName().contains(yearMonthKey)) {
            return;
        }
        String arxivId = idPrefix + findArxivId(item, yearMonthKey);

        Article article;
        if (articles.containsKey(arxivId)) {
            article = articles.get(arxivId);
        } else {
            article = new Article(arxivId, year, month);
            articles.put(arxivId, article);
        }
        ArticleFile articleFile = createArticleFile(arxivId, item);
        article.addArticleFile(articleFile);

        if (item.isDirectory()) {
            addFilesRecursively(item, article);
        }
    }

    public static void collectGcloud(int year, int month, Map<String, Article> articles) throws IOException {
        String yearMonthKey = Util.createYearMonthKey(year, month);
        File baseDirectory = new File(Constants.t7GcloudPath);
        if (!baseDirectory.exists()) {
            return;
        }
        var list = Arrays.stream(baseDirectory.listFiles()).sorted().toList();
        for (var articleTypeDir : list) {
            if (!articleTypeDir.isDirectory()) {
                continue;
            }
            String idPrefix = "";
            if (year < 2007 || (year == 2007 && month <= 3)) {
                idPrefix = articleTypeDir.getName();
            }
            list = Arrays.stream(articleTypeDir.listFiles()).sorted().toList();
            for (var psPdfHtmlDir : list) {
                if (!articleTypeDir.isDirectory()) {
                    continue;
                }
                File directory = new File(psPdfHtmlDir.getCanonicalPath() + File.separator + yearMonthKey);
                if (!directory.isDirectory()) {
                    continue;
                }
                list = Arrays.stream(directory.listFiles()).sorted().toList();

                for (var item : list) {
                    collectInfoForFileOrDir(year, month, articles, yearMonthKey, item, idPrefix);
                }
            }
        }
    }

    private static String findArxivId(File file, String yearMonthKey) throws IOException {
        String name = file.getName();
        int index = name.indexOf(yearMonthKey);
        if (index == -1) {
            throw new RuntimeException(name + " not include " + yearMonthKey + " " + file.getCanonicalPath());
        }
        int length = yearMonthKey.length();
        if (name.charAt(index + length) == '.') {
            length++;
        }

        StringBuilder sb = new StringBuilder(name.substring(0, index + length));

        for (int i = index + length; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (!Character.isDigit(ch)) {
                break;
            }
            sb.append(ch);
        }
        if (sb.length() < 5 + index || sb.length() > 11 + index) {
            throw new RuntimeException(name + " arxiv key is strange. Key: " + sb.toString() + " " + file.getCanonicalPath());
        }
        return sb.toString();

        /*
        28976
        19986
        7509
        */
//
//        int endIndex = name.lastIndexOf('.');
//        if (endIndex == -1) {
//            return dropVersion(name);
//        } else {
//            String id  = name.substring(0,  endIndex);
//            if (id.equals(yearMonthKey)) {
//                // file without extension after 0703
//                return name;
//            }
//            return dropVersion(id);
//        }
    }

//    private static String dropVersion(String articleId) {
//        int length = articleId.length();
//        articleId = articleId.toLowerCase();
//        char ch1 = articleId.charAt(length - 2);
//        char ch2 = articleId.charAt(length - 3);
//
//        if (ch1 == 'v') {
//            return articleId.substring(0, length - 2);
//        }
//        if (ch2 == 'v') {
//            return articleId.substring(0, length - 3);
//        }
//        return articleId;
//    }

    private static void addFilesRecursively(File item, Article article) throws IOException {
        var list = Arrays.stream(item.listFiles()).sorted().toList();
        for (var file : list) {
            if (file.getName().startsWith(".")) {
                continue;
            }
            ArticleFile articleFile = createArticleFile(article.getArxivId(), file);
            article.addArticleFile(articleFile);

            if (file.isDirectory()) {
                addFilesRecursively(file, article);
            }
        }
    }

    private static ArticleFile createArticleFile(String arxivId, File item) throws IOException {
        FileType type = FileTypeDetector.detectFileType(arxivId, item);
        String path = item.getAbsolutePath();
        if (path.endsWith(FileType.CHECK_FOR_LATEX.name())) {
            path = path.substring(0,  path.length() - FileType.CHECK_FOR_LATEX.name().length() - 1);
        }

        return new ArticleFile(path, type, item.length(), findVersion(item));
    }


    private static String findVersion(File file) throws IOException {
        try {
            String fileName = file.getName();
            int length = fileName.length();
            if (!fileName.endsWith(".pdf") || length < 10) {
                return "";
            }

            fileName = fileName.toLowerCase();
            char ch1 = fileName.charAt(length - 6);
            char ch2 = fileName.charAt(length - 7);

            if (ch1 == 'v') {
                return fileName.substring(length - 6, length - 4);
            }
            if (ch2 == 'v') {
                return fileName.substring(length - 7, length - 5);
            }
            return "";
        } catch (Exception e) {
            System.err.println(file.getCanonicalPath());
            throw e;
        }
    }
}

