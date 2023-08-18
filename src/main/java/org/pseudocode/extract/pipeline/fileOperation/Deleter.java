package org.pseudocode.extract.pipeline.fileOperation;

import org.apache.commons.io.FileUtils;
import org.pseudocode.extract.pipeline.model.Article;
import org.pseudocode.extract.pipeline.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

public class Deleter {

    public static void deleteArticlesWithoutPseudocode(Collection<Article> list) throws IOException {
        list = list.stream().filter(a -> !a.isHasPseudocode()).toList();
        for (var article : list) {
            deleteAllFileForArticle(article);
        }
        deleteTarFiles();
        deleteEmptyDirectories(new File(Constants.t7GcloudPath));
        deleteEmptyDirectories(new File(Constants.t7S3SrcPath));

    }

    private static void deleteTarFiles() throws IOException {
        File s3SrcDir = new File(Constants.t7S3SrcPath);
        for (File file : Objects.requireNonNull(s3SrcDir.listFiles())) {
            if (!file.getName().startsWith(".") && file.getName().endsWith(".tar")) {
                if (!file.delete()) {
                    System.err.println("Could not delete file: " + file.getCanonicalPath());
                }
            }
        }
    }

    private static void deleteEmptyDirectories(File file) {
        deleteEmptyDirectoryRecursively(file);
        file.mkdirs();
    }
    private static long deleteEmptyDirectoryRecursively(File dir) {

        File[] listFiles = dir.listFiles();
        long totalSize = 0;
        for (File file : listFiles) {
            if (file.isDirectory()) {
                totalSize += deleteEmptyDirectoryRecursively(file);
            } else {
                totalSize += file.length();
            }
        }

        if (totalSize == 0) {
            dir.delete();
        }

        return totalSize;
    }

    private static void deleteAllFileForArticle(Article article) throws IOException {
        var fileList = article.getArticleFiles()
                .stream()
                .filter(af -> af.getType() != FileType.DIR)
                .map(af -> af.getPath())
                .toList();
        for (var filePath : fileList) {
            if (!new File(filePath).delete()) {
                System.err.println("Could not delete file: " + filePath);
            }
        }

        var dirList = article.getArticleFiles()
                .stream()
                .filter(af -> af.getType() == FileType.DIR)
                .map(af -> af.getPath())
                .toList();
        for (var dirPath : dirList) {
            File dir = new File(dirPath);
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException ex) {
                FileUtils.deleteDirectory(dir);
            }
        }
    }

//    private static void deleteDirectories() throws IOException {
//        System.out.println("Start deleting directories " + new Date());
//        deleteDirectory(Constants.t7GcloudPath);
//        deleteDirectory(Constants.t7S3SrcPath);
//        deleteDirectory(Constants.t7S3pdfPath);
//        System.out.println("End deleting directories " + new Date());
//    }
//
//    private static void deleteDirectory(String directoryPath) throws IOException {
//        File directory = new File(directoryPath);
//        if (!directory.isDirectory()) {
//            throw new IllegalArgumentException(directoryPath + " is not directory");
//        }
//        try {
//            FileUtils.deleteDirectory(directory);
//        } catch (IOException ex) {
//            FileUtils.deleteDirectory(directory);
//        }
//        if (directory.exists()) {
//            throw new IOException(directoryPath + " should not exist");
//        }
//        if (!directory.mkdirs()) {
//            throw new IllegalStateException(String.format("Couldn't create directory %s.", directory.getAbsolutePath()));
//        }
//
//    }

}
