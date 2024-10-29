package com.example.EmployeeDB.repository;

import com.example.EmployeeDB.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByEmployeesEmployeeId(Long employeeId);
}