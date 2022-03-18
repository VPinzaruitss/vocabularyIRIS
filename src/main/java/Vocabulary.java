import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
class Vocabulary {
    private String domain;
    private List<Entries> entries;

    @Data
    @NoArgsConstructor
    static class Entries {
        private String key;
        private String description;
        private String plural;
        private List<String> insteadOf; //String or Object?
        private List<String> links;
        private List<String> usage;
        private String entryType;
        private String dataType;
        private String label;
        private Map descriptions;
        private Map domainSpecificDescriptions;
        private Boolean isGenerated;
        private Boolean generated;
        private String domain;
        private List<String> composedOf;
    }
}
