package com.example.pubg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerListResponse {
    private List<PlayerData> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerData {
        private String id;
    }
}
