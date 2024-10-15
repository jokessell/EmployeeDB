// src/main/java/com/example/mapper/ProjectMapper.java
package com.example.mapper;

import com.example.dto.ProjectDto;
import com.example.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "skills", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(source = "employees", target = "employeeIds", qualifiedByName = "employeeIds")
    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "skillIds")
    ProjectDto toDto(Project project);

    @Named("employeeIds")
    default Set<Long> mapEmployeesToIds(Set<com.example.entity.Employee> employees) {
        if (employees == null) {
            return null;
        }
        return employees.stream()
                .map(com.example.entity.Employee::getEmployeeId)
                .collect(Collectors.toSet());
    }

    @Named("skillIds")
    default Set<Long> mapSkillsToIds(Set<com.example.entity.Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(com.example.entity.Skill::getSkillId)
                .collect(Collectors.toSet());
    }
}
