package com.carbonfootprint.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carbonfootprint.exception.ResourceNotFoundException;
import com.carbonfootprint.model.AnalysisResult;
import com.carbonfootprint.model.Script;
import com.carbonfootprint.model.TaskResult;
import com.carbonfootprint.repository.AnalysisResultRepository;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class AnalysisService {
    
    @Autowired
    private AnalysisResultRepository resultRepository;

    // 在AnalysisService类中添加
    public AnalysisResult getAnalysisResultById(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis result not found with id: " + id));
    }
    
    public AnalysisResult saveAnalysisResult(Script script, JsonNode rootNode) {
        AnalysisResult result = new AnalysisResult();
        result.setScript(script);
        result.setAnalysisTime(LocalDateTime.now());
        
        // 提取总体指标
        JsonNode summary = rootNode.path("summary");
        result.setTotalEnergy(summary.path("total_energy").asDouble());
        result.setTotalCarbonFootprint(summary.path("total_carbon_footprint").asDouble());
        result.setTotalRuntime(summary.path("total_runtime").asDouble());
        
        // 提取任务级结果
        List<TaskResult> tasks = new ArrayList<>();
        JsonNode tasksNode = rootNode.path("tasks");
        for (JsonNode taskNode : tasksNode) {
            TaskResult task = new TaskResult();
            task.setTaskName(taskNode.path("name").asText());
            task.setEnergyConsumption(taskNode.path("energy").asDouble());
            task.setCarbonFootprint(taskNode.path("carbon_footprint").asDouble());
            task.setRuntime(taskNode.path("runtime").asDouble());
            task.setHardware(taskNode.path("hardware").asText());
            task.setCpuUsage(taskNode.path("cpu_usage").asDouble());
            task.setMemoryUsage(taskNode.path("memory_usage").asDouble());
            task.setAnalysisResult(result);
            tasks.add(task);
        }
        
        result.setTasks(tasks);
        return resultRepository.save(result);
    }
}
