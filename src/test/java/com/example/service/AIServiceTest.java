package com.example.service;// Test Cases for AIService.java

import com.example.dto.GenerateMoreRequestDto;
import com.example.dto.UserInputDto;
import com.example.service.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AIServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AIService aiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aiService = new AIService(webClient);
    }

    @Test
    void testGenerateTestData_validInput() {
        // Create valid UserInputDto
        UserInputDto userInputDto = new UserInputDto("Test Topic", 3, 5);

        // Mock WebClient response
        String jsonResponse = "{\"choices\":[{\"message\":{\"content\":\"[{\\\"property1\\\": \\\"value1\\\", \\\"property2\\\": \\\"value2\\\", \\\"property3\\\": \\\"value3\\\"}]\"}}]}";

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonResponse));

        // Perform test
        Map<String, Object> result = aiService.generateTestData(userInputDto);

        // Use assertions to validate expected result
        assertNotNull(result);
        assertTrue(result.containsKey("data"));
    }

    @Test
    void testGenerateTestData_handleWebClientException() {
        // Create valid UserInputDto
        UserInputDto userInputDto = new UserInputDto("Test Topic", 3, 5);

        // Mock WebClient to throw exception
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenThrow(new WebClientResponseException(HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null));

        // Use assertions to validate exception handling
        Exception exception = assertThrows(RuntimeException.class, () -> {
            aiService.generateTestData(userInputDto);
        });
        assertEquals("Failed to generate data from OpenAI API", exception.getMessage());
    }

    @Test
    void testGenerateTestData_nullInput() {
        // Test handling of null UserInputDto
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aiService.generateTestData(null);
        });
        assertEquals("User input cannot be null", exception.getMessage());
    }

    @Test
    void testGenerateMoreTestData_validInput() {
        // Similar approach as testGenerateTestData
        GenerateMoreRequestDto requestDto = new GenerateMoreRequestDto("Test Topic", 5, List.of("property1", "property2"));

        // Mock WebClient response
        String jsonResponse = "{\"choices\":[{\"message\":{\"content\":\"[{\\\"property1\\\": \\\"value1\\\", \\\"property2\\\": \\\"value2\\\"}]\"}}]}";

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonResponse));

        // Perform test
        Map<String, Object> result = aiService.generateMoreTestData(requestDto);

        // Use assertions to validate expected result
        assertNotNull(result);
        assertTrue(result.containsKey("data"));
    }

    @Test
    void testGenerateMoreTestData_emptyProperties() {
        // Test handling of empty properties list
        GenerateMoreRequestDto requestDto = new GenerateMoreRequestDto("Test Topic", 5, Collections.emptyList());

        // Mock WebClient response
        String jsonResponse = "{\"choices\":[{\"message\":{\"content\":\"[{\\\"property1\\\": \\\"value1\\\"}]\"}}]}";

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonResponse));

        // Perform test
        Map<String, Object> result = aiService.generateMoreTestData(requestDto);

        // Use assertions to validate expected result
        assertNotNull(result);
        assertTrue(result.containsKey("data"));
    }

    @Test
    void testProcessResponse_validResponse() throws Exception {
        // Provide a valid response JSON
        String rawResponse = "{\"choices\":[{\"message\":{\"content\":\"[{\\\"property1\\\": \\\"value1\\\"}]\"}}]}";

        // Validate response processing
        Map<String, Object> result = aiService.processResponse(rawResponse);
        assertNotNull(result);
        assertTrue(result.containsKey("data"));
    }

    @Test
    void testProcessResponse_invalidResponse() {
        // Test scenarios like missing nodes or malformed JSON
        String rawResponse = "{\"choices\":[]}";

        // Validate response processing with missing nodes
        Exception exception = assertThrows(RuntimeException.class, () -> {
            aiService.processResponse(rawResponse);
        });
        assertEquals("Choices array is missing or empty", exception.getMessage());
    }

    @Test
    void testProcessResponse_malformedJson() {
        String malformedJson = "{\"malformed json\"}"; // Intentionally malformed JSON

        Exception exception = assertThrows(RuntimeException.class, () -> {
            aiService.processResponse(malformedJson);
        });

        assertTrue(exception.getMessage().contains("Malformed JSON received from AI service"));
        assertTrue(exception.getMessage().contains("malformed json")); // Confirm the raw response is logged
    }
}

