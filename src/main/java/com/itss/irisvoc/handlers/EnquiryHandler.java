package com.itss.irisvoc.handlers;

import com.itss.irisvoc.EntryType;
import com.itss.irisvoc.Vocabulary;
import com.itss.t24runtime.Record;
import com.itss.t24runtime.T24Runtime;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.itss.irisvoc.Main.*;

@NoArgsConstructor
public class EnquiryHandler implements Handler {

    private static final Pattern ENQUIRY_PATTERN_RESOURCE =
            Pattern.compile("^.*\\.API(\\.([^.]+))*\\.\\d+\\.\\d+\\.\\d+$");

    @Override
    public void handleRecord(T24Runtime runtime, String recId, String tableName) {

        Record record = runtime.readRecord(tableName, recId);

        List<Record.Field> selLabel = record.get("SEL.LABEL").asListVm();
        List<Record.Field> fieldNames = record.get("FIELD.NAME").asListVm();
        List<Record.Field> labels = record.get("FIELD.LBL").asListVm();
        List<Record.Field> descriptions = record.get("DESCRIPT").asListVm();

        // create entry with entryType 'resource' or update usage
        Matcher matcherResource = ENQUIRY_PATTERN_RESOURCE.matcher(recId);
        if (matcherResource.matches()) {
            String resourceName = matcherResource.group(2);

            Vocabulary.Entries entry = entriesCacheByResource.get(resourceName);
            handleEntry(entry, EntryType.resource, resourceName, entriesCacheByResource);
        }

        // create entry with entryType 'verb' or update usage
        for (Record.Field field : descriptions) {

            String[] verbs = field.toString().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
            if (verbs.length > 0) {
                String verbName = verbs[0];

                    Vocabulary.Entries entry = entriesCacheByVerb.get(verbName);
                    handleEntry(entry, EntryType.verb, verbName, entriesCacheByVerb);
            }

        }

        for (Record.Field strings : selLabel) {

            String text = strings.toString().replaceAll(" ", "");

            // create entry with entryType 'property' or update usage
            Vocabulary.Entries entry = entriesCacheByProperty.get(text);
            handleEntry(entry, EntryType.property, text, entriesCacheByProperty);

        }

        int fieldNamesLen = fieldNames.size();
        for (int i = 0; i < fieldNamesLen; i++) {

            String text = i < labels.size() ? labels.get(i).toString() : fieldNames.get(i).toString();

            if (text.equals("")) {
                text = fieldNames.get(i).toString();
            }

            text = text.replaceAll("[:/. ]", "");

            // create entry with entryType 'property' or update usage
            Vocabulary.Entries entry = entriesCacheByProperty.get(text);
            handleEntry(entry, EntryType.property, text, entriesCacheByProperty);

        }

    }

}