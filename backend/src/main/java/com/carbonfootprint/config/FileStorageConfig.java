package com.carbonfootprint.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.io.File;
import jakarta.annotation.PostConstruct; 

@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageConfig {
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        if (uploadDir == null || uploadDir.isBlank()) {
            throw new IllegalStateException("'file.upload-dir' property is not set in application.properties");
        }
        
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            boolean created = uploadDirFile.mkdirs();
            if (created) {
                System.out.println("Created upload directory: " + uploadDir);
            } else {
                System.err.println("Failed to create upload directory: " + uploadDir);
            }
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}