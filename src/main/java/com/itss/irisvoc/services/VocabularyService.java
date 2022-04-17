package com.itss.irisvoc.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itss.irisvoc.entity.Vocabulary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VocabularyService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    // path - output file
    public static void serializationIntoJson(Vocabulary vocabulary) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("vocabulary-retail.json"),StandardCharsets.UTF_8)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, vocabulary);
        }

    }

}