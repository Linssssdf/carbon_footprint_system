package com.carbonfootprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.carbonfootprint.model.AnalysisResult;
import com.carbonfootprint.repository.AnalysisResultRepository;
import com.carbonfootprint.service.AnalysisService;
import com.carbonfootprint.service.ScriptService;
import com.carbonfootprint.service.AnalysisService.AnalysisSummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class AnalysisController {
    
    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private ScriptService scriptService;

    @Autowired
    private AnalysisResultRepository resultRepository;
    
    // 将端点从 /analysis/{id} 修改为 /analyses/{id}
    @GetMapping("/analyses/{id}")
    public ResponseEntity<AnalysisResult> getAnalysisResult(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getAnalysisResultById(id));
    }

    @PostMapping("/analysis/{id}/analyze")
    public ResponseEntity<AnalysisResult> analyzeScript(@PathVariable Long id) {
        return ResponseEntity.ok(scriptService.analyzeTraceFile(id));
    }
    
    @GetMapping("/analyses")
    public ResponseEntity<List<AnalysisSummary>> getAllAnalyses() {
        List<AnalysisResult> allResults = resultRepository.findAll();
        List<AnalysisSummary> summaries = allResults.stream()
            .map(result -> new AnalysisSummary(
                result.getId(),
                result.getScript().getFileName(),
                result.getAnalysisTime(),
                result.getTotalEnergy(),
                result.getTotalCarbonFootprint(),
                result.getTotalRuntime()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(summaries);
    }
}