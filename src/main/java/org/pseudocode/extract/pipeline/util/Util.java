package org.pseudocode.extract.pipeline.util;

import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util {

    public static void copyFile(String source, String destination) throws IOException {
        copyFile(new File(source), new File(destination));
    }

    public static void copyFile(File source, File destination) throws IOException {
        try (
                final FileInputStream in = new FileInputStream(source);
                final FileOutputStream out = new FileOutputStream(destination);
                ) {
            IOUtils.copy(in, out);
        }
    }

    public static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (sourceDirectory.isFile()) {
            throw new IllegalArgumentException();
        }
        if (!destinationDirectory.exists()) {
            if (!destinationDirectory.mkdirs()) {
                throw new IllegalStateException(String.format("Couldn't create directory %s.", destinationDirectory.getAbsolutePath()));
            }
        }
        for (var sourceFile : sourceDirectory.listFiles()) {
            String dest = destinationDirectory.getCanonicalPath() + File.separator + sourceFile.getName();
            if (sourceFile.isDirectory()) {
                copyDirectory(sourceFile,
                        new File(dest));
            } else {
                copyFile(sourceFile, new File(dest));
            }
        }
    }

    public static String createYearMonthKey(int year, int month) {
        year %= 100;
        return (year < 10 ? "0" : "") + year + (month < 10 ? "0" : "") + month;
    }
}
