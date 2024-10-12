package com.example.service;

import com.example.dto.GenerateMoreRequestDto;
import com.example.dto.Message;
import com.example.dto.OpenAIRequest;
import com.example.dto.UserInputDto;
import com.example.entity.AIData;
import com.example.repository.AIDataRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    private final WebClient webClient;
    private final AIDataRepository aiDataRepository; // Injected repository
    private final ObjectMapper objectMapper = new ObjectMapper();

    private OpenAIRequest buildOpenAIRequest(String prompt) {
        // Create the message object with the "user" role and the prompt content
        Message userMessage = new Message("user", prompt);

        // Build the OpenAI request object with the model, the list of messages, and token limit
        return new OpenAIRequest(
                "gpt-4",               // Adjust the model name as needed
                List.of(userMessage),  // List of messages (in this case, just the user prompt)
                1000                   // Set the max tokens (adjust this as needed)
        );
    }

    // Existing method for initial data generation
    @Transactional
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
            Map<String, Object> result = processResponse(rawResponse);

            // Step 4: Save the data to the AI_DATA table
            List<JsonNode> dataList = (List<JsonNode>) result.get("data");
            saveAIData(userInput.getTopic(), dataList);

            return result;

        } catch (WebClientResponseException e) {
            log.error("Error from OpenAI: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate data from OpenAI API", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while generating data", e);
        }
    }

    // New method for generating more data
    @Transactional
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
            Map<String, Object> result = processResponse(rawResponse);

            // Step 4: Append the new data to the existing AI_DATA record
            List<JsonNode> newDataList = (List<JsonNode>) result.get("data");
            appendAIData(requestDto.getTopic(), newDataList);

            return result;

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
        String basePrompt = String.format(
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

        // Add instruction to ensure consistent properties
        String consistencyInstruction = "Ensure that all records have the same properties and data types.";

        return basePrompt + "\n" + consistencyInstruction;
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

        // Add instruction to ensure consistent properties
        String consistencyInstruction = "Ensure that all records have the same properties and data types.";

        return promptBuilder.toString() + "\n" + consistencyInstruction;
    }

    private Map<String, Object> processResponse(String rawResponse) throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();

        // Parse the raw response as JSON
        JsonNode root = objectMapper.readTree(rawResponse);

        // Extract the content from choices[0].message.content
        JsonNode choicesNode = root.get("choices");
        if (choicesNode != null && choicesNode.isArray() && !choicesNode.isEmpty()) {
            String content = choicesNode.get(0).get("message").get("content").asText();

            // Print out the extracted content for debugging
            log.debug("Extracted content: {}", content);

            // Remove markdown code block indicators (`` ```json `` and `` ``` ``)
            String cleanedContent = content.replaceAll("```json", "").replaceAll("```", "").trim();
            log.debug("Cleaned content: {}", cleanedContent);

            // Now try to parse the cleaned content as JSON
            JsonNode dataArray = objectMapper.readTree(cleanedContent);  // Parse cleaned JSON array

            // Make sure the content is an array
            if (dataArray.isArray()) {
                List<JsonNode> dataList = objectMapper.convertValue(dataArray, new TypeReference<List<JsonNode>>() {});
                result.put("data", dataList);  // Put the array in the result map under "data"
            } else {
                throw new RuntimeException("Expected JSON array but got something else");
            }
        } else {
            throw new RuntimeException("Choices array is missing or empty");
        }

        return result;
    }

    // Save AI data to the AI_DATA table
    @Transactional
    public void saveAIData(String topic, List<JsonNode> dataList) throws IOException {
        AIData aiData = new AIData();
        aiData.setTopic(topic);
        aiData.setData(objectMapper.writeValueAsString(dataList));
        aiData.setCreatedAt(LocalDateTime.now());
        aiDataRepository.save(aiData);
        log.debug("AI data saved for topic: {}", topic);
    }

    // Append AI data to existing record
    @Transactional
    public void appendAIData(String topic, List<JsonNode> newDataList) throws IOException {
        Optional<AIData> optionalAIData = aiDataRepository.findByTopic(topic);
        if (optionalAIData.isPresent()) {
            AIData existingAIData = optionalAIData.get();
            String existingData = existingAIData.getData();
            List<JsonNode> existingDataList = objectMapper.readValue(existingData, new TypeReference<List<JsonNode>>() {});
            existingDataList.addAll(newDataList);
            existingAIData.setData(objectMapper.writeValueAsString(existingDataList));
            existingAIData.setCreatedAt(LocalDateTime.now());
            aiDataRepository.save(existingAIData);
            log.debug("AI data appended for topic: {}", topic);
        } else {
            // If no existing record, create a new one
            saveAIData(topic, newDataList);
        }
    }

    // Retrieve AI data by topic
    @Transactional
    public List<JsonNode> getAIDataByTopic(String topic) throws IOException {
        Optional<AIData> optionalAIData = aiDataRepository.findByTopic(topic);
        if (optionalAIData.isPresent()) {
            String data = optionalAIData.get().getData();
            return objectMapper.readValue(data, new TypeReference<List<JsonNode>>() {});
        } else {
            throw new RuntimeException("No AI data found for topic: " + topic);
        }
    }

    // Delete AI data by topic
    @Transactional
    public void deleteAIDataByTopic(String topic) {
        aiDataRepository.deleteByTopic(topic);
        log.debug("AI data deleted for topic: {}", topic);
    }
}
