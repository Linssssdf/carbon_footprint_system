package com.carbonfootprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carbonfootprint.exception.ResourceNotFoundException;
import com.carbonfootprint.service.VisualizationService;
import com.carbonfootprint.service.VisualizationService.VisualizationData;

import org.slf4j.Logger;

@RestController
@RequestMapping("/api/visualization")
public class VisualizationController {
    
    @Autowired
    private VisualizationService visualizationService;
    
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(VisualizationController.class);

    @GetMapping("/{resultId}")
    public ResponseEntity<?> getVisualizationData(@PathVariable Long resultId) {
        try {
            VisualizationData data = visualizationService.getVisualizationData(resultId);
            
            if (data == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Visualization data not found for resultId: " + resultId + "\"}");
            }
            
            logger.info("Returning visualization data: {}", data);
            logger.info("Tasks count: {}", data.getTasks().size());
            if (data.getTasks() != null && !data.getTasks().isEmpty()) {
                logger.info("First task: {}", data.getTasks().get(0));
            }
            
            // 直接返回VisualizationData对象，它包含所有必要的数据
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("Error retrieving visualization data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error retrieving visualization data: " + e.getMessage() + "\"}");
        }
    }
}