package com.itss.irisvoc;

import com.itss.t24runtime.Record;
import com.itss.t24runtime.Record.Field;
import com.itss.t24runtime.T24Runtime;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectSample {

    private static Pattern PATTERN_VERSION = Pattern.compile("^.*,.*\\.API\\..*\\.\\d+\\.\\d+\\.\\d+$");

    public static void main(String[] args) throws Exception {

        try (T24Runtime runtime = T24Runtime.getNotInitialized()) {

            Vocabulary vocabulary = VocabularyService.deserializationFromJson(Paths.get("vocabulary-retail.json"));
            Map<String, Vocabulary.Entries> entriesCache = VocabularyService.getEntriesCache(vocabulary);

            List<Vocabulary.Entries> listForAdd = new ArrayList<>();

            for (String recId : runtime.select("F.VERSION")) {

                Matcher matcher = PATTERN_VERSION.matcher(recId);
                if (!matcher.matches()) {
                    continue;
                }

                Record record = runtime.readRecord("F.VERSION", recId);

                Field id = record.get("@ID");
                System.out.println(id);

                List<Field> fieldNos = record.get("FIELD.NO").asListVm();
                List<Field> texts = record.get("TEXT").asListVm();
                List<Field> promptTexts = record.get("PROMPT.TEXT").asListVm();

                int len = fieldNos.size();
                for (int i = 0; i < len; i++) {

                    String text = i < texts.size() ? texts.get(i).toString() : "";
                    String promptText = i < promptTexts.size() ? promptTexts.get(i).toString() : "";

                    System.out.println(fieldNos.get(i) + " > " + text + " [" + promptText + "]");

                    // if json file doesn't have entry with certain key - create new entry
                    // if key is present but field 'entryType' not equals 'property' - create identical entry by old

                    final String USAGE = "T24_" + id + "_" + fieldNos.get(i);

                    Vocabulary.Entries entry = entriesCache.get(text);
                    if (entry == null) {
                        listForAdd.add(entryBuilder(text));
                    } else {
                        if (!entry.getUsage().contains(USAGE)) {
                            entry.getUsage().add(USAGE);
                        } else {

                            // create identical but with entryType equals 'property'
//                                    entries.add(entryBuilderByOld(entry));
                            Vocabulary.Entries newEntry = entryBuilderByOld(entry);
                            listForAdd.add(newEntry);
                            entriesCache.put(text, newEntry);
                        }
                    }

                }

                System.out.println("--------------------------");
            }

            vocabulary.getEntries().addAll(listForAdd);

            VocabularyService.serializationIntoJson(vocabulary, Paths.get("output.json"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Vocabulary.Entries entryBuilder(String key) {
        Vocabulary.Entries newEntry = new Vocabulary.Entries();

        newEntry.setKey(key);
        newEntry.setDescription("");
        newEntry.setPlural("");
        newEntry.setInsteadOf(new ArrayList<>());
        newEntry.setLinks(new ArrayList<>());
        newEntry.setUsage(new ArrayList<>());
        newEntry.setEntryType("property");
        newEntry.setDataType("String");
        newEntry.setLabel("");
        newEntry.setDescriptions(new HashMap<>());
        newEntry.setDomainSpecificDescriptions(new HashMap<>());
//            newEntry.setIsGenerated(Boolean.FALSE);
//            newEntry.setGenerated(Boolean.FALSE);
        newEntry.setDomain("retail");
        newEntry.setComposedOf(new ArrayList<>());

        return newEntry;
    }

    public static Vocabulary.Entries entryBuilderByOld(Vocabulary.Entries entry) {
        Vocabulary.Entries newEntry = new Vocabulary.Entries();

        newEntry.setKey(entry.getKey());
        newEntry.setDescription(entry.getDescription());
        newEntry.setPlural(entry.getPlural());
        newEntry.setInsteadOf(entry.getInsteadOf());
        newEntry.setLinks(entry.getLinks());
        newEntry.setUsage(entry.getUsage());
        newEntry.setEntryType("property");
        newEntry.setDataType(entry.getDataType());
        newEntry.setLabel(entry.getLabel());
        newEntry.setDescriptions(entry.getDescriptions());
        newEntry.setDomainSpecificDescriptions(entry.getDomainSpecificDescriptions());
//            newEntry.setIsGenerated(entry.getIsGenerated());
//            newEntry.setGenerated(entry.getGenerated());
        newEntry.setDomain(entry.getDomain());
        newEntry.setComposedOf(entry.getComposedOf());

        return newEntry;
    }
}
