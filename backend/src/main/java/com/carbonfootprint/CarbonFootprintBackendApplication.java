package com.carbonfootprint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carbonfootprint.config.FileStorageConfig;
import com.carbonfootprint.config.PythonConfig;

@SpringBootApplication(scanBasePackages = {
    "com.carbonfootprint.config",
    "com.carbonfootprint.controller",
    "com.carbonfootprint.service",
    "com.carbonfootprint.repository"
})
@EnableConfigurationProperties({FileStorageConfig.class, PythonConfig.class}) // 确保 FileStorageConfig.class 在这里
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
}