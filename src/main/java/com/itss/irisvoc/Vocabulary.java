package com.itss.irisvoc;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Vocabulary {
    private String domain = "retail";
    private List<Entries> entries = new ArrayList<>();

    @Data
    public static class Entries {
        private String key;
        private String description;
        private String plural;
        private List<String> insteadOf;
        private List<String> links;
        private List<String> usage = new ArrayList<>();
        private String entryType;
        private String dataType;
        private String label;
        private Map<String, Object> descriptions;
        private Map<String, Object> domainSpecificDescriptions;
        private boolean isGenerated;
//        private boolean generated;
        private String domain;
        private List<String> composedOf;
    }
}
