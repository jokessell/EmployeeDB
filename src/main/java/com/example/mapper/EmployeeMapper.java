package com.example.mapper;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee toEntity(EmployeeDto employeeDto);

    EmployeeDto toDto(Employee employee);

    void updateFromDto(EmployeeDto employeeDto, @MappingTarget Employee employee);
}

