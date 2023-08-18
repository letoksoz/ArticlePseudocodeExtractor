package org.pseudocode.extract.other;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class S3DownloadScriptGenerator {

    public static void main(String[] args) {
        Path filePath = Paths.get("s3_files_to_download");
        Charset charset = StandardCharsets.UTF_8;
        try {
            List<String> lines = Files.readAllLines(filePath, charset);
            for (var line : lines) {
                var parts = line.split(" ");
                String s3Location = parts[parts.length - 1];
                parts = s3Location.split("/");
                String fileName = parts[parts.length - 1];
                String command = "s3cmd get --requester-pays " + s3Location + " ./" + fileName;
                System.out.println(command);
            }
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }
    }
}
