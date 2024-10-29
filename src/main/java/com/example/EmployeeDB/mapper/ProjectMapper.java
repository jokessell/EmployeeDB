package com.example.EmployeeDB.mapper;

import com.example.EmployeeDB.dto.ProjectDto;
import com.example.EmployeeDB.entity.Employee;
import com.example.EmployeeDB.entity.Project;
import org.mapstruct.*;
import com.example.EmployeeDB.entity.Skill;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class, SkillMapper.class})
public interface ProjectMapper {

    @Mapping(target = "employeeIds", source = "employees", qualifiedByName = "projectEmployeesToIds")
    @Mapping(target = "skillIds", source = "skills", qualifiedByName = "projectSkillsToIds")
    @Mapping(target = "employees", source = "employees")
    @Mapping(target = "skills", source = "skills")
    ProjectDto toDto(Project project);

    @Mapping(target = "employees", ignore = true) // Associations handled in service
    @Mapping(target = "skills", ignore = true)    // Associations handled in service
    Project toEntity(ProjectDto projectDto);

    @Named("projectEmployeesToIds")
    default Set<Long> projectEmployeesToIds(Set<Employee> employees) {
        if (employees == null) {
            return null;
        }
        return employees.stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
    }

    @Named("projectSkillsToIds")
    default Set<Long> projectSkillsToIds(Set<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(Skill::getSkillId)
                .collect(Collectors.toSet());
    }
}
