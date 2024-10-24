package com.example.controller;

import com.example.dto.GenerateMoreRequestDto;
import com.example.dto.UserInputDto;
import com.example.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        if (requestDto == null) {
            throw new RuntimeException("User input cannot be null");
        }

        Map<String, Object> result = aiService.generateTestData(requestDto);

        // Extract the data field from the result and return it as a list of strings
        JsonNode dataJson = (JsonNode) result.get("data");
        List<String> dataList = new ArrayList<>();

        if (dataJson != null && dataJson.isArray()) {
            dataJson.forEach(item -> dataList.add(item.toString()));  // Convert each record to a string for consistency
        }

        return dataList;
    }

    @PostMapping("/generate-more-data")
    public List<String> generateMoreData(@RequestBody GenerateMoreRequestDto requestDto) {
        if (requestDto == null) {
            throw new RuntimeException("Request cannot be null");
        }

        Map<String, Object> result = aiService.generateMoreTestData(requestDto);

        // Extract the data field from the result and return it as a list of strings
        JsonNode dataJson = (JsonNode) result.get("data");
        List<String> dataList = new ArrayList<>();

        if (dataJson != null && dataJson.isArray()) {
            dataJson.forEach(item -> dataList.add(item.toString()));  // Use toString() to preserve JSON structure
        }

        return dataList;
    }

}
