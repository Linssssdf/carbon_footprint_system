package com.carbonfootprint.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String process;
    private Double energyConsumption;
    private Double carbonFootprint;
    private Double runtime;
    private String hardware;
    private Double cpuUsage;
    private Double memoryUsage;
    
    @ManyToOne
    @JoinColumn(name = "result_id")
    @JsonBackReference
    private AnalysisResult analysisResult;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProcess() { return process; }
    public void setProcess(String process) { this.process = process; }
    public Double getEnergyConsumption() { return energyConsumption; }
    public void setEnergyConsumption(Double energyConsumption) { this.energyConsumption = energyConsumption; }
    public Double getCarbonFootprint() { return carbonFootprint; }
    public void setCarbonFootprint(Double carbonFootprint) { this.carbonFootprint = carbonFootprint; }
    public Double getRuntime() { return runtime; }
    public void setRuntime(Double runtime) { this.runtime = runtime; }
    public String getHardware() { return hardware; }
    public void setHardware(String hardware) { this.hardware = hardware; }
    public Double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(Double cpuUsage) { this.cpuUsage = cpuUsage; }
    public Double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }
    public AnalysisResult getAnalysisResult() { return analysisResult; }
    public void setAnalysisResult(AnalysisResult analysisResult) { this.analysisResult = analysisResult; }
}