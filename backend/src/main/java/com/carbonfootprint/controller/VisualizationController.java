package com.carbonfootprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carbonfootprint.service.VisualizationService;

@RestController
@RequestMapping("/api/visualization")
public class VisualizationController {
    
    @Autowired
    private VisualizationService visualizationService;
    
    @GetMapping("/{resultId}")
    public ResponseEntity<?> getVisualizationData(@PathVariable Long resultId) {
        return ResponseEntity.ok(visualizationService.getVisualizationData(resultId));
    }
}