// src/main/java/com/example/mapper/SkillMapper.java
package com.example.mapper;

import com.example.dto.SkillDto;
import com.example.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillDto toDto(Skill skill);
    Skill toEntity(SkillDto skillDto);

    // Update existing Skill entity with SkillDto
    void updateFromDto(SkillDto skillDto, @MappingTarget Skill skill);
}
