package com.itss.irisvoc;

import com.itss.t24runtime.Record;
import com.itss.t24runtime.Record.Field;
import com.itss.t24runtime.T24Runtime;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectSample {

    public static class MainSubTest {
        public static void main(String[] args) throws Exception {

            try (T24Runtime runtime = T24Runtime.getNotInitialized()) {

                Vocabulary vocabulary = VocabularyService.deserializationFromJson(Paths.get("C:\\Users\\Kirill\\Desktop\\vocabularyIRIS\\src\\main\\resources\\vocabulary-retail.json"));
                List<Vocabulary.Entries> entries = vocabulary.getEntries();
                List<Vocabulary.Entries> listForAdd = new ArrayList<>();

                for (String recId : runtime.select("F.VERSION")) {
                    if (recId.matches("^.*,.*\\.API\\..*\\.\\d+\\.\\d+\\.\\d+$")) {

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

                            for (Vocabulary.Entries entry : entries) {
                                if (entry.getKey().equals(text)) {
                                    if (entry.getEntryType().equals("property")) {
                                        if (!entry.getUsage().contains(USAGE)) {
                                            entry.getUsage().add(USAGE);
                                            continue;
                                        }
                                    }

                                    // create identical but with entryType equals 'property'
//                                    entries.add(entryBuilderByOld(entry));
                                    listForAdd.add(entryBuilderByOld(entry));
                                    continue;
                                }

                                // create new entry and add to the entries
//                                entries.add(entryBuilder(text));
                                listForAdd.add(entryBuilder(text));
                            }

                        }

                        System.out.println("--------------------------");


                    }

                    entries.addAll(listForAdd);
                }

                VocabularyService.serializationIntoJson(vocabulary, Paths.get("C:\\Users\\Kirill\\Desktop\\vocabularyIRIS\\src\\main\\resources\\output.json"));

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

}
