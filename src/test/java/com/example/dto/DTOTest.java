package com.example.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DTOTest {

    @Test
    void testGenerateMoreRequestDto() {
        GenerateMoreRequestDto dto = new GenerateMoreRequestDto("Topic", 5, List.of("prop1", "prop2"));
        assertEquals("Topic", dto.getTopic());
        assertEquals(5, dto.getRecordCount());
        assertEquals(2, dto.getProperties().size());
    }

    @Test
    void testUserInputDto() {
        UserInputDto dto = new UserInputDto("Topic", 3, 10);
        assertEquals("Topic", dto.getTopic());
        assertEquals(3, dto.getPropertyCount());
        assertEquals(10, dto.getRecordCount());
    }

    @Test
    void testMessageDto() {
        Message message = new Message("user", "This is content");
        assertEquals("user", message.getRole());
        assertEquals("This is content", message.getContent());
    }

    @Test
    void testOpenAIRequest() {
        OpenAIRequest request = new OpenAIRequest("gpt-4o-mini", List.of(new Message("user", "Content")), 3000);
        assertEquals("gpt-4o-mini", request.getModel());
        assertEquals(1, request.getMessages().size());
        assertEquals(3000, request.getMax_tokens());
    }
}