package org.pseudocode.extract.statistic;

import org.pseudocode.extract.pipeline.fileOperation.PseudocodeResourceType;

import java.util.Map;
import java.util.TreeMap;

public class StatisticDataYearly {
    private int year;
    private int numOfArticles;

    private FileCounterData articlesFileCounterData = new FileCounterData(0, 0, 0, 0);
    private Map<String, Integer> topicsMap = new TreeMap<>();;
    private int numOfPseudocodeArticles;
    private Map<PseudocodeResourceType, Integer> pseudocodeSourceMap = new TreeMap<>();
    private int numOfPseudocodeArticlesHasHtml;
    private FileCounterData pseudocodeArticlesFileCounterData = new FileCounterData(0, 0, 0, 0);
    private Map<String, Integer> pseudocodeTopicsMap = new TreeMap<>();

    public StatisticDataYearly(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
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

    public void addStatisticData(StatisticData data) {
        numOfArticles += data.getNumOfArticles();
        numOfPseudocodeArticles += data.getNumOfPseudocodeArticles();
        numOfPseudocodeArticlesHasHtml += data.getNumOfPseudocodeArticlesHasHtml();

        articlesFileCounterData = articlesFileCounterData.merge(data.getArticlesFileCounterData());
        pseudocodeArticlesFileCounterData = pseudocodeArticlesFileCounterData.merge(data.getPseudocodeArticlesFileCounterData());

        mergeMaps(topicsMap, data.getTopicsMap());
        mergeMaps(pseudocodeSourceMap, data.getPseudocodeSourceMap());
        mergeMaps(pseudocodeTopicsMap, data.getPseudocodeTopicsMap());

    }

    private static <K> void mergeMaps(Map<K, Integer> m1, Map<K, Integer> m2) {
        for (var entity : m2.entrySet()) {
            var key = entity.getKey();
            if (m1.containsKey(key)) {
                m1.put(key, m1.get(key) + m2.get(key));
            } else {
                m1.put(key, m2.get(key));
            }
        }

    }
    @Override
    public String toString() {
        return "StatisticDataYearly{" + "\n" +
                "year = " + year + "\n" +
                "numOfArticles = " + numOfArticles + "\n" +
//                "articlesFileCounterData = " + articlesFileCounterData + "\n" +
//                "topicsMap = " + topicsMap + "\n" +
                "numOfPseudocodeArticles = " + numOfPseudocodeArticles + "\n" +
                String.format("ratioOfPseudocodeArticles = %%%.2f\n" , ((double) numOfPseudocodeArticles / numOfArticles) * 100) +
                "pseudocodeSourceMap = " + pseudocodeSourceMap + "\n" +
//                "numOfPseudocodeArticlesHasHtml = " + numOfPseudocodeArticlesHasHtml + "\n" +
//                "pseudocodeArticlesFileCounterData = " + pseudocodeArticlesFileCounterData + "\n" +
//                "pseudocodeTopicsMap = " + pseudocodeTopicsMap + "\n" +
                '}';
    }
}
