package com.carbonfootprint.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.carbonfootprint.config.PythonConfig;
import com.carbonfootprint.exception.ScriptExecutionException;
import com.carbonfootprint.model.AnalysisResult;
import com.carbonfootprint.model.Script;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class PythonRunnerService {
    
    @Autowired
    private PythonConfig pythonConfig;
    
    @Autowired
    private AnalysisService analysisService;

    public AnalysisResult analyzeTraceFile(Script script) {
        try {
            // 获取分析脚本的绝对路径
            Resource resource = new ClassPathResource(pythonConfig.getAnalysisScript());
            File analysisScript = resource.getFile();
            
            List<String> command = new ArrayList<>();
            command.add(pythonConfig.getExecutable());
            command.add(analysisScript.getAbsolutePath());
            command.add(script.getFilePath());
            
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new ScriptExecutionException("Analysis failed with exit code: " + exitCode);
            }
            
            // 解析JSON输出
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(output.toString());
            
            // 保存分析结果
            return analysisService.saveAnalysisResult(script, rootNode);
        } catch (IOException | InterruptedException e) {
            throw new ScriptExecutionException("Failed to analyze trace file", e);
        }
    }
    
    public AnalysisResult runScript(Script script) {
        try {
            // 准备Python命令
            List<String> command = new ArrayList<>();
            command.add(pythonConfig.getExecutable());
            command.add(script.getFilePath());
            
            // 添加硬件配置参数
            if (StringUtils.hasText(script.getHardwareConfig())) {
                command.add("--hardware");
                command.add(script.getHardwareConfig());
            }
            
            // 执行脚本
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new ScriptExecutionException("Script execution failed with exit code: " + exitCode);
            }
            
            // 解析输出为JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(output.toString());
            
            // 保存分析结果
            return analysisService.saveAnalysisResult(script, rootNode);
        } catch (IOException | InterruptedException e) {
            throw new ScriptExecutionException("Failed to execute Python script", e);
        }
    }
}
