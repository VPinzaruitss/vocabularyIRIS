package com.itss.irisvoc.handlers;

import com.itss.irisvoc.EntryType;
import com.itss.irisvoc.Vocabulary;
import com.itss.t24runtime.Record;
import com.itss.t24runtime.T24Runtime;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.itss.irisvoc.Main.*;

public class VersionHandler implements Handler {

    private static final Pattern VERSION_PATTERN_RESOURCE = Pattern.compile("^.*,.*\\.API(\\.([^\\.]+))*\\.\\d+\\.\\d+\\.\\d+$");

    @Override
    public void handleTable(T24Runtime runtime, String tableName, Pattern pattern) {
        Handler.super.handleTable(runtime, tableName, pattern);
    }

    @Override
    public void handleRecord(T24Runtime runtime, String recId, String tableName) {
        Record record = runtime.readRecord(tableName, recId);

        Record.Field id = record.get("@ID");

        List<Record.Field> fieldNos = record.get("FIELD.NO").asListVm();
        List<Record.Field> texts = record.get("TEXT").asListVm();
        List<Record.Field> assocVersions = record.get("ASSOC.VERSION").asListVm();
        Record.Field description = record.get("DESCRIPTION");

        // nested records
        for (Record.Field field : assocVersions) {
            handleRecord(runtime, field.toString(), tableName);
        }

        // create entry with entryType 'resource' or update usage
        Matcher matcherResource = VERSION_PATTERN_RESOURCE.matcher(recId);
        if (matcherResource.matches()) {
            String resourceName = matcherResource.group(2);

            Vocabulary.Entries entry = entriesCacheByResource.get(resourceName);
            handleEntry(entry, EntryType.resource, resourceName, "", entriesCacheByResource);
        }

        // create entry with entryType 'verb' or update usage
        String[] verbs = description.toString().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        if (verbs.length > 0) {
            String verbName = verbs[0];

            Vocabulary.Entries entry = entriesCacheByVerb.get(verbName);
            handleEntry(entry, EntryType.verb, verbName, "", entriesCacheByVerb);
        }

        int len = fieldNos.size();
        for (int i = 0; i < len; i++) {

            String text = i < texts.size() ? texts.get(i).toString() : fieldNos.get(i).toString();

            if (text.equals("")) {
                text = fieldNos.get(i).toString();
            }

            text = text.replaceAll("[:/. ]", "");

            // create entry with entryType 'property' or update usage
            final String USAGE = "T24_" + id + "_" + fieldNos.get(i);
            Vocabulary.Entries entry = entriesCacheByProperty.get(text);
            handleEntry(entry, EntryType.property, text, USAGE, entriesCacheByProperty);
        }
    }

    @Override
    public void handleEntry(Vocabulary.Entries entry, EntryType entryType, String key, String USAGE, Map<String, Vocabulary.Entries> map) {
        Handler.super.handleEntry(entry, entryType, key, USAGE, map);
    }

    @Override
    public Vocabulary.Entries entryBuilder(String key, String entryType, String USAGE) {
        return Handler.super.entryBuilder(key, entryType, USAGE);
    }

}