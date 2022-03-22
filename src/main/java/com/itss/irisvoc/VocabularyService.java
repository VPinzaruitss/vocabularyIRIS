package com.itss.irisvoc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itss.irisvoc.Vocabulary.Entries;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class VocabularyService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // path - output file
    public static void serializationIntoJson(Vocabulary vocabulary, @NonNull Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            objectMapper.writeValue(writer, vocabulary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // path - file with JSON
    public static Vocabulary deserializationFromJson(@NonNull Path path) throws Exception {
        if (!Files.exists(path))
            throw new FileNotFoundException();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return objectMapper.readValue(reader, Vocabulary.class);
        }
    }

    public static Map<String, Entries> getEntriesCache(Vocabulary vocabulary) {

        Map<String, Entries> entries = new HashMap<>();

        for (Entries entry : vocabulary.getEntries()) {

            if (!entry.getEntryType().equals("property")) {
                continue;
            }

            entries.put(entry.getKey(), entry);
        }

        return entries;
    }
}
