package com.itss.irisvoc;

import com.itss.irisvoc.handlers.EnquiryHandler;
import com.itss.irisvoc.handlers.VersionHandler;
import com.itss.t24runtime.T24Runtime;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern VERSION_RECORD_PATTERN = Pattern.compile("^.*,.*\\.API\\..*\\.\\d+\\.\\d+\\.\\d+$");
    private static final Pattern ENQUIRY_RECORD_PATTERN = Pattern.compile("^.*\\.API\\..*\\.\\d+\\.\\d+\\.\\d+");

    public static final List<Vocabulary.Entries> listForAdd = new ArrayList<>();

    public static Map<String, Vocabulary.Entries> entriesCacheByProperty = new HashMap<>();
    public static Map<String, Vocabulary.Entries> entriesCacheByResource = new HashMap<>();
    public static Map<String, Vocabulary.Entries> entriesCacheByVerb = new HashMap<>();

    public static void main(String[] args) {

        Options options = Standalone.getCommandOptions();

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);

            final Path src = Paths.get(cmd.getOptionValue("s"));
            final Path out = Paths.get(cmd.getOptionValue("o"));

            Vocabulary vocabulary = VocabularyService.deserializationFromJson(src);

//            Vocabulary vocabulary = new Vocabulary();

            // fill up maps
            VocabularyService.getEntriesCache(vocabulary);

            try (T24Runtime runtime = T24Runtime.getNotInitialized()) {

                new VersionHandler().handleTable(runtime, "F.VERSION", VERSION_RECORD_PATTERN);
                new EnquiryHandler().handleTable(runtime, "F.ENQUIRY", ENQUIRY_RECORD_PATTERN);

            } catch (Exception e) {
                e.printStackTrace();
            }

            vocabulary.getEntries().addAll(Main.listForAdd);

            VocabularyService.serializationIntoJson(vocabulary, out);

        } catch (Exception e) {
            e.printStackTrace();

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Main", options);
        }
    }
}