package com.itss.irisvoc;

import com.itss.t24runtime.T24Standalone;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        Options options = getCommandOptions();

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);

            final String TAFJ_HOME = cmd.getOptionValue("h");
            final Path out = Paths.get(cmd.getOptionValue("o"));
            final Path src = Paths.get(cmd.getOptionValue("s"));

            T24Standalone.run(TAFJ_HOME, SelectSample.class, args);

//            Vocabulary deserializedFile = VocabularyService.deserializationFromJson(src);
//            VocabularyService.serializationIntoJson(deserializedFile, out);
        } catch (Exception e) {
            e.printStackTrace();

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Main", options);
        }
    }

    private static Options getCommandOptions() {
        // create Options object
        Options options = new Options();

        options.addRequiredOption("h", "home", true, "TAFJ home path");
        options.addRequiredOption("s", "src", true, "Source folder");
        options.addRequiredOption("o", "out", true, "Output folder");

        return options;
    }
}