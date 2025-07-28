package com.hmdp.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BinLog {

    @JsonProperty("before")
    private JSONObject before;
    @JsonProperty("after")
    private JSONObject after;
    @JsonProperty("source")
    private SourceDTO source;
    @JsonProperty("op")
    private String op;
    @JsonProperty("ts_ms")
    private Long tsMs;

    @Data
    public static class SourceDTO {
        @JsonProperty("db")
        private String db;
        @JsonProperty("table")
        private String table;
        @JsonProperty("server_id")
        private Integer serverId;
        @JsonProperty("pos")
        private Long pos;
        @JsonProperty("ts_ms")
        private Long tsMs;
    }
}