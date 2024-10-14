// src/main/java/com/example/mapper/ProjectMapper.java
package com.example.mapper;

import com.example.dto.ProjectDto;
import com.example.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "employee", ignore = true) // Association handled in Service
    Project toEntity(ProjectDto projectDto);

    @Mapping(source = "employee.employeeId", target = "employeeId")
    ProjectDto toDto(Project project);
}
