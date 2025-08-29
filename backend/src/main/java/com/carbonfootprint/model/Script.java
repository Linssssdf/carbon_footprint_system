package com.carbonfootprint.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String fileName;
    private String filePath;
    private LocalDateTime uploadTime;
    private LocalDateTime lastExecutionTime;
    private String status;
    private String hardwareConfig;

    public Script() {}
    
    @OneToOne(mappedBy = "script", cascade = CascadeType.ALL)
    @JsonIgnore
    private AnalysisResult result;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
    public LocalDateTime getLastExecutionTime() { return lastExecutionTime; }
    public void setLastExecutionTime(LocalDateTime lastExecutionTime) { this.lastExecutionTime = lastExecutionTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHardwareConfig() { return hardwareConfig; }
    public void setHardwareConfig(String hardwareConfig) { this.hardwareConfig = hardwareConfig; }
    public AnalysisResult getResult() { return result; }
    public void setResult(AnalysisResult result) { this.result = result; }
}