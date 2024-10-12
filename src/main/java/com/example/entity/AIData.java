package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "AI_DATA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Lob
    @Column(columnDefinition = "CLOB") // For H2 compatibility
    private String data; // JSON data as a string

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Optional: Add more fields if needed
}
