// src/main/java/com/example/mapper/EmployeeMapper.java
package com.example.mapper;

import com.example.dto.EmployeeDto;
import com.example.dto.SkillDto;
import com.example.entity.Employee;
import com.example.entity.Skill;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = SkillMapper.class)
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

    // **Updated toDto method to include employeeId and skills**
    @Mapping(target = "employeeId", source = "employeeId")
    @Mapping(target = "skills", source = "skills")
    @Mapping(target = "skillIds", ignore = true) // Optional: Remove if not needed
    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "employeeProjectsToIds")
    EmployeeDto toDto(Employee employee);

    // Custom mapping method to extract project IDs
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
