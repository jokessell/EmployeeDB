package com.example.EmployeeDB.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ROLE_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_NAME", unique = true, nullable = false)
    private String roleName;
}
