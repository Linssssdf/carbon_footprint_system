package com.carbonfootprint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carbonfootprint.config.FileStorageConfig;
import com.carbonfootprint.config.PythonConfig;

import jakarta.annotation.PostConstruct;

import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = {
    "com.carbonfootprint.config",
    "com.carbonfootprint.controller",
    "com.carbonfootprint.service",
    "com.carbonfootprint.repository"
})
@EnableConfigurationProperties({FileStorageConfig.class, PythonConfig.class})
public class CarbonFootprintBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarbonFootprintBackendApplication.class, args);
    }

    @RestController
    class HealthController {
        @GetMapping("/")
        public String healthCheck() {
            return "Backend is running! " + java.time.LocalDateTime.now();
        }
    }

    @Autowired
    private Environment env;

    @PostConstruct
    public void verifyConfig() {
        String serverPort = env.getProperty("server.port");
        System.out.println("Server Port from config: " + serverPort);
        
        String pythonScript = env.getProperty("python.analysis-script");
        System.out.println("Python script path from config: " + pythonScript);
        
        // 你还可以打印其他属性来验证
        String uploadDir = env.getProperty("file.upload-dir");
        System.out.println("Upload directory: " + uploadDir);
    }
}