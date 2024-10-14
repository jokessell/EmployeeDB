package com.example.service;

import com.example.dto.GenerateMoreRequestDto;
import com.example.dto.Message;
import com.example.dto.OpenAIRequest;
import com.example.dto.UserInputDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // For logging
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
@Slf4j  // For logging
public class AIService {

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);


    private OpenAIRequest buildOpenAIRequest(String prompt) {
        // Create the message object with the "user" role and the prompt content
        Message userMessage = new Message("user", prompt);

        // Build the OpenAI request object with the model, the list of messages, and token limit
        return new OpenAIRequest(
                "gpt-4o-mini",               // Adjust the model name as needed
                List.of(userMessage),  // List of messages (in this case, just the user prompt)
                3000                   // Set the max tokens (adjust this as needed)
        );
    }

    // Existing method for initial data generation
    public Map<String, Object> generateTestData(UserInputDto userInput) {
        try {
            // Step 1: Create a single prompt for both refining the topic and generating the data
            String combinedPrompt = generateCombinedPrompt(userInput.getTopic(), userInput.getPropertyCount(), userInput.getRecordCount());

            // Step 2: Send the combined prompt to ChatGPT and get the response
            String rawResponse = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildOpenAIRequest(combinedPrompt)) // Send the combined prompt
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Raw response: {}", rawResponse);

            // Step 3: Process the response to extract the refined topic, data, and data types
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

        // Dynamically generate properties based on the requested propertyCount
        for (int i = 1; i <= propertyCount; i++) {
            properties.append(String.format("\"property%d\": \"value%d\"", i, i));
            if (i != propertyCount) {
                properties.append(", ");
            }
        }

        // Create the full prompt that enforces the number of records and properties
        return String.format(
                "The user wants to generate data for the topic \"%s\". " +
                        "Please generate exactly %d records with exactly %d properties per record. " +
                        "Respond in valid JSON format only, with no additional text or explanations. " +
                        "Each record should follow this format:\n" +
                        "[\n" +
                        "  {\n" +
                        "    %s\n" +  // Example properties dynamically generated
                        "  },\n" +
                        "  ...\n" +
                        "]\n" +
                        "Make sure there are exactly %d records, and each record has exactly %d properties, no more, no less.",
                userTopic, recordCount, propertyCount, properties.toString(), recordCount, propertyCount
        );
    }

    // New method for generating more data
    public Map<String, Object> generateMoreTestData(GenerateMoreRequestDto requestDto) {
        try {
            // Step 1: Create the prompt for generating more data
            String prompt = generateMoreDataPrompt(requestDto);

            // Step 2: Send the prompt to OpenAI API and get the response
            String rawResponse = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildOpenAIRequest(prompt))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Raw response: {}", rawResponse);

            // Step 3: Process the response to extract the data
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

        // Construct the prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(String.format("Please generate %d additional records for the topic \"%s\".", recordCount, topic));

        if (properties != null && !properties.isEmpty()) {
            promptBuilder.append(" Each record should include the following properties:");
            for (String property : properties) {
                promptBuilder.append(String.format(" \"%s\",", property));
            }
            // Remove the trailing comma
            promptBuilder.setLength(promptBuilder.length() - 1);
            promptBuilder.append(".");
        }

        promptBuilder.append(" Provide realistic and diverse values relevant to the topic. Respond in valid JSON format only, with no additional text or explanations.");

        return promptBuilder.toString();
    }

    private Map<String, Object> processResponse(String rawResponse) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();

        logger.debug("Raw AI Response: {}", rawResponse);

        // Parse the raw response as JSON
        JsonNode root = objectMapper.readTree(rawResponse);

        // Extract the content from choices[0].message.content
        JsonNode choicesNode = root.get("choices");
        if (choicesNode != null && choicesNode.isArray() && !choicesNode.isEmpty()) {
            JsonNode messageNode = choicesNode.get(0).get("message");
            if (messageNode == null) {
                throw new RuntimeException("Message node is missing in choices[0]");
            }

            String content = messageNode.get("content").asText();

            // Log the extracted content
            logger.debug("Extracted content: {}", content);

            // Remove markdown code block indicators (```json and ```)
            String cleanedContent = content.replaceAll("```json", "").replaceAll("```", "").trim();
            logger.debug("Cleaned content: {}", cleanedContent);

            // Now try to parse the cleaned content as JSON
            try {
                JsonNode dataArray = objectMapper.readTree(cleanedContent);  // Parse cleaned JSON array
                logger.debug("Parsed JSON Array: {}", dataArray.toString());

                // Make sure the content is an array
                if (dataArray.isArray()) {
                    result.put("data", dataArray);  // Put the array in the result map under "data"
                } else {
                    throw new RuntimeException("Expected JSON array but got something else");
                }
            } catch (JsonProcessingException e) {
                logger.error("Failed to parse cleaned content as JSON", e);
                throw new RuntimeException("Malformed JSON received from AI service", e);
            }
        } else {
            throw new RuntimeException("Choices array is missing or empty");
        }

        return result;
    }

}
