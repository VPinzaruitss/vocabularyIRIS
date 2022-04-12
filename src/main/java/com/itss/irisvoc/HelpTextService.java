package com.itss.irisvoc;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HelpTextService {
    private static final Map<String, HelpText> elements = new HashMap<>();

    public static void main(String[] args) throws IOException {

        String fileName = "C:\\Users\\Kirill\\Desktop\\HELP.TEXT";

        Files.walk(Paths.get(fileName))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().toLowerCase().endsWith("xml"))
                .forEach(HelpTextService::parse);

//        parse(Paths.get("C:\\Users\\Kirill\\Desktop\\test.xml"));

        for (Map.Entry<String, HelpText> entry : elements.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().getDesc());
        }
    }

    @SneakyThrows
    private static void parse(Path path) {
        String fileContent = String.join("", Files.readAllLines(path));
        String[] splitFileByFieldTag = fileContent.split("<field>");

        for (String str : splitFileByFieldTag) {
            String field = getField(str);
            String desc = getDesc(str);

            if (!field.isEmpty()) {
                HelpText helpText = new HelpText(field, desc);

                // key = (file name + field)
                String key = path
                        .getFileName()
                        .toString()
                        .substring(0, path.getFileName().toString().length() - 4) + "*" + field;

                elements.put(key, helpText);
            }
        }
    }

    private static String getField(String str) {
        return str.substring(0, str.indexOf('<'))
                .replaceAll("<.*?>|\n|\t|\\s([\\s])+", "")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&");
    }

    private static String getDesc(String str) {
        int startIndex= str.indexOf("<desc>");
        int endIndex = str.indexOf("</desc>");

        String descContent;

        if (startIndex < 0 && endIndex < 0) {
            return "";
        }

        if (startIndex <= 0 && endIndex >= 0) {
            descContent = str.substring(0, endIndex);
        } else if (endIndex <= 0) {
            descContent = str.substring(startIndex);
        } else {
            descContent = str.substring(startIndex, endIndex);
        }

        return descContent
                .replaceAll("<.*?>|\n|\t|\\s([\\s])+", "")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&");
    }
}