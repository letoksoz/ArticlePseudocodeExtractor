package org.pseudocode.extract.other;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class S3DownloadScriptSimpleGenerator {

    public static void main(String[] args) {
        // S3DownloadScriptGenerator
        // s3cmd get --requester-pays s3://arxiv/src/arXiv_src_2011_001.tar ./arXiv_src_2011_001.tar
        // arXiv_src_9107_001.tar
        // arXiv_src_2306_133.tar
        // <filename>src/arXiv_src_2306_130.tar</filename>
        Path filePath = Paths.get("/volumes/big/arXiv_pdf_manifest.xml");
        Charset charset = StandardCharsets.UTF_8;
        try {
            List<String> lines = Files.readAllLines(filePath, charset);
            for (var line : lines) {
                String begin = "<filename>pdf/";
                if (line.contains(begin)) {
                    int startIndex = line.indexOf(begin) + begin.length();
                    int endIndex = line.indexOf("</filename>");
                    String fileName = line.substring(startIndex, endIndex);
//                    System.out.println("/volumes/big/" + fileName);
                    System.out.println("s3cmd get --requester-pays s3://arxiv/pdf/" + fileName +
                            " ./pdf/" + fileName);
                }
            }
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }
    }
}
