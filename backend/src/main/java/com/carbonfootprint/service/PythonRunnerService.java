package com.carbonfootprint.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PythonRunnerService.class);
    
    @Autowired
    private PythonConfig pythonConfig;
    
    @Autowired
    private AnalysisService analysisService;

    public AnalysisResult analyzeTraceFile(Script script) {
        File analysisScript = null;
        boolean isTempFile = false;
        
        try {
            logger.info("Python script path from config: {}", pythonConfig.getAnalysisScript());
            
            // Get classpath resources
            Resource resource = new ClassPathResource(pythonConfig.getAnalysisScript());
            
            if (!resource.exists()) {
                throw new ScriptExecutionException("Analysis script not found in classpath: " + 
                        pythonConfig.getAnalysisScript());
            }
            
            // Check if the resource is in the JAR file
            String resourceUri = resource.getURI().toString();
            logger.info("Resource URI: {}", resourceUri);
            
            if (resourceUri.startsWith("jar:")) {
                // The resource is in the JAR file and needs to be extracted to a temporary file
                logger.info("Script is inside JAR file, extracting to temporary file");
                
                // Create temporary files
                analysisScript = File.createTempFile("analysis", ".py");
                try (InputStream in = resource.getInputStream()) {
                    Files.copy(in, analysisScript.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                isTempFile = true;
                logger.info("Extracted script to temporary file: {}", analysisScript.getAbsolutePath());
            } else {
                // The resource is in the file system
                analysisScript = resource.getFile();
                logger.info("Using script from filesystem: {}", analysisScript.getAbsolutePath());
            }
            
            if (analysisService == null) {
                throw new ScriptExecutionException("AnalysisService is not initialized");
            }
            
            List<String> command = new ArrayList<>();
            command.add(pythonConfig.getExecutable());
            command.add(analysisScript.getAbsolutePath());
            command.add(script.getFilePath());
            
            logger.info("Executing command: {}", command);
            
            // Execute the command
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // Read the output
            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                    logger.debug("Python output: {}", line);
                }
                
                // Read the wrong output
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                    logger.error("Python error: {}", line);
                }
            }
            
            int exitCode = process.waitFor();
            logger.info("Python process exited with code: {}", exitCode);
            
            if (exitCode != 0) {
                throw new ScriptExecutionException("Analysis failed with exit code: " + exitCode + 
                        ". Error output: " + errorOutput.toString());
            }
            
            // Check if the output is empty
            if (output.length() == 0) {
                throw new ScriptExecutionException("Python script produced no output");
            }
            
            // In the analyzeTraceFile method of PythonRunnerService.java
            String outputStr = output.toString();
            logger.info("Raw Python output: {}", outputStr);

            // Try extracting the JSON part (if the output contains non-JSON content)
            String jsonStr = extractJsonFromOutput(outputStr);
            if (jsonStr == null) {
                // 如果没有找到JSON，尝试直接使用整个输出
                jsonStr = outputStr;
                logger.warn("No JSON found in output, using entire output as JSON");
            }

            logger.info("Extracted JSON: {}", jsonStr);

            // 解析JSON输出
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonStr);

            // 添加日志检查解析后的数据
            logger.info("Parsed JSON - Has summary: {}", rootNode.has("summary"));
            if (rootNode.has("summary")) {
                JsonNode summary = rootNode.path("summary");
                logger.info("Summary values - Energy: {}, Carbon: {}", 
                    summary.path("total_energy").asDouble(),
                    summary.path("total_carbon_footprint").asDouble());
            }
            
            // 检查是否有错误状态
            if (rootNode.has("status") && "failed".equals(rootNode.get("status").asText())) {
                String errorMessage = rootNode.has("message") ? 
                        rootNode.get("message").asText() : "Unknown error in Python script";
                throw new ScriptExecutionException("Python script failed: " + errorMessage);
            }

            // 在解析JSON后添加数据验证
            if (rootNode.has("summary")) {
                JsonNode summary = rootNode.path("summary");
                double totalEnergy = summary.path("totalEnergy").asDouble();
                double totalCarbon = summary.path("totalCarbonFootprint").asDouble();
                
                // 检查数据是否异常
                if (totalEnergy > 1e9 || totalCarbon > 1e9) {
                    logger.warn("异常数据检测: 能源消耗或碳排放值过高, 可能单位转换错误");
                    // 可以在这里添加数据修正逻辑或抛出异常
                }
            }
            
            // 保存分析结果
            return analysisService.saveAnalysisResult(script, rootNode);
        } catch (IOException | InterruptedException e) {
            throw new ScriptExecutionException("Failed to analyze trace file: " + e.getMessage(), e);
        } finally {
            // 如果是临时文件，使用后删除
            if (isTempFile && analysisScript != null && analysisScript.exists()) {
                try {
                    analysisScript.delete();
                    logger.info("Deleted temporary script file: {}", analysisScript.getAbsolutePath());
                } catch (SecurityException e) {
                    logger.warn("Could not delete temporary file: {}", analysisScript.getAbsolutePath());
                }
            }
        }
    }

    // 添加辅助方法用于从输出中提取JSON
    private String extractJsonFromOutput(String output) {
        // 查找第一个 { 或 [ 字符（JSON的开始）
        int jsonStart = Math.max(output.indexOf('{'), output.indexOf('['));
        if (jsonStart < 0) {
            return null; // 没有找到JSON开始标记
        }
        
        // 查找最后一个 } 或 ] 字符（JSON的结束）
        int jsonEnd = Math.max(output.lastIndexOf('}'), output.lastIndexOf(']'));
        if (jsonEnd < jsonStart) {
            return null; // 结束标记在开始标记之前
        }
        
        // 提取JSON部分
        return output.substring(jsonStart, jsonEnd + 1);
    }
}