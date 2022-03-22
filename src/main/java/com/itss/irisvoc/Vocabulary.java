package com.itss.irisvoc;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
class Vocabulary {
    private String domain;
    private List<Entries> entries;


    @Data
    static class Entries {
        private String key;
        private String description = "";
        private String plural = "";
        private List<String> insteadOf = new ArrayList<>(); //String or Object?
        private List<String> links = new ArrayList<>();
        private List<String> usage = new ArrayList<>();
        private String entryType;
        private String dataType = "String";
        private String label = "";
        private Map<String, Object> descriptions = new HashMap<>();
        private Map<String, Object> domainSpecificDescriptions = new HashMap<>();
        private boolean isGenerated;
        private boolean generated;
        private String domain = "retail";
        private List<String> composedOf = new ArrayList<>();
    }
}
