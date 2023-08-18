package org.pseudocode.extract.pipeline.fileOperation;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FileTypeDetector {

    private static int fileTypeLength = FileType.values().length;

    public static int[] fileTypeCounter = new int[fileTypeLength];

    public static Map<String, Integer> unknowExtentionCounterMap = new HashMap<>();

    public static FileType detectFileType(String arxivId, File file) throws IOException {

        if (file.isDirectory()) {
            fileTypeCounter[0]++;
            return FileType.DIR;
        }
        String fileName = file.getName().toUpperCase();

        if (file.getName().equals(arxivId)) {
            FileType fileType = findFileTypeForNoExtensionFile(file);
            fileTypeCounter[fileType.ordinal()]++;
            return fileType;
        }

        int i = 0;
        for (var type : FileType.values()) {
            if (fileName.endsWith(type.name())) {
                fileTypeCounter[i]++;
                return type;
            }
            i++;
        }
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            FileType fileType = findFileTypeForNoExtensionFile(file);
            fileTypeCounter[fileType.ordinal()]++;
            return fileType;
        }

        String extension = fileName.substring(index + 1);
        if (unknowExtentionCounterMap.containsKey(extension)) {
            unknowExtentionCounterMap.put(extension, unknowExtentionCounterMap.get(extension) + 1);
        } else {
            unknowExtentionCounterMap.put(extension, 1);
        }

        fileTypeCounter[FileType.UNKNOWN.ordinal()]++;
        return FileType.UNKNOWN;
    }

    private static FileType findFileTypeForNoExtensionFile(File file) throws IOException {
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        if (content.contains("\\begin{document}")) {
            return FileType.LATEX_WITHOUT_EXTENSION;
        }
        if (content.contains("%!PS-Adobe-")) {
            return FileType.PS_WITHOUT_EXTENSION;
        }
        return FileType.NO_EXTENSION;
    }

}
