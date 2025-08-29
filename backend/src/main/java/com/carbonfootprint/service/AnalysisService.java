package com.carbonfootprint.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.carbonfootprint.exception.ResourceNotFoundException;
import com.carbonfootprint.model.AnalysisResult;
import com.carbonfootprint.model.Script;
import com.carbonfootprint.model.TaskResult;
import com.carbonfootprint.repository.AnalysisResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AnalysisService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PythonRunnerService.class);

    @Autowired
    private AnalysisResultRepository resultRepository;

    public AnalysisResult getAnalysisResultById(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis result not found with id: " + id));
    }
    
    // 保存分析结果
    public AnalysisResult saveAnalysisResult(Script script, JsonNode resultData) {
        ObjectMapper mapper = new ObjectMapper();
        AnalysisResult result = new AnalysisResult();
        
        JsonNode summary = resultData.get("summary");
        if (summary != null) {
            // 修改键名以匹配Python脚本的输出
            result.setTotalEnergy(summary.path("totalEnergy").asDouble(0.0));
            result.setTotalCarbonFootprint(summary.path("totalCarbonFootprint").asDouble(0.0));
            result.setTotalRuntime(summary.path("totalRuntime").asDouble(0.0));
            result.setAvgCpuUtilization(summary.path("avgCpuUtilization").asDouble(0.0));
        }

        // 保存完整的原始数据
        try {
            result.setRawData(mapper.writeValueAsString(resultData));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize raw data", e);
            result.setRawData("{}");
        }

        result.setScript(script);
        result.setAnalysisTime(LocalDateTime.now());

        return resultRepository.save(result);
    }    
    /**
     * Retrieves a summary of all analysis results for the dashboard.
     * This method correctly handles cases where analysis results might be incomplete (null values).
     *
     * @return DashboardSummary containing aggregated metrics.
     */
    public DashboardSummary getDashboardSummary() {
        // Find all analysis results
        List<AnalysisResult> allResults = resultRepository.findAll();

        // Use a stream to filter out any results with null values for key metrics
        List<AnalysisResult> validResults = allResults.stream()
                .filter(result -> result.getTotalEnergy() != null &&
                                 result.getTotalCarbonFootprint() != null &&
                                 result.getTotalRuntime() != null)
                .collect(Collectors.toList());

        // Check if the list of valid results is empty to avoid errors
        if (validResults.isEmpty()) {
            return new DashboardSummary(0.0, 0.0, 0.0, 0L);
        }

        // Aggregate metrics from valid results
        double totalEnergy = validResults.stream()
                .mapToDouble(AnalysisResult::getTotalEnergy)
                .sum();
        
        double totalCarbonFootprint = validResults.stream()
                .mapToDouble(AnalysisResult::getTotalCarbonFootprint)
                .sum();
        
        double avgRuntime = validResults.stream()
                .mapToDouble(AnalysisResult::getTotalRuntime)
                .average()
                .orElse(0.0); // Use orElse(0.0) to handle case where stream is empty

        long totalAnalyses = validResults.size();

        return new DashboardSummary(
            totalEnergy,
            totalCarbonFootprint,
            avgRuntime,
            totalAnalyses
        );
    }

    public List<AnalysisSummary> getRecentAnalyses(int limit) {
        return resultRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "analysisTime")))
                .stream()
                .map(result -> new AnalysisSummary(
                        result.getId(),
                        result.getScript().getFileName(),
                        result.getAnalysisTime(),
                        result.getTotalEnergy(),
                        result.getTotalCarbonFootprint(),
                        result.getTotalRuntime()
                ))
                .collect(Collectors.toList());
    }

    public List<EnergyConsumer> getTopEnergyConsumers(int limit) {
        List<TaskResult> allTasks = new ArrayList<>();
        resultRepository.findAll().forEach(result -> {
            if (result.getTasks() != null) {
                allTasks.addAll(result.getTasks());
            }
        });
        
        return allTasks.stream()
                .filter(task -> task.getEnergyConsumption() != null)
                .sorted(Comparator.comparing(TaskResult::getEnergyConsumption).reversed())
                .limit(limit)
                .map(task -> new EnergyConsumer(task.getProcess(), task.getEnergyConsumption(), task.getAnalysisResult().getId()))
                .collect(Collectors.toList());
    }
    
    // 内部类用于数据传输
    public static class DashboardSummary {
        private final double totalEnergy;
        private final double totalCarbonFootprint;
        private final double avgRuntime;
        private final long totalAnalyses;
        
        public DashboardSummary(double totalEnergy, double totalCarbonFootprint, double avgRuntime, long totalAnalyses) {
            this.totalEnergy = totalEnergy;
            this.totalCarbonFootprint = totalCarbonFootprint;
            this.avgRuntime = avgRuntime;
            this.totalAnalyses = totalAnalyses;
        }

        public double getTotalEnergy() { return totalEnergy; }
        public double getTotalCarbonFootprint() { return totalCarbonFootprint; }
        public double getAvgRuntime() { return avgRuntime; }
        public long getTotalAnalyses() { return totalAnalyses; }
    }

    public static class AnalysisSummary {
        private final Long id;
        private final String fileName;
        private final LocalDateTime analyzedAt;
        private final Double totalEnergy;
        private final Double totalCarbonFootprint;
        private final Double totalRuntime;

        public AnalysisSummary(Long id, String fileName, LocalDateTime analyzedAt, Double totalEnergy, Double totalCarbonFootprint, Double totalRuntime) {
            this.id = id;
            this.fileName = fileName;
            this.analyzedAt = analyzedAt;
            this.totalEnergy = totalEnergy;
            this.totalCarbonFootprint = totalCarbonFootprint;
            this.totalRuntime = totalRuntime;
        }

        public Long getId() { return id; }
        public String getFileName() { return fileName; }
        public LocalDateTime getAnalyzedAt() { return analyzedAt; }
        public Double getTotalEnergy() { return totalEnergy; }
        public Double getTotalCarbonFootprint() { return totalCarbonFootprint; }
        public Double getTotalRuntime() { return totalRuntime; }
    }
    
    public static class EnergyConsumer {
        private final String process;
        private final double energy;
        private final Long analysisId;
        
        public EnergyConsumer(String process, double energy, Long analysisId) {
            this.process = process;
            this.energy = energy;
            this.analysisId = analysisId;
        }

        public String getProcess() { return process; }
        public double getEnergy() { return energy; }
        public Long getAnalysisId() { return analysisId; }
    }
    // 导出分析结果到文件
    public void exportAnalysisResult(Long resultId, String filePath) {
        AnalysisResult result = getAnalysisResultById(resultId);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(result);
            
            Files.write(Paths.get(filePath), jsonData.getBytes(), StandardOpenOption.CREATE);
            logger.info("Exported analysis result {} to {}", resultId, filePath);
        } catch (IOException e) {
            logger.error("Failed to export analysis result {}", resultId, e);
            throw new RuntimeException("Export failed", e);
        }
    }

    // 从文件导入分析结果
    public AnalysisResult importAnalysisResult(String filePath) {
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonData, AnalysisResult.class);
        } catch (IOException e) {
            logger.error("Failed to import analysis result from {}", filePath, e);
            throw new RuntimeException("Import failed", e);
        }
    }
}