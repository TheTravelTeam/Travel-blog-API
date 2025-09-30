package com.wcs.travel_blog.step.controller;

import com.wcs.travel_blog.step.dto.StepRequestDTO;
import com.wcs.travel_blog.step.dto.StepResponseDTO;
import com.wcs.travel_blog.step.service.StepService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/steps")
public class StepController {

    private final StepService stepService;

    public StepController(StepService stepService) {
        this.stepService = stepService;
    }

    @GetMapping
    public ResponseEntity<List<StepResponseDTO>> getAllSteps() {
        List<StepResponseDTO> steps = stepService.getAllSteps();
        if (steps.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(steps);
    }

    @GetMapping("/{stepId}")
    public ResponseEntity<StepResponseDTO> getStepById(@PathVariable Long stepId) {
        StepResponseDTO stepDTo = stepService.getStepById(stepId);
        return ResponseEntity.ok(stepDTo);
    }

    @PostMapping
    public ResponseEntity<StepResponseDTO> createStep(@Valid @RequestBody StepRequestDTO stepDTo) {
        StepResponseDTO createdStep = stepService.createStep(stepDTo);
        return ResponseEntity.status(201).body(createdStep);
    }

    @PutMapping("/{stepId}")
    public ResponseEntity<StepResponseDTO> updateStep(@PathVariable Long stepId, @Valid @RequestBody StepRequestDTO stepDTo) {
        StepResponseDTO updatedStep = stepService.updateStep(stepId, stepDTo);
        return ResponseEntity.ok(updatedStep);
    }

    @DeleteMapping("/{stepId}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long stepId) {
        try {
            stepService.deleteStep(stepId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
