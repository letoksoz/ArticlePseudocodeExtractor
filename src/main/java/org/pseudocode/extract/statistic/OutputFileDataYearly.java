package org.pseudocode.extract.statistic;

import java.util.Date;

public class OutputFileDataYearly {
    private int year;
    private Date start;
    private long totalTime;
    private long copyingTime;
    private long extractingTime;
    private long detectionTime;

    private int totalNumberOfFilesAndDirectories;
    private int totalArticles;

    public OutputFileDataYearly(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void addData(OutputFileData data) {
        if (year != data.getYear()) {
            throw new RuntimeException("Invalid data to add" + data.toString());
        }
        if (data.getMonth() == 1) {
            start = data.getStart();
        }
        totalTime += data.getEnd().getTime() - data.getStart().getTime();
        copyingTime += data.getEndCopying().getTime() - data.getStartCopying().getTime();
        extractingTime += data.getEndExtracting().getTime() - data.getStartExtracting().getTime();
        detectionTime += data.getEndDetection().getTime() - data.getStartDetection().getTime();

        totalNumberOfFilesAndDirectories += data.getTotalNumberOfFilesAndDirectories();
        totalArticles += data.getTotalArticles();

    }

    public int getTotalNumberOfFilesAndDirectories() {
        return totalNumberOfFilesAndDirectories;
    }

    public void setTotalNumberOfFilesAndDirectories(int totalNumberOfFilesAndDirectories) {
        this.totalNumberOfFilesAndDirectories = totalNumberOfFilesAndDirectories;
    }

    public int getTotalArticles() {
        return totalArticles;
    }

    public void setTotalArticles(int totalArticles) {
        this.totalArticles = totalArticles;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append(": ");
        sb.append(start);
        sb.append("\n\tNum. of Articles:\t\t\t\t\t");
        sb.append(totalArticles);
        sb.append("\n\tNum. of files amd directories:\t\t");
        sb.append(totalNumberOfFilesAndDirectories);
        sb.append("\n\tnum. of average files per article\t");
        sb.append(String.format("%d", totalArticles == 0 ? 0 : (totalNumberOfFilesAndDirectories / totalArticles)));

        sb.append("\n\tTotal:\t\t");
        sb.append(timeString(totalTime));

        sb.append("\n\tCopying:\t");
        sb.append(timeString(copyingTime));
        sb.append(" (%");
        sb.append(String.format("%.2f", ((double) copyingTime / totalTime) * 100));
        sb.append(")");

        sb.append("\n\tExtracting:\t");
        sb.append(timeString(extractingTime));
        sb.append(" (%");
        sb.append(String.format("%.2f", ((double) extractingTime / totalTime) * 100));
        sb.append(")");

        sb.append("\n\tDetection:\t");
        sb.append(timeString(detectionTime));
        sb.append(" (%");
        sb.append(String.format("%.2f", ((double) detectionTime / totalTime) * 100));
        sb.append(")");

        return sb.toString();
    }

    private String timeString(long diff) {
        diff /= 1000;
        long second = diff % 60;
        diff /= 60;
        long minutes = diff % 60;
        long hour = diff / 60;

        return String.format("%02d:%02d:%02d", hour, minutes, second);

    }

}
