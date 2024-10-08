package com.example.controller;

import com.example.dto.UserInputDto;
import com.example.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/generate-data")
    public List<String> generateData(@RequestBody UserInputDto requestDto) {
        Map<String, Object> result = aiService.generateTestData(requestDto);

        // Extract the data field from the result and return it as a list of strings
        JsonNode dataJson = (JsonNode) result.get("data");
        List<String> dataList = new ArrayList<>();

        if (dataJson != null && dataJson.isArray()) {
            dataJson.forEach(item -> dataList.add(item.toString()));  // Convert each record to a string
        }

        return dataList;
    }

}
