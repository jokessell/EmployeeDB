// src/test/java/com/example/controller/SkillControllerTest.java
package com.example.controller;

import com.example.dto.SkillDto;
import com.example.service.SkillService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SkillController.class)
public class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkillService skillService;

    @Test
    void testGetAllSkills() throws Exception {
        SkillDto skillDto = new SkillDto(1L, "Java Programming");

        // Mocking pageable
        Pageable pageable = PageRequest.of(0, 10);
        Page<SkillDto> skillPage = new PageImpl<>(List.of(skillDto), pageable, 1);
        Mockito.when(skillService.getAllSkills(Mockito.any(Pageable.class))).thenReturn(skillPage);

        mockMvc.perform(get("/api/skills").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].skillId").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Java Programming"));
    }


    @Test
    void testGetSkillById() throws Exception {
        SkillDto skillDto = new SkillDto(1L, "Java Programming");
        Mockito.when(skillService.getSkillById(1L)).thenReturn(skillDto);

        mockMvc.perform(get("/api/skills/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillId").value(1L))
                .andExpect(jsonPath("$.name").value("Java Programming"));
    }

    @Test
    void testCreateSkill() throws Exception {
        SkillDto skillDto = new SkillDto(null, "Java Programming");
        SkillDto createdSkill = new SkillDto(1L, "Java Programming");
        Mockito.when(skillService.createSkill(Mockito.any(SkillDto.class))).thenReturn(createdSkill);

        mockMvc.perform(post("/api/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Java Programming\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillId").value(1L))
                .andExpect(jsonPath("$.name").value("Java Programming"));
    }

    @Test
    void testUpdateSkill() throws Exception {
        SkillDto skillDto = new SkillDto(1L, "Advanced Java");
        Mockito.when(skillService.updateSkill(Mockito.eq(1L), Mockito.any(SkillDto.class))).thenReturn(skillDto);

        mockMvc.perform(put("/api/skills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Advanced Java\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillId").value(1L))
                .andExpect(jsonPath("$.name").value("Advanced Java"));
    }

    @Test
    void testDeleteSkill() throws Exception {
        Mockito.doNothing().when(skillService).deleteSkill(1L);

        mockMvc.perform(delete("/api/skills/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}