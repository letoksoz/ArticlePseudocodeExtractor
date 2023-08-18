package org.pseudocode.extract.statistic;

import org.pseudocode.extract.pipeline.fileOperation.FileType;
import org.pseudocode.extract.pipeline.fileOperation.PseudocodeResourceType;

import java.util.Map;
import java.util.TreeMap;

public class StatisticData {

    private int year;
    private int month;
    private int numOfArticles;

    private FileCounterData articlesFileCounterData;
    private Map<String, Integer> topicsMap = new TreeMap<>();;
    private int numOfPseudocodeArticles;
    private Map<PseudocodeResourceType, Integer> pseudocodeSourceMap = new TreeMap<>();
    private int numOfPseudocodeArticlesHasHtml;
    private FileCounterData pseudocodeArticlesFileCounterData;
    private Map<String, Integer> pseudocodeTopicsMap = new TreeMap<>();

    public StatisticData(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public void addPseudocodeSource(PseudocodeResourceType type, Integer count) {
        pseudocodeSourceMap.put(type, count);
    }

    public void addTopics(String topic) {
        if (topicsMap.containsKey(topic)) {
            topicsMap.put(topic, topicsMap.get(topic) + 1);
        } else {
            topicsMap.put(topic, 1);
        }
    }

    public void addPseudocodeTopicsMap(String topic) {
        if (pseudocodeTopicsMap.containsKey(topic)) {
            pseudocodeTopicsMap.put(topic, pseudocodeTopicsMap.get(topic) + 1);
        } else {
            pseudocodeTopicsMap.put(topic, 1);
        }
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setNumOfArticles(int numOfArticles) {
        this.numOfArticles = numOfArticles;
    }

    public void setArticlesFileCounterData(FileCounterData articlesFileCounterData) {
        this.articlesFileCounterData = articlesFileCounterData;
    }

    public void setNumOfPseudocodeArticles(int numOfPseudocodeArticles) {
        this.numOfPseudocodeArticles = numOfPseudocodeArticles;
    }


    public void setNumOfPseudocodeArticlesHasHtml(int numOfPseudocodeArticlesHasHtml) {
        this.numOfPseudocodeArticlesHasHtml = numOfPseudocodeArticlesHasHtml;
    }

    public void setPseudocodeArticlesFileCounterData(FileCounterData pseudocodeArticlesFileCounterData) {
        this.pseudocodeArticlesFileCounterData = pseudocodeArticlesFileCounterData;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getNumOfArticles() {
        return numOfArticles;
    }

    public FileCounterData getArticlesFileCounterData() {
        return articlesFileCounterData;
    }

    public Map<String, Integer> getTopicsMap() {
        return topicsMap;
    }

    public int getNumOfPseudocodeArticles() {
        return numOfPseudocodeArticles;
    }

    public Map<PseudocodeResourceType, Integer> getPseudocodeSourceMap() {
        return pseudocodeSourceMap;
    }

    public int getNumOfPseudocodeArticlesHasHtml() {
        return numOfPseudocodeArticlesHasHtml;
    }

    public FileCounterData getPseudocodeArticlesFileCounterData() {
        return pseudocodeArticlesFileCounterData;
    }

    public Map<String, Integer> getPseudocodeTopicsMap() {
        return pseudocodeTopicsMap;
    }

    @Override
    public String toString() {
        return "StatisticData{" + "\n" +
                "year = " + year + "\n" +
                "month = " + month + "\n" +
                "numOfArticles = " + numOfArticles + "\n" +
                "articlesFileCounterData = " + articlesFileCounterData + "\n" +
                "topicsMap = " + topicsMap + "\n" +
                "numOfPseudocodeArticles = " + numOfPseudocodeArticles + "\n" +
                "pseudocodeSourceMap = " + pseudocodeSourceMap + "\n" +
                "numOfPseudocodeArticlesHasHtml = " + numOfPseudocodeArticlesHasHtml + "\n" +
                "pseudocodeArticlesFileCounterData = " + pseudocodeArticlesFileCounterData + "\n" +
                "pseudocodeTopicsMap = " + pseudocodeTopicsMap + "\n" +
                '}';
    }
}
