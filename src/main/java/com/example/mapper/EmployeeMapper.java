// src/main/java/com/example/mapper/EmployeeMapper.java
package com.example.mapper;

import com.example.dto.EmployeeDto;
import com.example.dto.ProjectDto;
import com.example.entity.Employee;
import com.example.entity.Project;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {SkillMapper.class})
public interface EmployeeMapper {

    @Mapping(target = "employeeId", source = "employeeId")
    @Mapping(target = "skills", source = "skills")
    @Mapping(target = "skillIds", ignore = true) // For input purposes
    @Mapping(target = "projectIds", ignore = true) // For input purposes
    @Mapping(target = "projects", source = "projects", qualifiedByName = "mapProjectsWithoutEmployees")
    EmployeeDto toDto(Employee employee);

    @Named("mapProjectsWithoutEmployees")
    default Set<ProjectDto> mapProjectsWithoutEmployees(Set<Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream()
                .map(this::mapProjectWithoutEmployees)
                .collect(Collectors.toSet());
    }

    @Mapping(target = "employees", ignore = true) // Avoid circular reference
    ProjectDto mapProjectWithoutEmployees(Project project);

    // Existing mapping methods for toEntity and updateFromDto
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
}
