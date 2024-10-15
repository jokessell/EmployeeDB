// src/main/java/com/example/controller/SkillController.java
package com.example.controller;

import com.example.dto.SkillDto;
import com.example.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    // GET /api/skills?page=0&size=10&sort=name,asc
    @GetMapping
    public ResponseEntity<Page<SkillDto>> getAllSkills(Pageable pageable) {
        Page<SkillDto> skills = skillService.getAllSkills(pageable);
        return ResponseEntity.ok(skills);
    }

    // GET /api/skills/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SkillDto> getSkillById(@PathVariable Long id) {
        SkillDto skill = skillService.getSkillById(id);
        return ResponseEntity.ok(skill);
    }

    // POST /api/skills
    @PostMapping
    public ResponseEntity<SkillDto> createSkill(@Valid @RequestBody SkillDto skillDto) {
        SkillDto createdSkill = skillService.createSkill(skillDto);
        return ResponseEntity.ok(createdSkill);
    }

    // PUT /api/skills/{id}
    @PutMapping("/{id}")
    public ResponseEntity<SkillDto> updateSkill(@PathVariable Long id, @Valid @RequestBody SkillDto skillDto) {
        SkillDto updatedSkill = skillService.updateSkill(id, skillDto);
        return ResponseEntity.ok(updatedSkill);
    }

    // DELETE /api/skills/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
