package org.pseudocode.extract.other;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenRandomFiles {
    static int fileCounter = 0;
    public static void main(String[] args) throws IOException, InterruptedException {
        int n = 25;

        openRandomAlgorithmImages(n);
        // openRandomAlgorithmPDFs(n);
    }

    private static void openRandomAlgorithmPDFs(int n) throws IOException, InterruptedException {
        String baseDirectoryPath = "/Users/Levent/Desktop/pseudo_code_data/latex_and_pdf";

        String extension = ".pdf";
        String contains = "";

        openRandomFiles(baseDirectoryPath, extension, contains, n);
    }

    private static void openRandomAlgorithmImages(int n) throws IOException, InterruptedException {
        String baseDirectoryPath = "/Volumes/T7/data/2011";

        String extension = ".jpeg";
        String contains = "_algo_";

        openRandomFiles(baseDirectoryPath, extension, contains, n);
    }

    private static void openRandomFiles(String baseDirectoryPath, String extension, String contains, int n) throws IOException, InterruptedException {
        File baseDirectory = new File(baseDirectoryPath);
        List<File> fileList = new ArrayList<>();
        findFileList(fileList, baseDirectory, extension, contains, 0);
        System.out.println("Size: " + fileList.size());
        openRandomFiles(fileList, n);
    }

    private static void openRandomFiles(List<File> fileList, int n) throws IOException, InterruptedException {
        for (int i = 0; i < n; i++) {
            File file = fileList.get((int) (Math.random() * fileList.size()));
            fileList.remove(file);
            Desktop.getDesktop().open(file.getAbsoluteFile());
            Thread.sleep(2000);

        }

    }

    private static void findFileList(List<File> fileList, File baseDirectory, String extension, String contains, int deepLevel) {
        for (var file : baseDirectory.listFiles()) {
            fileCounter++;
            if (fileCounter % 10_000 == 0) {
                System.out.println("File Size: " + fileCounter);
                System.out.println("Image Size: " + fileList.size());

            }
            if (file.isDirectory() && deepLevel < 1) {
                findFileList(fileList, file, extension, contains, deepLevel + 1);
            } else {
                String name = file.getName();
                if (name.endsWith(extension) && name.contains(contains)) {
                    fileList.add(file);

                }
            }
        }

    }
}
