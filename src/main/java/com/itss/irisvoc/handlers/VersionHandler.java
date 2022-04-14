package com.itss.irisvoc.handlers;

import com.itss.irisvoc.EntryType;
import com.itss.irisvoc.HelpTextService;
import com.itss.irisvoc.Vocabulary;
import com.itss.t24runtime.Record;
import com.itss.t24runtime.T24Runtime;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.itss.irisvoc.Main.*;

public class VersionHandler implements Handler {

    private static final Pattern VERSION_PATTERN_RESOURCE =
            Pattern.compile("^(.*),.*\\.API(\\.([^.]+))*\\.\\d+\\.\\d+\\.\\d+$");

    @Override
    public void handleRecord(T24Runtime runtime, String recId, String screen) {

        Record record = runtime.readRecord(screen, recId);

        List<Record.Field> fieldNos = record.get("FIELD.NO").asListVm();
        List<Record.Field> texts = record.get("TEXT").asListVm();
        List<Record.Field> assocVersions = record.get("ASSOC.VERSION").asListVm();
        Record.Field description = record.get("DESCRIPTION");

        // nested records
        for (Record.Field field : assocVersions) {
            handleRecord(runtime, field.toString(), screen);
        }

        String tableName = "";

        // create entry with entryType 'resource' or update usage
        Matcher matcherResource = VERSION_PATTERN_RESOURCE.matcher(recId);
        if (matcherResource.matches()) {
            tableName = matcherResource.group(1);
            String resourceName = matcherResource.group(3);

            Vocabulary.Entries entry = entriesCacheByResource.get(resourceName);
            handleEntry(entry, EntryType.resource, resourceName, entriesCacheByResource, null, null);
        }

        // create entry with entryType 'verb' or update usage
        String[] verbs = description.toString().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        if (verbs.length > 0) {
            String verbName = verbs[0];

            Vocabulary.Entries entry = entriesCacheByVerb.get(verbName);
            handleEntry(entry, EntryType.verb, verbName, entriesCacheByVerb, null, null);
        }

        int len = fieldNos.size();
        for (int i = 0; i < len; i++) {
            String text = i < texts.size() ? texts.get(i).toString() : fieldNos.get(i).toString();

            String keyInHelpText = tableName + "*" + fieldNos.get(i);
            String desc = HelpTextService.elements.get(keyInHelpText);

            if (text.equals("")) {
                text = fieldNos.get(i).toString();
            }

            text = text.replaceAll("[:/. ]", "");

            String USAGE = "T24_" + recId;

            // create entry with entryType 'property' or update usage
            Vocabulary.Entries entry = entriesCacheByProperty.get(text);
            handleEntry(entry, EntryType.property, text, entriesCacheByProperty, desc, USAGE);

        }

    }

}