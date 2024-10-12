package com.example.controller;

import com.example.dto.GenerateMoreRequestDto;
import com.example.dto.UserInputDto;
import com.example.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/ai")
@RequiredArgsConstructor
@Slf4j
public class AIController {

    private final AIService aiService;

    // Create new AI data
    @PostMapping("/data")
    public ResponseEntity<List<JsonNode>> generateData(@Valid @RequestBody UserInputDto requestDto) throws IOException {
        aiService.generateTestData(requestDto);
        List<JsonNode> dataList = aiService.getAIDataByTopic(requestDto.getTopic());
        return ResponseEntity.status(HttpStatus.CREATED).body(dataList);
    }


    // Append more AI data
    @PatchMapping("/data/{topic}")
    public ResponseEntity<List<JsonNode>> generateMoreData(
            @PathVariable String topic,
            @Valid @RequestBody GenerateMoreRequestDto requestDto) throws IOException {
        aiService.generateMoreTestData(requestDto);
        List<JsonNode> dataList = aiService.getAIDataByTopic(topic);
        return ResponseEntity.ok(dataList);
    }

    // Retrieve AI data by topic
    @GetMapping("/data/{topic}")
    public ResponseEntity<List<JsonNode>> getDataByTopic(@PathVariable String topic) throws IOException {
        List<JsonNode> dataList = aiService.getAIDataByTopic(topic);
        return ResponseEntity.ok(dataList);
    }

    // Delete AI data by topic
    @DeleteMapping("/data/{topic}")
    public ResponseEntity<Void> deleteDataByTopic(@PathVariable String topic) {
        aiService.deleteAIDataByTopic(topic);
        return ResponseEntity.noContent().build();
    }
}
