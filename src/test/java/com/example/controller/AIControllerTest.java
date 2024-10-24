package com.example.controller;

import com.example.dto.GenerateMoreRequestDto;
import com.example.dto.UserInputDto;
import com.example.service.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AIControllerTest {

    @Mock
    private AIService aiService;

    @InjectMocks
    private AIController aiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateData_validRequest() {
        UserInputDto requestDto = new UserInputDto();
        // Setup the mock response for aiService
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("data", new ObjectMapper().createArrayNode().add("Generated Data 1").add("Generated Data 2"));

        when(aiService.generateTestData(requestDto)).thenReturn(mockResult);

        // Call the method and verify the response
        List<String> response = aiController.generateData(requestDto);
        assertEquals(2, response.size());
        assertEquals("\"Generated Data 1\"", response.get(0));  // Expect quotes around the string
    }

    @Test
    void testGenerateData_nullRequest() {
        // Test for null request, expecting an exception or proper error response handling
        Exception exception = assertThrows(RuntimeException.class, () -> {
            aiController.generateData(null);
        });
        assertEquals("User input cannot be null", exception.getMessage());
    }

    @Test
    void testGenerateMoreData_validRequest() {
        GenerateMoreRequestDto requestDto = new GenerateMoreRequestDto();
        // Setup the mock response for aiService
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("data", new ObjectMapper().createArrayNode().add("More Data 1").add("More Data 2"));

        when(aiService.generateMoreTestData(requestDto)).thenReturn(mockResult);

        // Call the method and verify the response
        List<String> response = aiController.generateMoreData(requestDto);
        assertEquals(2, response.size());
        assertEquals("\"More Data 1\"", response.get(0));  // Expect quotes around the string
    }

    @Test
    void testGenerateMoreData_emptyProperties() {
        GenerateMoreRequestDto requestDto = new GenerateMoreRequestDto();
        // Simulate empty response from the service
        when(aiService.generateMoreTestData(requestDto)).thenReturn(new HashMap<>());

        // Call the method and verify that it handles empty data gracefully
        List<String> response = aiController.generateMoreData(requestDto);
        assertTrue(response.isEmpty());
    }
}
