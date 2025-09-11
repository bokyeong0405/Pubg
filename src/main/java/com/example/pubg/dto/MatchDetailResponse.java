package com.example.pubg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchDetailResponse {

    private DataWrapper data;
    private List<Included> included;

    // ✅ MatchSummaryDto 역할을 수행할 필드 추가
    private int kills;
    private int assists;
    private int dBNOs;
    private double damageDealt;
    private double timeSurvived;
    private int winPlace;
    private String gameMode;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataWrapper {
        private String type;
        private String id;
        private Attributes attributes;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Attributes {
            private String gameMode;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Included {
        private String type;
        private String id;
        private Attributes attributes;
        private Relationships relationships;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Attributes {
            private Stats stats;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Stats {
                private int teamId;
                private String name;
                private int kills;
                private double damageDealt;
                private double timeSurvived;
                private int winPlace;
                private int assists;
                private int dBNOs;
            }
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Relationships {
            private Participants participants;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Participants {
                private List<ParticipantRef> data;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class ParticipantRef {
                    private String id;
                    private String type;
                }
            }
        }
    }
}


//@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class MatchDetailResponse {
//
//    private List<Included> included;
//
//    @Data
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class Included {
//        private String type;       // "participant", "roster" 등
//        private String id;         // participantId 또는 rosterId
//        private Attributes attributes;
//        private Relationships relationships;
//
//        @Data
//        @JsonIgnoreProperties(ignoreUnknown = true)
//        public static class Attributes {
//            private Stats stats;
//
//            @Data
//            @JsonIgnoreProperties(ignoreUnknown = true)
//            public static class Stats {
//                private int teamId; // 우리가 후처리로 직접 설정할 값
//                private String name;
//                private int kills;
//                private double damageDealt;
//                private double timeSurvived;
//                private int winPlace;
//                private int assists;
//                private int dBNOs;
//            }
//        }
//
//        @Data
//        @JsonIgnoreProperties(ignoreUnknown = true)
//        public static class Relationships {
//            private Participants participants;
//
//            @Data
//            @JsonIgnoreProperties(ignoreUnknown = true)
//            public static class Participants {
//                private List<ParticipantRef> data;
//
//                @Data
//                @JsonIgnoreProperties(ignoreUnknown = true)
//                public static class ParticipantRef {
//                    private String id;
//                    private String type;
//                }
//            }
//        }
//    }
//}
