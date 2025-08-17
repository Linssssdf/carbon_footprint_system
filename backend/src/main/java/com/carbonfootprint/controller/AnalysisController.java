package com.carbonfootprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carbonfootprint.model.AnalysisResult;
import com.carbonfootprint.service.AnalysisService;
import com.carbonfootprint.service.ScriptService;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {
    
    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private ScriptService scriptService;
    
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResult> getAnalysisResult(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getAnalysisResultById(id));
    }
    @PostMapping("/{id}/analyze")
    public ResponseEntity<AnalysisResult> analyzeScript(@PathVariable Long id) {
        return ResponseEntity.ok(scriptService.analyzeTraceFile(id));
    }
}