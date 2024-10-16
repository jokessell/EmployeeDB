// src/main/java/com/example/mapper/ProjectMapper.java
package com.example.mapper;

import com.example.dto.ProjectDto;
import com.example.entity.Project;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class, SkillMapper.class})
public interface ProjectMapper {

    // Mapping from Project entity to ProjectDto with detailed employees and skills
    @Mapping(target = "employeeIds", source = "employees", qualifiedByName = "projectEmployeesToIds")
    @Mapping(target = "skillIds", source = "skills", qualifiedByName = "projectSkillsToIds")
    @Mapping(target = "employees", source = "employees")
    @Mapping(target = "skills", source = "skills")
    ProjectDto toDto(Project project);

    // Mapping from ProjectDto to Project entity (excluding detailed objects)
    @Mapping(target = "employees", ignore = true) // Associations handled in service
    @Mapping(target = "skills", ignore = true)    // Associations handled in service
    Project toEntity(ProjectDto projectDto);

    // Custom mapping methods to extract IDs from Employees and Skills
    @Named("projectEmployeesToIds")
    default Set<Long> projectEmployeesToIds(Set<com.example.entity.Employee> employees) {
        if (employees == null) {
            return null;
        }
        return employees.stream()
                .map(com.example.entity.Employee::getEmployeeId)
                .collect(Collectors.toSet());
    }

    @Named("projectSkillsToIds")
    default Set<Long> projectSkillsToIds(Set<com.example.entity.Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(com.example.entity.Skill::getSkillId)
                .collect(Collectors.toSet());
    }
}
