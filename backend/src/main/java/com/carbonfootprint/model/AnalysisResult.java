package com.carbonfootprint.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime analysisTime;
    private Double totalEnergy;
    private Double totalCarbonFootprint;
    private Double totalRuntime;
    
    @OneToOne
    @JoinColumn(name = "script_id")
    private Script script;
    
    @OneToMany(mappedBy = "analysisResult", cascade = CascadeType.ALL)
    private List<TaskResult> tasks;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getAnalysisTime() { return analysisTime; }
    public void setAnalysisTime(LocalDateTime analysisTime) { this.analysisTime = analysisTime; }
    public Double getTotalEnergy() { return totalEnergy; }
    public void setTotalEnergy(Double totalEnergy) { this.totalEnergy = totalEnergy; }
    public Double getTotalCarbonFootprint() { return totalCarbonFootprint; }
    public void setTotalCarbonFootprint(Double totalCarbonFootprint) { this.totalCarbonFootprint = totalCarbonFootprint; }
    public Double getTotalRuntime() { return totalRuntime; }
    public void setTotalRuntime(Double totalRuntime) { this.totalRuntime = totalRuntime; }
    public Script getScript() { return script; }
    public void setScript(Script script) { this.script = script; }
    public List<TaskResult> getTasks() { return tasks; }
    public void setTasks(List<TaskResult> tasks) { this.tasks = tasks; }

    @Column(columnDefinition = "TEXT")
    private String rawData;

    public String getRawData() {
        return rawData;
    }
    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}