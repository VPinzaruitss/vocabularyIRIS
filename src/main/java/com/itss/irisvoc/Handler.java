package com.itss.irisvoc;

import com.itss.t24runtime.T24Runtime;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.itss.irisvoc.Main.listForAdd;

public interface Handler {

    // get all records in table and check with pattern
    default void handleTable(T24Runtime runtime, String tableName, Pattern pattern) {
        for (String recId : runtime.select(tableName)) {

            Matcher matcher = pattern.matcher(recId);
            if (!matcher.matches()) {
                continue;
            }

            handleRecord(runtime, recId, tableName);
        }
    }

    // get all fields for certain table and processing them
    void handleRecord(T24Runtime runtime, String recId, String tableName);

    default void handleEntry(Vocabulary.Entries entry, EntryType entryType,
                             String key, String USAGE, Map<String, Vocabulary.Entries> map) {
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

    // create entry
    default Vocabulary.Entries entryBuilder(String key, String entryType, String USAGE) {
        Vocabulary.Entries newEntry = new Vocabulary.Entries();

        newEntry.setKey(key);
        if (!USAGE.equals(""))
            newEntry.getUsage().add(USAGE);
        newEntry.setEntryType(entryType);

        return newEntry;
    }
}
