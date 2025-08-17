package com.carbonfootprint.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carbonfootprint.exception.ResourceNotFoundException;
import com.carbonfootprint.model.AnalysisResult;
import com.carbonfootprint.model.TaskResult;
import com.carbonfootprint.repository.AnalysisResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VisualizationService {
    
    @Autowired
    private AnalysisResultRepository resultRepository;
    

    private TaskResult findMostEfficientTask(AnalysisResult result) {
        return result.getTasks().stream()
                .min((t1, t2) -> Double.compare(calculateEfficiency(t1), calculateEfficiency(t2)))
                .orElse(null);
    }

    private TaskResult findLeastEfficientTask(AnalysisResult result) {
        return result.getTasks().stream()
                .max((t1, t2) -> Double.compare(calculateEfficiency(t1), calculateEfficiency(t2)))
                .orElse(null);
    }

    private double calculateAverageEfficiency(AnalysisResult result) {
        return result.getTasks().stream()
                .mapToDouble(this::calculateEfficiency)
                .average()
                .orElse(0.0);
    }

    public JsonNode getVisualizationData(Long resultId) {
        AnalysisResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis result not found"));
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(result.getRawData());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse analysis data", e);
        }
    }
    
    private double calculateEfficiency(TaskResult task) {
        // 根据能耗和运行时间计算效率
        return task.getRuntime() > 0 ? 
               task.getEnergyConsumption() / task.getRuntime() : 0;
    }

    
    
    private Map<String, Object> analyzeEfficiency(AnalysisResult result) {
        // 效率分析逻辑...
        return Map.of(
            "mostEfficientTask", findMostEfficientTask(result),
            "leastEfficientTask", findLeastEfficientTask(result),
            "averageEfficiency", calculateAverageEfficiency(result)
        );
    }
}