package com.example.pubg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDetailResponse {
    private PlayerData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerData {
        private Relationships relationships;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Relationships {
            private Matches matches;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Matches {
                private List<MatchId> data;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class MatchId {
                    private String id;
                    private String type;
                }
            }
        }
    }
}
