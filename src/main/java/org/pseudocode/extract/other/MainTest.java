package org.pseudocode.extract.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainTest {

    private static final String outputDirectory = "/Users/Levent/Desktop/Pseudo code data/latex_and_pdf/";
    public static void main(String[] args) throws IOException, InterruptedException {

        String[] arguments = new String[] {"pdflatex", "-output-directory", "\"" + outputDirectory + "\"", "\"" + outputDirectory + "2011.00033.tex\""};
        Process proc = new ProcessBuilder(arguments).start();

        //String command = "/bin/bash mkdir testdir ";
        //var proc = Runtime.getRuntime().exec(command);
       // System.out.println(command);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        // Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        while (proc.isAlive()) {
            Thread.sleep(2000);
        }
        System.out.println(proc);
        System.out.println(proc.exitValue());
    }
}
