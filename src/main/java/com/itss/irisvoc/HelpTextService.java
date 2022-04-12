package com.itss.irisvoc;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HelpTextService {
    private static final Map<String, String> elements = new HashMap<>();

    public static void main(String[] args) throws IOException {

        // todo add path as a parameter

        Path fileName = Paths.get("C:\\Users\\Kirill\\Desktop\\vocabularyIRIS\\Transact.HelpText.R21.zip");

        try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(fileName))) {
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {

                if (entry.isDirectory()) {
                    continue;
                }

                if (entry.getName().toLowerCase().endsWith(".xml")) {

                    try {

                        String fileContent = readAll(zip);

                        int startIndexFileName = entry.getName().lastIndexOf('/') + 1;

                        // table = file name
                        String table = entry.getName().substring(startIndexFileName, entry.getName().length() - 4); // remove '.xml'

                        parse(fileContent, table);

                    } catch (Exception ex) {
                        System.err.println(entry.getName() + " [" + ex + "]");
                    }
                }
            }
        }

        for (Map.Entry<String, String> entry : elements.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    @SneakyThrows
    private static void parse(String fileContent, String table) {
        String[] splitFileByFieldTag = fileContent.split("<field>");

        for (String str : splitFileByFieldTag) {
            String field = getField(str);
            String desc = getDesc(str);

            if (!field.isEmpty()) {
                // key = (file name (table) + field)
                String key = table + "*" + field;

                elements.put(key, desc);
            }
        }
    }

    private static String getField(String str) {
        return str.substring(0, str.indexOf('<'))
                .replaceAll("<.*?>|\n|\t|\\s([\\s])+", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&");
    }

    private static String getDesc(String str) {
        int startIndex = str.indexOf("<desc>");
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
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&");
    }

    @SneakyThrows
    public static String readAll(InputStream is) {
        if (is == null) {
            return "";
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[512];
        int b;
        while ((b = is.read(buffer)) >= 0) {
            baos.write(buffer, 0, b);
        }

        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}