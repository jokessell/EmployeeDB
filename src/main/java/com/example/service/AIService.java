package com.example.service;

import com.example.dto.Message;
import com.example.dto.OpenAIRequest;
import com.example.dto.UserInputDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private OpenAIRequest buildOpenAIRequest(String prompt) {
        // Create the message object with the "user" role and the prompt content
        Message userMessage = new Message("user", prompt);

        // Build the OpenAI request object with the model, the list of messages, and token limit
        return new OpenAIRequest(
                "gpt-4o-mini",       // Model name (you can adjust this based on the model you're using)
                List.of(userMessage),   // List of messages (in this case, just the user prompt)
                1000                    // Set the max tokens (adjust this as needed)
        );
    }


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

            System.out.println("Raw response: " + rawResponse);
            // Step 3: Process the response to extract the refined topic, data, and data types
            return processResponse(rawResponse);

        } catch (WebClientResponseException e) {
            System.err.println("Error from OpenAI: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate data from OpenAI API");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while generating data");
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


    private Map<String, Object> processResponse(String rawResponse) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();

        // Parse the raw response as JSON
        JsonNode root = objectMapper.readTree(rawResponse);

        // Extract the content from choices[0].message.content
        JsonNode choicesNode = root.get("choices");
        if (choicesNode != null && choicesNode.isArray() && !choicesNode.isEmpty()) {
            String content = choicesNode.get(0).get("message").get("content").asText();

            // Print out the extracted content for debugging
            System.out.println("Extracted content: " + content);

            // Remove markdown code block indicators (`` ```json `` and `` ``` ``)
            String cleanedContent = content.replaceAll("```json", "").replaceAll("```", "").trim();
            System.out.println("Cleaned content: " + cleanedContent);
            // Now try to parse the cleaned content as JSON
            JsonNode dataArray = objectMapper.readTree(cleanedContent);  // Parse cleaned JSON array

            // Make sure the content is an array
            if (dataArray.isArray()) {
                result.put("data", dataArray);  // Put the array in the result map under "data"
            } else {
                throw new RuntimeException("Expected JSON array but got something else");
            }
        } else {
            throw new RuntimeException("Choices array is missing or empty");
        }

        return result;
    }




}
