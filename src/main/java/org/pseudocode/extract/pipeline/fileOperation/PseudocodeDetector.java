package org.pseudocode.extract.pipeline.fileOperation;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.pseudocode.extract.pipeline.model.Article;
import org.pseudocode.extract.pipeline.model.ArticleFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PseudocodeDetector {
    private static String[] algorithms = {"Algorithm 1", "algorithm 1", "Algorithm-1", "algorithm-1", "Algorithm:", "algorithm:"};
    private static String[] pseudocode = {"Pseudocode", "pseudocode", "Pseudo-code", "pseudo-code"};

    public static void detectPseudocode(Article article) throws IOException {
        boolean foundPseudocode = detectFromLatexFiles(article);
        if (!foundPseudocode) {
            detectFromPdfFiles(article);
        }
    }

    private static boolean detectFromLatexFiles(Article article) throws IOException {
        var latexList = article.getArticleFiles()
                .stream()
                .filter(af -> af.getType() == FileType.LATEX || af.getType() == FileType.TEX
                        || af.getType() == FileType.LATEX_WITHOUT_EXTENSION)
                .toList();
        boolean algorithmInLatex = false;
        for (var latexArticleFile : latexList) {
            String content = FileUtils.readFileToString(new File(latexArticleFile.getPath()), StandardCharsets.UTF_8);

            if (content.contains("\\begin{algorithm}")) {
                article.setHasPseudocode(true);
                article.setPseudocodeSourceType(PseudocodeResourceType.LATEX_BEGIN_ALGORITHM);
                return true;
            }
            if (isIncludePseudocode(content)) {
                algorithmInLatex = true;
            }
        }
        if (algorithmInLatex) {
            article.setHasPseudocode(true);
            article.setPseudocodeSourceType(PseudocodeResourceType.LATEX);
            return true;
        }
        return false;
    }

    private static void detectFromPdfFiles(Article article) throws IOException {
        var pdfList = article.getArticleFiles()
                .stream()
                .filter(af -> af.getType() == FileType.PDF)
                .toList();
        for (var pdfArticleFile : pdfList) {
            if (pdfContainsAlgorithm(pdfArticleFile)) {
                article.setHasPseudocode(true);
                article.setPseudocodeSourceType(PseudocodeResourceType.PDF);
                return;
            }
        }
    }

    private static boolean pdfContainsAlgorithm(ArticleFile pdfArticleFile) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfArticleFile.getPath()))) {
            return isIncludePseudocode(readAllContent(document));
        } catch (Exception ex) {
            System.out.println("ERROR:: pdf could not read. PFD: " + pdfArticleFile.getPath() + " error message: " + ex.getMessage());
        }
        return false;
    }

    private static String readAllContent(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }

    private static boolean isIncludePseudocode(String content) {
        return contains(content, algorithms) || contains(content, pseudocode);
    }


    public static boolean contains(String content, String[] words) {
        for(var word : words) {
            if (content.contains(word)) {
                return true;
            }
        }
        return false;
    }

}
