package com.example.EmployeeDB.dto;

import com.example.EmployeeDB.dto.SkillDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SkillDtoTest {

    @Test
    void testSkillDtoGettersAndSetters() {
        SkillDto skillDto = new SkillDto();
        skillDto.setSkillId(1L);
        skillDto.setName("Java Programming");

        assertEquals(1L, skillDto.getSkillId());
        assertEquals("Java Programming", skillDto.getName());
    }

    @Test
    void testSkillDtoAllArgsConstructor() {
        SkillDto skillDto = new SkillDto(1L, "Java Programming");
        assertEquals(1L, skillDto.getSkillId());
        assertEquals("Java Programming", skillDto.getName());
    }

    @Test
    void testSkillDtoNoArgsConstructor() {
        SkillDto skillDto = new SkillDto();
        assertNull(skillDto.getSkillId());
        assertNull(skillDto.getName());
    }
}