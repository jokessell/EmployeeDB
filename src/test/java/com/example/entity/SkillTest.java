// src/test/java/com/example/entity/SkillTest.java
package com.example.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SkillTest {

    @Test
    void testSkillGettersAndSetters() {
        Skill skill = new Skill();
        skill.setSkillId(1L);
        skill.setName("Spring Framework");

        assertEquals(1L, skill.getSkillId());
        assertEquals("Spring Framework", skill.getName());
    }
}