package com.carbonfootprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "python")
public class PythonConfig {
    private String executable;

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    private String analysisScript;

    public String getAnalysisScript() {
        return analysisScript;
    }

    public void setAnalysisScript(String analysisScript) {
        this.analysisScript = analysisScript;
    }
}
