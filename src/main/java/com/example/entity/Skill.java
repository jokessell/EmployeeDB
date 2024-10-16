// src/main/java/com/example/entity/Skill.java
package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SKILL_TBL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"employees", "projects"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SKILL_ID")
    @EqualsAndHashCode.Include
    private Long skillId;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    // Many-to-Many with Employee
    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<Employee> employees = new HashSet<>();

    // Many-to-Many with Project
    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<Project> projects = new HashSet<>();
}
