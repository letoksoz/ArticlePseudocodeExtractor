package org.pseudocode.extract.statistic;


public record FileCounterData (
        int numOfArticlesHasPdfAndLatex,
        int numOfArticlesHasNotPdfAndLatex,
        int numOfArticlesHasPdfButHasNotLatex,
        int numOfArticlesHasNotPdfButHasLatex
) {

    public FileCounterData merge(FileCounterData data) {
        return new FileCounterData(
                data.numOfArticlesHasPdfAndLatex + numOfArticlesHasPdfAndLatex,
                data.numOfArticlesHasNotPdfAndLatex + numOfArticlesHasNotPdfAndLatex,
                data.numOfArticlesHasPdfButHasNotLatex + numOfArticlesHasPdfButHasNotLatex,
                data.numOfArticlesHasNotPdfButHasLatex + numOfArticlesHasNotPdfButHasLatex

        );
    }

    @Override
    public String toString() {
        return "\n" +
                "\t\tnumOfArticlesHasPdfAndLatex =" + numOfArticlesHasPdfAndLatex + "\n" +
                "\t\tnumOfArticlesHasNotPdfAndLatex =" + numOfArticlesHasNotPdfAndLatex + "\n" +
                "\t\tnumOfArticlesHasPdfButHasNotLatex =" + numOfArticlesHasPdfButHasNotLatex + "\n" +
                "\t\tnumOfArticlesHasNotPdfButHasLatex =" + numOfArticlesHasNotPdfButHasLatex
                ;
    }
}
