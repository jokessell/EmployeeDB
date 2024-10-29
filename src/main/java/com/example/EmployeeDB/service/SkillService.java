package com.example.EmployeeDB.service;

import com.example.EmployeeDB.dto.SkillDto;
import com.example.EmployeeDB.entity.Skill;
import com.example.EmployeeDB.exception.ResourceNotFoundException;
import com.example.EmployeeDB.mapper.SkillMapper;
import com.example.EmployeeDB.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public Page<SkillDto> getAllSkills(Pageable pageable) {
        return skillRepository.findAll(pageable).map(skillMapper::toDto);
    }

    public SkillDto getSkillById(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));
        return skillMapper.toDto(skill);
    }

    public SkillDto createSkill(SkillDto skillDto) {
        Skill skill = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toDto(savedSkill);
    }

    public SkillDto updateSkill(Long skillId, SkillDto skillDto) {
        Skill existingSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));
        existingSkill.setName(skillDto.getName());
        Skill updatedSkill = skillRepository.save(existingSkill);
        return skillMapper.toDto(updatedSkill);
    }

    public void deleteSkill(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));
        skillRepository.delete(skill);
    }
}
