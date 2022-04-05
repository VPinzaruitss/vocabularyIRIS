package com.itss.irisvoc;

import com.itss.t24runtime.T24Standalone;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class Standalone {

    public static void main(String[] args) {

        Options options = getCommandOptions();

        String TAFJ_HOME = null;
        CommandLine cmd;
        try {

            cmd = new DefaultParser().parse(options, args);
            TAFJ_HOME = cmd.getOptionValue("h");

        } catch (Exception e) {

            e.printStackTrace();

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Standalone", options);

        }

        T24Standalone.run(TAFJ_HOME, Main.class, args);
    }

    public static Options getCommandOptions() {

        // create Options object
        Options options = new Options();

        options.addRequiredOption("h", "home", true, "TAFJ home path");
        options.addRequiredOption("o", "out", true, "Output folder");

        return options;
    }
}
