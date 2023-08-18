package org.pseudocode.extract.vision;
// file: RunShellCommandFromJava.java

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TeseractExtractor {

    public static void executeTesseractCommand(List<String> list) throws IOException, InterruptedException {
        for (String fileName : list) {
            executeTesseractCommand(fileName);
        }
    }


    public static void executeTesseractCommand(String fileName) throws IOException, InterruptedException {
        String command = "tesseract " + fileName + ".jpeg " + fileName + " hocr";
        // System.out.println(command);
        Process proc = Runtime.getRuntime().exec(command);

        // Read the output

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.print(line + "\n");
        }

        proc.waitFor();
    }

    public static void replaceDTD(List<String> list) throws IOException {
        for (String fileName : list) {
            replaceDTD(fileName);
        }
    }

    public static void replaceDTD(String fileName) throws IOException {
        Path path = Paths.get(fileName + ".hocr");
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd",
                "file:////Volumes/T7/data/xhtml1-transitional.dtd");Files.write(path, content.getBytes( charset));
    }
}