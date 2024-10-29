package com.example.EmployeeDB.service;

import com.example.EmployeeDB.dto.SkillDto;
import com.example.EmployeeDB.entity.Skill;
import com.example.EmployeeDB.exception.ResourceNotFoundException;
import com.example.EmployeeDB.mapper.SkillMapper;
import com.example.EmployeeDB.repository.SkillRepository;
import com.example.EmployeeDB.service.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    @Captor
    ArgumentCaptor<Skill> skillCaptor;

    private Skill skill1;
    private Skill skill2;
    private SkillDto skillDto1;
    private SkillDto skillDto2;

    @BeforeEach
    void setUp() {
        skill1 = new Skill();
        skill1.setSkillId(1L);
        skill1.setName("Java");

        skill2 = new Skill();
        skill2.setSkillId(2L);
        skill2.setName("Spring");

        skillDto1 = new SkillDto();
        skillDto1.setSkillId(1L);
        skillDto1.setName("Java");

        skillDto2 = new SkillDto();
        skillDto2.setSkillId(2L);
        skillDto2.setName("Spring");
    }

    /**
     * Test retrieving all skills successfully with pagination.
     */
    @Test
    void testGetAllSkills_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        List<Skill> skills = Arrays.asList(skill1, skill2);
        Page<Skill> skillPage = new PageImpl<>(skills, pageable, skills.size());

        when(skillRepository.findAll(pageable)).thenReturn(skillPage);
        when(skillMapper.toDto(skill1)).thenReturn(skillDto1);
        when(skillMapper.toDto(skill2)).thenReturn(skillDto2);

        Page<SkillDto> result = skillService.getAllSkills(pageable);

        assertNotNull(result, "Result should not be null.");
        assertEquals(2, result.getTotalElements(), "Total elements should be 2.");
        List<SkillDto> skillDtos = result.getContent();
        assertEquals(2, skillDtos.size(), "Number of Skill DTOs should be 2.");
        assertTrue(skillDtos.contains(skillDto1), "Result should contain skillDto1.");
        assertTrue(skillDtos.contains(skillDto2), "Result should contain skillDto2.");

        verify(skillRepository, times(1)).findAll(pageable);
        verify(skillMapper, times(1)).toDto(skill1);
        verify(skillMapper, times(1)).toDto(skill2);
    }

    /**
     * Test retrieving all skills when none exist.
     * Expects an empty page.
     */
    @Test
    void testGetAllSkills_NoSkills() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Skill> skills = Collections.emptyList();
        Page<Skill> skillPage = new PageImpl<>(skills, pageable, 0);

        when(skillRepository.findAll(pageable)).thenReturn(skillPage);

        Page<SkillDto> result = skillService.getAllSkills(pageable);

        assertNotNull(result, "Result should not be null.");
        assertEquals(0, result.getTotalElements(), "Total elements should be 0.");
        assertTrue(result.getContent().isEmpty(), "Skill DTO list should be empty.");

        verify(skillRepository, times(1)).findAll(pageable);
        verify(skillMapper, never()).toDto(any(Skill.class));
    }

    /**
     * Test retrieving a skill by valid ID.
     */
    @Test
    void testGetSkillById_Success() {
        Long skillId = 1L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill1));
        when(skillMapper.toDto(skill1)).thenReturn(skillDto1);

        SkillDto result = skillService.getSkillById(skillId);

        assertNotNull(result, "Result should not be null.");
        assertEquals(skillDto1, result, "Returned SkillDto should match expected.");

        verify(skillRepository, times(1)).findById(skillId);
        verify(skillMapper, times(1)).toDto(skill1);
    }

    /**
     * Test retrieving a skill by non-existent ID.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testGetSkillById_NotFound() {
        Long skillId = 99L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            skillService.getSkillById(skillId);
        }, "Expected ResourceNotFoundException for non-existent skill ID.");

        assertEquals("Skill not found with ID: 99", exception.getMessage(), "Exception message should match.");
        verify(skillRepository, times(1)).findById(skillId);
        verify(skillMapper, never()).toDto(any(Skill.class));
    }

    /**
     * Test creating a skill with valid data.
     */
    @Test
    void testCreateSkill_Success() {
        SkillDto newSkillDto = new SkillDto();
        newSkillDto.setName("Python");

        Skill newSkill = new Skill();
        newSkill.setName("Python");

        Skill savedSkill = new Skill();
        savedSkill.setSkillId(3L);
        savedSkill.setName("Python");

        SkillDto savedSkillDto = new SkillDto();
        savedSkillDto.setSkillId(3L);
        savedSkillDto.setName("Python");

        when(skillMapper.toEntity(newSkillDto)).thenReturn(newSkill);
        when(skillRepository.save(newSkill)).thenReturn(savedSkill);
        when(skillMapper.toDto(savedSkill)).thenReturn(savedSkillDto);

        SkillDto result = skillService.createSkill(newSkillDto);

        assertNotNull(result, "Result should not be null.");
        assertEquals(savedSkillDto, result, "Saved SkillDto should match expected.");

        verify(skillMapper, times(1)).toEntity(newSkillDto);
        verify(skillRepository, times(1)).save(newSkill);
        verify(skillMapper, times(1)).toDto(savedSkill);
    }

    /**
     * Test creating a skill with invalid data (missing skill name).
     * Expects IllegalArgumentException.
     */
    @Test
    void testCreateSkill_InvalidData_MissingSkillName() {
        SkillDto invalidSkillDto = new SkillDto();
        invalidSkillDto.setName(""); // Missing skill name

        Skill invalidSkill = new Skill();
        invalidSkill.setName("");

        when(skillMapper.toEntity(invalidSkillDto)).thenReturn(invalidSkill);
        when(skillRepository.save(invalidSkill)).thenThrow(new IllegalArgumentException("Skill name is required."));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            skillService.createSkill(invalidSkillDto);
        }, "Expected IllegalArgumentException due to missing skill name.");

        assertEquals("Skill name is required.", exception.getMessage(), "Exception message should match.");
        verify(skillMapper, times(1)).toEntity(invalidSkillDto);
        verify(skillRepository, times(1)).save(invalidSkill);
        verify(skillMapper, never()).toDto(any(Skill.class));
    }

    /**
     * Test updating an existing skill with valid data.
     */
    @Test
    void testUpdateSkill_Success() {
        Long skillId = 1L;
        SkillDto updateSkillDto = new SkillDto();
        updateSkillDto.setName("Advanced Java");

        Skill existingSkill = skill1;
        existingSkill.setName("Java");

        Skill updatedSkill = new Skill();
        updatedSkill.setSkillId(skillId);
        updatedSkill.setName("Advanced Java");

        SkillDto updatedSkillDto = new SkillDto();
        updatedSkillDto.setSkillId(skillId);
        updatedSkillDto.setName("Advanced Java");

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(existingSkill));
        when(skillRepository.save(existingSkill)).thenReturn(updatedSkill);
        when(skillMapper.toDto(updatedSkill)).thenReturn(updatedSkillDto);

        SkillDto result = skillService.updateSkill(skillId, updateSkillDto);

        assertNotNull(result, "Result should not be null.");
        assertEquals(updatedSkillDto, result, "Updated SkillDto should match expected.");

        verify(skillRepository, times(1)).findById(skillId);
        verify(skillRepository, times(1)).save(existingSkill);
        verify(skillMapper, times(1)).toDto(updatedSkill);
    }

    /**
     * Test updating a non-existent skill.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testUpdateSkill_NotFound() {
        Long skillId = 99L;
        SkillDto updateSkillDto = new SkillDto();
        updateSkillDto.setName("Non-existent Skill");

        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            skillService.updateSkill(skillId, updateSkillDto);
        }, "Expected ResourceNotFoundException for non-existent skill ID.");

        assertEquals("Skill not found with ID: 99", exception.getMessage(), "Exception message should match.");
        verify(skillRepository, times(1)).findById(skillId);
        verify(skillRepository, never()).save(any(Skill.class));
        verify(skillMapper, never()).toDto(any(Skill.class));
    }

    /**
     * Test deleting an existing skill successfully.
     */
    @Test
    void testDeleteSkill_Success() {
        Long skillId = 1L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill1));
        doNothing().when(skillRepository).delete(skill1);

        skillService.deleteSkill(skillId);

        verify(skillRepository, times(1)).findById(skillId);
        verify(skillRepository, times(1)).delete(skill1);
    }

    /**
     * Test deleting a non-existent skill.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testDeleteSkill_NotFound() {
        Long skillId = 99L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            skillService.deleteSkill(skillId);
        }, "Expected ResourceNotFoundException for non-existent skill ID.");

        assertEquals("Skill not found with ID: 99", exception.getMessage(), "Exception message should match.");
        verify(skillRepository, times(1)).findById(skillId);
        verify(skillRepository, never()).delete(any(Skill.class));
    }
}
