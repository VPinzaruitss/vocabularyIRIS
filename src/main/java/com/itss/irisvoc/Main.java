package com.itss.irisvoc;

import com.itss.t24runtime.T24Standalone;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class Main {

    public static void main(String[] args) {

        Options options = getCommandOptions();

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);

            final String TAFJ_HOME = cmd.getOptionValue("h");

            T24Standalone.run(TAFJ_HOME, EntryHandler.class, args);

        } catch (Exception e) {
            e.printStackTrace();

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Main", options);
        }
    }

    public static Options getCommandOptions() {

        // create Options object
        Options options = new Options();

        options.addRequiredOption("h", "home", true, "TAFJ home path");
        options.addRequiredOption("s", "src", true, "Source folder");
        options.addRequiredOption("o", "out", true, "Output folder");

        return options;
    }
}