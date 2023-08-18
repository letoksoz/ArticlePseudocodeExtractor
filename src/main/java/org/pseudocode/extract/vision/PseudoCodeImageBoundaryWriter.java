package org.pseudocode.extract.vision;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PseudoCodeImageBoundaryWriter {
    private static String fileName = "/Volumes/T7/data/boundaries.txt";

    private static PseudoCodeImageBoundaryWriter boundaryWriter;

    static {
        try {
            boundaryWriter = new PseudoCodeImageBoundaryWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedWriter writer;

    public PseudoCodeImageBoundaryWriter() throws IOException {
        writer = new BufferedWriter(new FileWriter(fileName));
    }

    public synchronized void writeLine(String line) throws IOException {
        writer.write(line);
    }

    public static PseudoCodeImageBoundaryWriter getInstance() {
        return boundaryWriter;
    }
}
