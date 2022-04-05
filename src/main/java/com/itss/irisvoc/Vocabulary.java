package com.itss.irisvoc;

import com.fasterxml.jackson.databind.JsonNode;
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
        private List<String> usage;
        private String entryType;
        private String dataType;
        private String label;
        private Map<String, JsonNode> descriptions;
        private Map<String, Object> domainSpecificDescriptions;
        private Boolean isGenerated;
        private String domain;
        private List<String> composedOf;
    }
}