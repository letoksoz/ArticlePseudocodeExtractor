package org.pseudocode.extract.statistic;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OutputFileData implements Comparable {
    private int year;
    private int month;
    private Date start;
    private Date end;
    private Date startCopying;
    private Date endCopying;
    private Date startExtracting;
    private Date endExtracting;
    private Date startDetection;
    private Date endDetection;
    private int totalNumberOfFilesAndDirectories;
    private int totalArticles;

    public OutputFileData(int year, int month, Date start) {
        this.year = year;
        this.month = month;
        this.start = start;
    }

    @Override
    public int compareTo(Object o) {
        var other = (OutputFileData) o;
        if (year == other.year) {
            return month - other.month;
        }
        return year - other.year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStartCopying() {
        return startCopying;
    }

    public void setStartCopying(Date startCopying) {
        this.startCopying = startCopying;
    }

    public Date getEndCopying() {
        return endCopying;
    }

    public void setEndCopying(Date endCopying) {
        this.endCopying = endCopying;
    }

    public Date getStartExtracting() {
        return startExtracting;
    }

    public void setStartExtracting(Date startExtracting) {
        this.startExtracting = startExtracting;
    }

    public Date getEndExtracting() {
        return endExtracting;
    }

    public void setEndExtracting(Date endExtracting) {
        this.endExtracting = endExtracting;
    }

    public Date getStartDetection() {
        return startDetection;
    }

    public void setStartDetection(Date startDetection) {
        this.startDetection = startDetection;
    }

    public Date getEndDetection() {
        return endDetection;
    }

    public void setEndDetection(Date endDetection) {
        this.endDetection = endDetection;
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
        long diff = end.getTime() - start.getTime();
        long diffCopying = endCopying.getTime() - startCopying.getTime();
        long diffExtracting = endExtracting.getTime() - startExtracting.getTime();
        long diffDetection = endDetection.getTime() - startDetection.getTime();

        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append(" - ");
        sb.append(month);
        sb.append(": ");
        sb.append("\n\tNum. of Articles:\t\t\t\t\t");
        sb.append(totalArticles);
        sb.append("\n\tNum. of files amd directories:\t\t");
        sb.append(totalNumberOfFilesAndDirectories);
        sb.append("\n\tnum. of average files per article\t");
        sb.append(String.format("%d", totalArticles == 0 ? 0 : (totalNumberOfFilesAndDirectories / totalArticles)));



        sb.append("\n\tTotal:\t\t");
        sb.append(timeString(diff));

        sb.append("\n\tCopying:\t");
        sb.append(timeString(diffCopying));
        sb.append(" (%");
        sb.append(String.format("%.2f", ((double) diffCopying / diff) * 100));
        sb.append(")");

        sb.append("\n\tExtracting:\t");
        sb.append(timeString(diffExtracting));
        sb.append(" (%");
        sb.append(String.format("%.2f", ((double) diffExtracting / diff) * 100));
        sb.append(")");

        sb.append("\n\tDetection:\t");
        sb.append(timeString(diffDetection));
        sb.append(" (%");
        sb.append(String.format("%.2f", ((double) diffDetection / diff) * 100));
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
