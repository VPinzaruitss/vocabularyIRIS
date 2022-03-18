import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.Options;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Options getCommandOptions() {
        // create Options object
        Options options = new Options();

        options.addRequiredOption("h", "home", true, "TAFJ home path");
        options.addOption("c", "config", true, "TAFJ configuration name");
        options.addRequiredOption("s", "src", true, "Source folder");
        options.addRequiredOption("o", "out", true, "Output folder");
        options.addOption("i", "inserts", true, "Folder with additional inserts");
        return options;
    }



    public static void main(String[] args) {
        serialization(deserialization(),null);

    }

    public static void serialization(Vocabulary vocabulary , Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            objectMapper.writeValue(writer, vocabulary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Vocabulary deserialization() {
        Vocabulary vocabulary = null;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("src\\main\\resources\\vocabulary-retail.json"), StandardCharsets.UTF_8)) {
//            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            vocabulary = objectMapper.readValue(reader, Vocabulary.class);
            System.out.println(vocabulary);

//            for(Vocabulary.Entries entries: vocabulary.getEntries()){
//                System.out.println(entries);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vocabulary;

    }
}