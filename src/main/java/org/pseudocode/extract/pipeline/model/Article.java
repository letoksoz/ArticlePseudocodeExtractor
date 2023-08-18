package org.pseudocode.extract.pipeline.model;

import org.pseudocode.extract.pipeline.fileOperation.PseudocodeResourceType;
import org.pseudocode.extract.pipeline.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Article {
    private String arxivId;
    private int year;
    private int month;
    private boolean hasPseudocode = false;
    private PseudocodeResourceType pseudocodeSourceType;
    private List<ArticleFile> articleFiles;
    private String topic = null;

    public Article(String arxivId, int year, int month) {
        this.arxivId = arxivId;
        this.year = year;
        this.month = month;

        this.hasPseudocode = false;
        this.articleFiles = new ArrayList<>();
    }

    public String getArxivId() {
        return arxivId;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public boolean isHasPseudocode() {
        return hasPseudocode;
    }

    public void setHasPseudocode(boolean hasPseudocode) {
        this.hasPseudocode = hasPseudocode;
    }

    public PseudocodeResourceType getPseudocodeSourceType() {
        return pseudocodeSourceType;
    }

    public void setPseudocodeSourceType(PseudocodeResourceType pseudocodeSourceType) {
        this.pseudocodeSourceType = pseudocodeSourceType;
    }

    public void addArticleFile(ArticleFile articleFile) {
        articleFiles.add(articleFile);
    }

    public List<ArticleFile> getArticleFiles() {
        return articleFiles;
    }

    public String getTopic() {
        if (topic == null) {
            var yearMonthKey = Util.createYearMonthKey(year, month);
            int index = arxivId.indexOf(yearMonthKey);
            if (index == -1) {
                System.err.println("ERROR:: arxivId not include yearMonthKey. Article: " + toString());
                topic = "";
            } else {
                topic = arxivId.substring(0, index);
            }
        }
        return topic;
    }

    @Override
    public String toString() {
        return "Article{" +
                "arxivId='" + arxivId + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", hasPseudocode=" + hasPseudocode +
                ", articleFiles=" + articleFiles +
                '}';
    }
}
