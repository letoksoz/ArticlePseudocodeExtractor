package org.pseudocode.extract.pipeline.model;

import org.pseudocode.extract.pipeline.fileOperation.FileType;

public class ArticleFile {
    private String path;
    private FileType type;
    private long size;
    private String version;

    public ArticleFile(String path, FileType type, long size, String version) {
        this.path = path;
        this.type = type;
        this.size = size;
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public FileType getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "ArticleFile{" +
                "path='" + path + '\'' +
                ", type=" + type +
                ", version='" + version + '\'' +
                '}';
    }
}
