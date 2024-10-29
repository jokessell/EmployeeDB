package com.example.EmployeeDB.mapper;

import com.example.EmployeeDB.dto.SkillDto;
import com.example.EmployeeDB.entity.Skill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skillDto);
}