package com.example.EmployeeDB.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USER_TBL")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USER_NAME", unique = true, nullable = false)
    private String userName;

    @Column(name = "USER_PASSWORD", nullable = false)
    private String password; // Encrypted password

    @ManyToMany
    @JoinTable(
            name = "USER_ROLE_TBL",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles = new HashSet<>();
}
