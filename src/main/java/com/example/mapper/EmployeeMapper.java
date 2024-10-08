package com.example.mapper;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "age", ignore = true)
    Employee toEntity(EmployeeDto employeeDto);

    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "age", ignore = true)
    void updateFromDto(EmployeeDto employeeDto, @MappingTarget Employee employee);
}

