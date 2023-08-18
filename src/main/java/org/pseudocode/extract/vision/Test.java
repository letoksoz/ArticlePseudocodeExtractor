package org.pseudocode.extract.vision;

public class Test {
    public static void main(String[] args) {
        ArticleProcessor processor = new ArticleProcessor(
                "articles/2011.03532v3"
        );
        new Thread(processor).start();

    }
}
