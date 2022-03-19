import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Options options = getCommandOptions();

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);

            Path out = Paths.get(cmd.getOptionValue("o"));
            Path src = Paths.get(cmd.getOptionValue("s"));

            if (!Files.exists(src))
                throw new FileNotFoundException();

            Vocabulary deserializedFile = VocabularyService.deserializationFromJson(src);
            VocabularyService.serializationIntoJson(deserializedFile, out);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("Parsing failed");
        }
    }

    private static Options getCommandOptions() {
        // create Options object
        Options options = new Options();

        options.addRequiredOption("s", "src", true, "Source folder");
        options.addRequiredOption("o", "out", true, "Output folder");

        return options;
    }
}