package org.pseudocode.extract.vision;


import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PseudoCodeImageExtractor {

    public static AtomicInteger problematicAlgorithmCounter = new AtomicInteger(0);

    public static Set<String> extract(List<String> list) throws Exception {
        Set<String> set = new HashSet<>();
        for (String fileName : list) {
            if (extract(fileName)) {
                set.add(fileName);
                System.out.println(fileName);
            }
        }
        return set;
    }

    public static boolean extract(String fileName) throws Exception {
        boolean found = false;

        Document document = readXMLDocumentFromFile(fileName + ".hocr");
        Element root = document.getDocumentElement();
        NodeList nList = root.getChildNodes();
        Node body = findElementByName(nList, "body");
        Element mainDiv = findFirstElement(body.getChildNodes());

        nList = mainDiv.getChildNodes();
        boolean algorithmFound = false;
        int separatorCounter = 0;
        int x1, y1, x2, y2;
        x1 = y1 = x2 = y2 = -1;
        int algoCounter  = 0;
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element divElement = (Element) node;

                if (!algorithmFound && divElement.getAttribute("class").equals("ocr_carea")) {
                    String[] texts = findFirstTwoText(divElement);
                    // System.out.println(Arrays.toString(texts));
                    if (isAlgorithmDiv(texts)) {
                        algorithmFound = true;
                        var parts = divElement.getAttribute("title").split(" ");
                        x1 = Integer.parseInt(parts[1]);
                        y1 = Integer.parseInt(parts[2]);
                        x2 = Integer.parseInt(parts[3]);
                        y2 = Integer.parseInt(parts[4]);

                    }
                } else if (algorithmFound) {

                    var parts = divElement.getAttribute("title").split(" ");
                    int x1New = Integer.parseInt(parts[1]);
                    int y1New = Integer.parseInt(parts[2]);
                    int x2New = Integer.parseInt(parts[3]);
                    int y2New = Integer.parseInt(parts[4]);

                    if (isValidElement(x1, x2, y2, x1New, y1New, x2New, y2New)) {
                        continue;
                    }

                    x1 = Math.min(x1, x1New);
                    y1 = Math.min(y1, y1New);
                    x2 = Math.max(x2, x2New);
                    y2 = Math.max(y2,y2New);
                    if (divElement.getAttribute("class").equals("ocr_separator")) {
                        separatorCounter++;
                    }

                    if (separatorCounter == 2) {
                        separatorCounter = 0;
                        algorithmFound = false;
                        PseudoCodeImageBoundaryWriter
                                .getInstance()
                                .writeLine(fileName + ".jpeg " + x1 + "," + y1 + "," + x2 + "," + y2);
                        System.out.println(fileName + ".jpeg " + x1 + "," + y1 + "," + x2 + "," + y2);
                        found = true;
                        ImageCropper.crop(fileName + ".jpeg",
                                fileName + "_algo_"  + algoCounter + ".jpeg",
                                x1, y1, x2, y2);
                        algoCounter++;
                    }
                }
            }
        }
        if (algorithmFound && !found) {
            problematicAlgorithmCounter.incrementAndGet();
            System.out.println("problematicAlgorithmCounter was increased for " + fileName);
        }
        return found;

    }

    private static boolean isValidElement(int x1, int x2, int y2, int x1New, int y1New, int x2New, int y2New) {
        return Math.abs(x2New - x1New) < 300 ||
                Math.abs(x1New - x1) > 300 ||
                Math.abs(x2New - x2) > 300 ||
                y2New < y2 || y1New < y2;
    }

    private static boolean isAlgorithmDiv(String[] texts) {
        if (texts[0].startsWith("Algorithm")) {
            int index = "Algorithm".length();
            while (index < texts[0].length()) {
                char ch = texts[0].charAt(index++);
                if (ch == ' ') {
                    continue;
                }
                if (Character.isDigit(ch)) {
                    return true;
                }
            }

            return Character.isDigit(texts[1].charAt(0));

        }
        return false;
    }

    private static String[] findFirstTwoText(Element element) {
        // getting ocr_par
        element = findFirstElement(element.getChildNodes());
        // getting ocr_line
        element = findFirstElement(element.getChildNodes());
        String[] array = {"", ""};
        int index = 0;
        NodeList nList = element.getChildNodes();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                array[index++] = node.getTextContent();
                if (index == 2) {
                    break;
                }
            }
        }
        return array;

    }


    public static Document readXMLDocumentFromFile(String fileNameWithPath) throws Exception {

        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Build Document
        Document document = builder.parse(new File(fileNameWithPath));

        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();

        return document;
    }

    public static Element findFirstElement(NodeList nList) {
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
        }
        return null;
    }

    public static Element findElementByName(NodeList nList, String name) {
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);

            if (node.getNodeName().equals(name)) {
                return (Element) node;
            }
        }
        return null;
    }
}
