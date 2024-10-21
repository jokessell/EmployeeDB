// src/main/java/com/example/service/SkillService.java
package com.example.service;

import com.example.dto.SkillDto;
import com.example.entity.Skill;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.SkillMapper;
import com.example.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    // Retrieve all skills with pagination
    public Page<SkillDto> getAllSkills(Pageable pageable) {
        return skillRepository.findAll(pageable).map(skillMapper::toDto);
    }

    // Retrieve a skill by ID
    public SkillDto getSkillById(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));
        return skillMapper.toDto(skill);
    }

    // Create a new skill
    public SkillDto createSkill(SkillDto skillDto) {
        Skill skill = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toDto(savedSkill);
    }

    // Update an existing skill
    public SkillDto updateSkill(Long skillId, SkillDto skillDto) {
        Skill existingSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));
        existingSkill.setName(skillDto.getName());
        Skill updatedSkill = skillRepository.save(existingSkill);
        return skillMapper.toDto(updatedSkill);
    }

    // Delete a skill
    public void deleteSkill(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));
        skillRepository.delete(skill);
    }
}
