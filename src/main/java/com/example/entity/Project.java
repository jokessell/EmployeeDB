// src/main/java/com/example/entity/Project.java
package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PROJECT_TBL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROJECT_ID")
    private Long projectId;

    @Column(name = "PROJECT_NAME", nullable = false)
    private String projectName;

    @Column(name = "DESCRIPTION")
    private String description;

    // Many-to-Many with Employee
    @ManyToMany(mappedBy = "projects")
    @JsonIgnore
    private Set<Employee> employees = new HashSet<>();

    // Many-to-Many with Skill
    @ManyToMany
    @JoinTable(
            name = "PROJECT_SKILL_TBL",
            joinColumns = @JoinColumn(name = "PROJECT_ID"),
            inverseJoinColumns = @JoinColumn(name = "SKILL_ID")
    )
    private Set<Skill> skills = new HashSet<>();
}
