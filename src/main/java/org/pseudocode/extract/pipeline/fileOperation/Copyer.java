package org.pseudocode.extract.pipeline.fileOperation;

import org.pseudocode.extract.pipeline.util.Util;

import java.io.File;
import java.io.IOException;

import static org.pseudocode.extract.pipeline.Constants.*;
import static org.pseudocode.extract.pipeline.util.Util.createYearMonthKey;

public class Copyer {

    public static void copyFromS3Src(int year, int month) throws IOException {
        String filePrefix = "arXiv_src_" + createYearMonthKey(year, month);
        String filePostfix = ".tar";
        File bigS3Src = new File(bigS3SrcPath);
        var list = bigS3Src.listFiles();
        for (var tarFile : list) {
            if (tarFile.getName().startsWith(filePrefix) && tarFile.getName().endsWith(filePostfix)) {
                 File destinationFile = new File(t7S3SrcPath + tarFile.getName());
                Util.copyFile(tarFile, destinationFile);
            }
        }
    }

    public static void copyFromS3Pdf(int year, int month) {
        // TODO
    }

    public static void copyFromGcloud(int year, int month) throws IOException {
        File baseDirectory = new File(bigGcloudPath);
        String yearMonthKey = createYearMonthKey(year, month);
        for (var articleTypeFile : baseDirectory.listFiles()) {
            if (articleTypeFile.isFile()) {
                continue;
            }
            for (var pdfPsHtmlDir : articleTypeFile.listFiles()) {
                if (pdfPsHtmlDir.isFile() || pdfPsHtmlDir.getName().equals("ps")) {
                    continue;
                }

                for (var yearMonthDir : pdfPsHtmlDir.listFiles()) {
                    if (yearMonthDir.isFile()) {
                        continue;
                    }

                    if (yearMonthDir.getName().equals(yearMonthKey)) {
                        String path = yearMonthDir.getCanonicalPath().replace(bigGcloudPath, t7GcloudPath);
                        Util.copyDirectory(yearMonthDir, new File(path));
                    }
                }
            }

        }
    }
}
