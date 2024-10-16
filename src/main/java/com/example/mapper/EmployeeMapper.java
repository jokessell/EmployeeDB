// src/main/java/com/example/mapper/EmployeeMapper.java
package com.example.mapper;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // Existing mapping methods
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "employeeId", ignore = true) // For toEntity
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "skills", ignore = true)
    Employee toEntity(EmployeeDto employeeDto);

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "employeeId", ignore = true) // For updateFromDto
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "skills", ignore = true)
    void updateFromDto(EmployeeDto employeeDto, @MappingTarget Employee employee);

    // **Add this method to map Employee to EmployeeDto**
    @Mapping(target = "skillIds", source = "skills", qualifiedByName = "employeeSkillsToIds")
    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "employeeProjectsToIds")
    EmployeeDto toDto(Employee employee);

    // **Custom mapping methods to extract IDs from Skills and Projects**
    @Named("employeeSkillsToIds")
    default Set<Long> employeeSkillsToIds(Set<com.example.entity.Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(com.example.entity.Skill::getSkillId)
                .collect(Collectors.toSet());
    }

    @Named("employeeProjectsToIds")
    default Set<Long> employeeProjectsToIds(Set<com.example.entity.Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream()
                .map(com.example.entity.Project::getProjectId)
                .collect(Collectors.toSet());
    }
}
