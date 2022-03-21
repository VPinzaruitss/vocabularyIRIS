package com.itss.irisvoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class VocabularyService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // path - output file
    public static void serializationIntoJson(Vocabulary vocabulary, @NonNull Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            objectMapper.writeValue(writer, vocabulary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // path - file with JSON
    public static Vocabulary deserializationFromJson(@NonNull Path path) throws FileNotFoundException {
        if (!Files.exists(path))
            throw new FileNotFoundException();

        Vocabulary vocabulary = null;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            vocabulary = objectMapper.readValue(reader, Vocabulary.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vocabulary;
    }
}
