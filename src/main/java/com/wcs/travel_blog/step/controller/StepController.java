package com.wcs.travel_blog.step.controller;

import com.wcs.travel_blog.step.dto.StepDTO;
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
    public ResponseEntity<List<StepDTO>> getAllSteps() {
        List<StepDTO> steps = stepService.getAllSteps();
        if (steps.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(steps);
    }

    @GetMapping("/{stepId}")
    public ResponseEntity<StepDTO> getStepById(@PathVariable Long stepId) {
        StepDTO stepDTo = stepService.getStepById(stepId);
        return ResponseEntity.ok(stepDTo);
    }

    @PostMapping
    public ResponseEntity<StepDTO> createStep(@Valid @RequestBody StepDTO stepDTo) {
        StepDTO createdStep = stepService.createStep(stepDTo);
        return ResponseEntity.status(201).body(createdStep);
    }

    @PutMapping("/{stepId}")
    public ResponseEntity<StepDTO> updateStep(@PathVariable Long stepId, @Valid @RequestBody StepDTO stepDTo) {
        StepDTO updatedStep = stepService.updateStep(stepId, stepDTo);
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
