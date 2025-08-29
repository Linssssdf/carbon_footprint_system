package com.carbonfootprint.controller;

import com.carbonfootprint.model.AnalysisResult;
import com.carbonfootprint.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/summary")
    public ResponseEntity<AnalysisService.DashboardSummary> getDashboardSummary() {
        return ResponseEntity.ok(analysisService.getDashboardSummary());
    }

    @GetMapping("/recent-analyses")
    public ResponseEntity<List<AnalysisService.AnalysisSummary>> getRecentAnalyses() {
        return ResponseEntity.ok(analysisService.getRecentAnalyses(5));
    }

    @GetMapping("/top-consumers")
    public ResponseEntity<List<AnalysisService.EnergyConsumer>> getTopEnergyConsumers() {
        return ResponseEntity.ok(analysisService.getTopEnergyConsumers(10));
    }

    @PostMapping("/export/{resultId}")
    public ResponseEntity<String> exportAnalysisResult(@PathVariable Long resultId) {
        String exportDir = "./exports/";
        String fileName = "analysis_" + resultId + "_" + System.currentTimeMillis() + ".json";
        String filePath = exportDir + fileName;
        
        try {
            Files.createDirectories(Paths.get(exportDir));
            analysisService.exportAnalysisResult(resultId, filePath);
            return ResponseEntity.ok("{\"filePath\": \"" + filePath + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Export failed: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/import")
    public ResponseEntity<AnalysisResult> importAnalysisResult(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath");
        try {
            AnalysisResult result = analysisService.importAnalysisResult(filePath);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}