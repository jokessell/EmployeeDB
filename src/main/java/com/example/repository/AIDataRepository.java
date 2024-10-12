package com.example.repository;

import com.example.entity.AIData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AIDataRepository extends JpaRepository<AIData, Long> {
    Optional<AIData> findByTopic(String topic);
    void deleteByTopic(String topic);
}
