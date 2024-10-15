// src/main/java/com/example/mapper/EmployeeMapper.java
package com.example.mapper;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "skills", ignore = true)
    Employee toEntity(EmployeeDto employeeDto);

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "skills", ignore = true)
    void updateFromDto(EmployeeDto employeeDto, @MappingTarget Employee employee);
}
