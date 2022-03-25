package com.itss.irisvoc;

import com.itss.t24runtime.Record;
import com.itss.t24runtime.T24Runtime;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntryHandler {
    private static final Pattern PATTERN_VERSION = Pattern.compile("^.*,.*\\.API\\..*\\.\\d+\\.\\d+\\.\\d+$");
    private static final Pattern PATTERN_RESOURCE = Pattern.compile("^.*,.*\\.API(\\.([^\\.]+))*\\.\\d+\\.\\d+\\.\\d+$");
    
    private static final List<Vocabulary.Entries> listForAdd = new ArrayList<>();

    private static Map<String, Vocabulary.Entries> entriesCacheByProperty;
    private static Map<String, Vocabulary.Entries> entriesCacheByResource;
    private static Map<String, Vocabulary.Entries> entriesCacheByVerb;

    public static void main(String[] args) throws Exception {

        Options options = Main.getCommandOptions();

        CommandLine cmd;
        try (T24Runtime runtime = T24Runtime.getNotInitialized()) {

            cmd = new DefaultParser().parse(options, args);

            final Path src = Paths.get(cmd.getOptionValue("s"));
            final Path out = Paths.get(cmd.getOptionValue("o"));

            Vocabulary vocabulary = VocabularyService.deserializationFromJson(src);

            entriesCacheByProperty = VocabularyService.getEntriesCache(vocabulary, EntryType.property.toString());
            entriesCacheByResource = VocabularyService.getEntriesCache(vocabulary, EntryType.resource.toString());
            entriesCacheByVerb = VocabularyService.getEntriesCache(vocabulary, EntryType.verb.toString());
            

            for (String recId : runtime.select("F.VERSION")) {

//                TELLER,CBVTMS.API.CBD.1.0.0
//                TSA.SERVICE,EB.API.JOBS.1.0.0
//                if (!recId.equals("TELLER,CBVTMS.API.CBD.1.0.0")) {
//                    continue;
//                }

                // get all with version
                Matcher matcher = PATTERN_VERSION.matcher(recId);
                if (!matcher.matches()) {
                    continue;
                }

                recursion(runtime, recId);

//                Record record = runtime.readRecord("F.VERSION", recId);
//
//                Record.Field id = record.get("@ID");
//
//                List<Record.Field> fieldNos = record.get("FIELD.NO").asListVm();
//                List<Record.Field> texts = record.get("TEXT").asListVm();
//                Record.Field description = record.get("DESCRIPTION");
//
//                // create entry with entryType resource
//                Matcher matcherResource = PATTERN_RESOURCE.matcher(recId);
//                if (matcherResource.matches()) {
//                    String resourceName = matcherResource.group(2);
//
//                    Vocabulary.Entries entry = entriesCacheByResource.get(resourceName);
//                    handleEntry(entry, EntryType.resource, resourceName, "", entriesCacheByResource);
//                }
//
//                // create entry with entryType verb
//                String[] verbs = description.toString().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
//                if (verbs.length > 0) {
//                    String verbName = verbs[0];
//
//                    Vocabulary.Entries entry = entriesCacheByVerb.get(verbName);
//                    handleEntry(entry, EntryType.verb, verbName, "", entriesCacheByVerb);
//                }
//
//                int len = fieldNos.size();
//                for (int i = 0; i < len; i++) {
//
//                    String text = i < texts.size() ? texts.get(i).toString() : "";
//
//                    // create entry with entryType property
//                    final String USAGE = "T24_" + id + "_" + fieldNos.get(i);
//                    Vocabulary.Entries entry = entriesCacheByProperty.get(text);
//                    handleEntry(entry, EntryType.property, text.replaceAll(" ", ""), USAGE, entriesCacheByProperty);
//                }

            }

            vocabulary.getEntries().addAll(listForAdd);

            VocabularyService.serializationIntoJson(vocabulary, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    
    private static void recursion(T24Runtime runtime, String recId) {
        Record record = runtime.readRecord("F.VERSION", recId);

        Record.Field id = record.get("@ID");

        List<Record.Field> fieldNos = record.get("FIELD.NO").asListVm();
        List<Record.Field> texts = record.get("TEXT").asListVm();
        List<Record.Field> assocVersions = record.get("ASSOC.VERSION").asListVm();
        Record.Field description = record.get("DESCRIPTION");

        for (Record.Field field : assocVersions) {
            recursion(runtime, field.toString());
        }

        // create entry with entryType resource
        Matcher matcherResource = PATTERN_RESOURCE.matcher(recId);
        if (matcherResource.matches()) {
            String resourceName = matcherResource.group(2);

            Vocabulary.Entries entry = entriesCacheByResource.get(resourceName);
            handleEntry(entry, EntryType.resource, resourceName, "", entriesCacheByResource);
        }

        // create entry with entryType verb
        String[] verbs = description.toString().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        if (verbs.length > 0) {
            String verbName = verbs[0];

            Vocabulary.Entries entry = entriesCacheByVerb.get(verbName);
            handleEntry(entry, EntryType.verb, verbName, "", entriesCacheByVerb);
        }

        int len = fieldNos.size();
        for (int i = 0; i < len; i++) {

            String text = i < texts.size() ? texts.get(i).toString().replaceAll(" ", "") : "";

            // create entry with entryType property
            final String USAGE = "T24_" + id + "_" + fieldNos.get(i);
            Vocabulary.Entries entry = entriesCacheByProperty.get(text);
            handleEntry(entry, EntryType.property, text, USAGE, entriesCacheByProperty);
        }
    }

    private static void handleEntry(Vocabulary.Entries entry, EntryType entryType, String key, String USAGE, Map<String, Vocabulary.Entries> map) {
        if (entry == null) {
            Vocabulary.Entries newEntry = entryBuilder(key, entryType.toString(), USAGE);
            listForAdd.add(newEntry);
            map.put(key, newEntry);
        } else {
            if (!entry.getUsage().contains(USAGE)) {
                entry.getUsage().add(USAGE);
            }
        }
    }

    private static Vocabulary.Entries entryBuilder(String key, String entryType, String USAGE) {
        Vocabulary.Entries newEntry = new Vocabulary.Entries();

        newEntry.setKey(key);
        if (!USAGE.equals(""))
            newEntry.getUsage().add(USAGE);
        newEntry.setEntryType(entryType);

        return newEntry;
    }
}