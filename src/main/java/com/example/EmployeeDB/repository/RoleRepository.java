package com.example.EmployeeDB.repository;

import com.example.EmployeeDB.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String name);
    // Custom query to count users with a specific role
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    long countByRoleName(@Param("roleName") String roleName);
}

