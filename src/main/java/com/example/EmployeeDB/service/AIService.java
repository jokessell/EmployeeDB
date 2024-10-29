package com.example.EmployeeDB.service;

import com.example.EmployeeDB.dto.GenerateMoreRequestDto;
import com.example.EmployeeDB.dto.Message;
import com.example.EmployeeDB.dto.OpenAIRequest;
import com.example.EmployeeDB.dto.UserInputDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    @Setter
    @Value("${openai.api.key:}")
    private String openAiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private OpenAIRequest buildOpenAIRequest(String prompt) {
        Message userMessage = new Message("user", prompt);
        return new OpenAIRequest(
                "gpt-4o-mini",
                List.of(userMessage),
                3000
        );
    }

    public Map<String, Object> generateTestData(UserInputDto userInput) {
        if (userInput == null) {
            throw new IllegalArgumentException("User input cannot be null");
        }

        try {
            String combinedPrompt = generateCombinedPrompt(userInput.getTopic(), userInput.getPropertyCount(), userInput.getRecordCount());
            String rawResponse = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildOpenAIRequest(combinedPrompt))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Raw response: {}", rawResponse);
            return processResponse(rawResponse);

        } catch (WebClientResponseException e) {
            log.error("Error from OpenAI: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate data from OpenAI API", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while generating data", e);
        }
    }

    private String generateCombinedPrompt(String userTopic, int propertyCount, int recordCount) {
        StringBuilder properties = new StringBuilder();
        for (int i = 1; i <= propertyCount; i++) {
            properties.append(String.format("\"property%d\": \"value%d\"", i, i));
            if (i != propertyCount) {
                properties.append(", ");
            }
        }
        return String.format(
                "The user wants to generate data for the topic \"%s\". " +
                        "Please generate exactly %d records with exactly %d properties per record. " +
                        "Respond in valid JSON format only, with no additional text or explanations. " +
                        "Each record should follow this format:\n" +
                        "[\n" +
                        "  {\n" +
                        "    %s\n" +
                        "  },\n" +
                        "  ...\n" +
                        "]\n" +
                        "Make sure there are exactly %d records, and each record has exactly %d properties, no more, no less.",
                userTopic, recordCount, propertyCount, properties.toString(), recordCount, propertyCount
        );
    }

    public Map<String, Object> generateMoreTestData(GenerateMoreRequestDto requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("Request data cannot be null");
        }
        if (requestDto.getTopic() == null || requestDto.getTopic().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be null or empty");
        }

        try {
            String prompt = generateMoreDataPrompt(requestDto);
            String rawResponse = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildOpenAIRequest(prompt))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Raw response: {}", rawResponse);
            return processResponse(rawResponse);

        } catch (WebClientResponseException e) {
            log.error("Error from OpenAI: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate data from OpenAI API", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while generating data", e);
        }
    }

    private String generateMoreDataPrompt(GenerateMoreRequestDto requestDto) {
        String topic = requestDto.getTopic();
        int recordCount = requestDto.getRecordCount();
        List<String> properties = requestDto.getProperties();

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(String.format("Please generate %d additional records for the topic \"%s\".", recordCount, topic));

        if (properties != null && !properties.isEmpty()) {
            promptBuilder.append(" Each record should include the following properties:");
            for (String property : properties) {
                promptBuilder.append(String.format(" \"%s\",", property));
            }
            promptBuilder.setLength(promptBuilder.length() - 1);
            promptBuilder.append(".");
        }

        promptBuilder.append(" Provide realistic and diverse values relevant to the topic. Respond in valid JSON format only, with no additional text or explanations.");
        return promptBuilder.toString();
    }

    Map<String, Object> processResponse(String rawResponse) {
        Map<String, Object> result = new LinkedHashMap<>();

        logger.debug("Raw AI Response: {}", rawResponse);

        if (rawResponse == null || rawResponse.isEmpty()) {
            throw new RuntimeException("Empty response from AI service");
        }

        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            JsonNode choicesNode = root.get("choices");
            if (choicesNode != null && choicesNode.isArray() && !choicesNode.isEmpty()) {
                JsonNode messageNode = choicesNode.get(0).get("message");
                if (messageNode == null) {
                    throw new RuntimeException("Message node is missing in choices[0]");
                }

                String content = messageNode.get("content").asText();

                logger.debug("Extracted content: {}", content);

                String cleanedContent = content.replaceAll("```json", "").replaceAll("```", "").trim();
                logger.debug("Cleaned content: {}", cleanedContent);

                try {
                    JsonNode dataArray = objectMapper.readTree(cleanedContent);

                    if (dataArray.isArray()) {
                        result.put("data", dataArray);
                    } else {
                        logger.warn("Expected JSON array but got something else, wrapping in array for safety.");
                        ArrayNode arrayNode = objectMapper.createArrayNode();
                        arrayNode.add(dataArray);
                        result.put("data", arrayNode);
                    }
                } catch (JsonProcessingException e) {
                    logger.error("Failed to parse cleaned content as JSON: {}", cleanedContent, e);
                    throw new RuntimeException("Malformed JSON in cleaned content. Cleaned content: " + cleanedContent, e);
                }
            } else {
                throw new RuntimeException("Choices array is missing or empty");
            }

        } catch (JsonProcessingException e) {
            logger.error("Failed to parse raw response as JSON: {}", rawResponse, e);
            throw new RuntimeException("Malformed JSON received from AI service. Raw response: " + rawResponse, e);
        } catch (RuntimeException e) {
            logger.warn("A RuntimeException occurred during response processing: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during response processing", e);
            throw new RuntimeException("Unexpected error occurred while processing response from AI service", e);
        }

        return result;
    }

}

