package com.itss.irisvoc.handlers;

import com.itss.irisvoc.EntryType;
import com.itss.irisvoc.entity.Vocabulary;
import com.itss.t24runtime.T24Runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Handler {
    List<Vocabulary.Entries> listForAdd = new ArrayList<>();

    Map<String, Vocabulary.Entries> entriesCacheByProperty = new HashMap<>();
    Map<String, Vocabulary.Entries> entriesCacheByResource = new HashMap<>();
    Map<String, Vocabulary.Entries> entriesCacheByVerb = new HashMap<>();

    // get all records in table and check with pattern
    default void handleTable(T24Runtime runtime, String screen, Pattern pattern) {

        for (String recId : runtime.select(screen)) {

            Matcher matcher = pattern.matcher(recId);
            if (!matcher.matches()) {
                continue;
            }

            handleRecord(runtime, recId, screen);
        }

    }

    // get all fields for certain table and processing them
    void handleRecord(T24Runtime runtime, String recId, String screen);

    // create entry if that not present
    default void handleEntry(Vocabulary.Entries entry, EntryType entryType,
                             String key, Map<String, Vocabulary.Entries> map,
                             String description, String usage) {

        if (entry == null) {
            if (key == null || key.isEmpty()) {
                return;
            }

            Vocabulary.Entries newEntry = entryBuilder(key, entryType.toString(), description, usage);
            listForAdd.add(newEntry);
            map.put(key, newEntry);
        } else if (!entry.getUsage().contains(usage) && usage != null) {
            entry.getUsage().add(usage);
        }

    }

    // create entry
    default Vocabulary.Entries entryBuilder(String key, String entryType,
                                            String description, String usage) {

        Vocabulary.Entries newEntry = new Vocabulary.Entries();

        newEntry.setKey(key);
        newEntry.setEntryType(entryType);
        newEntry.setDescription(description);

        if (usage != null) {
            newEntry.getUsage().add(usage);
        }

        return newEntry;
    }

}