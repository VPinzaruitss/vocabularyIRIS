package com.itss.irisvoc;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
class Vocabulary {
    private String domain;
    private List<Entries> entries;


    @Data
    static class Entries {
        private String key;
        private String description;
        private String plural;
        private List<String> insteadOf; //String or Object?
        private List<String> links;
        private List<String> usage = new ArrayList<>();
        private String entryType;
        private String dataType = "String";
        private String label;
        private Map<String, Object> descriptions;
        private Map<String, Object> domainSpecificDescriptions;
        private boolean isGenerated;
        private boolean generated;
        private String domain = "Party";
        private List<String> composedOf;
    }
}
