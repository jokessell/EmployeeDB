package com.example.controller;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import com.example.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeDto employeeDto) {
        Employee createdEmployee = employeeService.createEmployee(employeeDto);
        return ResponseEntity.ok(createdEmployee);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Employee>> createEmployeesBatch(@RequestBody List<EmployeeDto> employeeDtos) {
        List<Employee> createdEmployees = employeeService.createEmployeesBatch(employeeDtos);
        return ResponseEntity.ok(createdEmployees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
