package com.example.EmployeeDB.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SKILL_TBL")
@Data
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

    @ManyToMany(mappedBy = "skills")
    @JsonIgnoreProperties({"skills", "projects"})
    private Set<Employee> employees = new HashSet<>();

    @ManyToMany(mappedBy = "skills")
    @JsonIgnoreProperties({"skills", "employees"})
    private Set<Project> projects = new HashSet<>();
}
