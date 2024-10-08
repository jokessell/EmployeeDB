package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAIRequest {
    private String model;
    private List<Message> messages;  // The list of messages in the chat request
    private int max_tokens;
}
