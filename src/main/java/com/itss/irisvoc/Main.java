package com.itss.irisvoc;

import com.itss.irisvoc.handlers.EnquiryHandler;
import com.itss.irisvoc.handlers.VersionHandler;
import com.itss.t24runtime.T24Runtime;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {
    private static final Pattern VERSION_RECORD_PATTERN = Pattern.compile("^.*,.*\\.API\\.?.*\\.\\d+\\.\\d+\\.\\d+$");
    private static final Pattern ENQUIRY_RECORD_PATTERN = Pattern.compile("^.*\\.API\\.?.*\\.\\d+\\.\\d+\\.\\d+$");

    public static final List<Vocabulary.Entries> listForAdd = new ArrayList<>();

    public static Map<String, Vocabulary.Entries> entriesCacheByProperty = new HashMap<>();
    public static Map<String, Vocabulary.Entries> entriesCacheByResource = new HashMap<>();
    public static Map<String, Vocabulary.Entries> entriesCacheByVerb = new HashMap<>();

    public static void main(String[] args) throws IOException {

        Options options = Standalone.getCommandOptions();

        Path out = null;
        CommandLine cmd;
        try {

            cmd = new DefaultParser().parse(options, args);
            out = Paths.get(cmd.getOptionValue("o"));

        } catch (Exception e) {

            e.printStackTrace();

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Main", options);

        }

        Vocabulary vocabulary = new Vocabulary();

        Vocabulary.Entries partyDomain = new Vocabulary.Entries();
        partyDomain.setKey("party");
        partyDomain.setEntryType("domain");
        vocabulary.getEntries().add(partyDomain);

        HelpTextService.getDescriptions();

        try (T24Runtime runtime = T24Runtime.getNotInitialized()) {

            new VersionHandler().handleTable(runtime, "F.VERSION", VERSION_RECORD_PATTERN);
            new EnquiryHandler().handleTable(runtime, "F.ENQUIRY", ENQUIRY_RECORD_PATTERN);

        } catch (Exception e) {
            e.printStackTrace();
        }

        vocabulary.getEntries().addAll(Main.listForAdd);

        try {
            VocabularyService.serializationIntoJson(vocabulary, out);
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);

        }
    }
}