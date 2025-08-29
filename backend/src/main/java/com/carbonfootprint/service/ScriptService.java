package com.carbonfootprint.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import com.carbonfootprint.config.FileStorageConfig;
import com.carbonfootprint.config.PythonConfig;
import com.carbonfootprint.exception.FileStorageException;
import com.carbonfootprint.exception.ResourceNotFoundException;
import com.carbonfootprint.exception.ScriptExecutionException;
import com.carbonfootprint.model.AnalysisResult;
import com.carbonfootprint.model.Script;
import com.carbonfootprint.repository.ScriptRepository;

@Service
public class ScriptService {
    
    @Autowired
    private ScriptRepository scriptRepository;
    
    @Autowired
    private PythonRunnerService pythonRunnerService;
    @Autowired
    private FileStorageConfig fileStorageConfig;

    @Autowired
    private PythonConfig pythonConfig;

    private static final Logger logger = LoggerFactory.getLogger(ScriptService.class);
    
    // ScriptService.java - 添加详细日志
    public Script storeTraceFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        logger.info("尝试存储文件: {}", fileName);
        
        try {
            // 验证文件扩展名
            if (!fileName.endsWith(".csv")) {
                logger.error("无效的文件类型: {}", fileName);
                throw new FileStorageException("无效的文件类型. 只允许CSV文件");
            }
            
            // 获取上传目录
            String uploadDirPath = fileStorageConfig.getUploadDir();
            logger.info("上传目录: {}", uploadDirPath);
            
            // 创建目标路径
            Path targetLocation = Paths.get(uploadDirPath).resolve(fileName);
            logger.info("目标路径: {}", targetLocation.toAbsolutePath());
            
            // 确保父目录存在
            Files.createDirectories(targetLocation.getParent());
            
            // 保存文件
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.info("文件存储成功: {}", fileName);
            
            // 保存元数据到数据库
            Script script = new Script();
            script.setFileName(fileName);
            script.setFilePath(targetLocation.toAbsolutePath().toString());
            script.setUploadTime(LocalDateTime.now());
            script.setStatus("UPLOADED");
            
            Script savedScript = scriptRepository.save(script);
            logger.info("脚本元数据保存成功，ID: {}", savedScript.getId());
            
            return savedScript;
        } catch (IOException ex) {
            logger.error("文件存储失败: {}", ex.getMessage());
            logger.error("堆栈跟踪:", ex);
            throw new FileStorageException("无法存储文件 " + fileName, ex);
        }
    }

    public AnalysisResult analyzeTraceFile(Long scriptId) {
        Script script = getScriptById(scriptId);
        logger.info("Starting analysis for script ID: {}", scriptId);
        
        // 验证是否为 trace 文件
        if (!script.getFileName().endsWith(".csv")) {
            throw new ScriptExecutionException("Only CSV trace files can be analyzed");
        }
        
        try {
            // 执行分析
            AnalysisResult result = pythonRunnerService.analyzeTraceFile(script);
            logger.info("Analysis completed successfully, result ID: {}", result.getId());
            
            // 更新脚本状态
            script.setStatus("ANALYZED");
            scriptRepository.save(script);
            logger.info("Script status updated to ANALYZED");
            
            return result;
        } catch (Exception ex) {
            logger.error("Analysis failed for script ID: {}", scriptId, ex);
            script.setStatus("ANALYSIS_FAILED");
            scriptRepository.save(script);
            throw ex;
        }
    }
    public Script getScriptById(Long id) {
        return scriptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Script not found with id: " + id));
    }
}
