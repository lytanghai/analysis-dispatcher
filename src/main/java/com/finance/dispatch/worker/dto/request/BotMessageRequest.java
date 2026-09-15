package com.finance.dispatch.worker.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BotMessageRequest {

    @JsonProperty("chat_id")
    private Long chatId;

    @JsonProperty("text")
    private String text;
}
