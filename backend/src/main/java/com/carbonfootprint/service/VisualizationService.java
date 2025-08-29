package com.carbonfootprint.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carbonfootprint.exception.ResourceNotFoundException;
import com.carbonfootprint.model.AnalysisResult;
import com.carbonfootprint.repository.AnalysisResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

@Service
public class VisualizationService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(VisualizationService.class);
    
    @Autowired
    private AnalysisResultRepository resultRepository;

    public VisualizationData getVisualizationData(Long resultId) {
        AnalysisResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis result not found"));
        
        logger.info("Raw data from database: {}", result.getRawData());
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode data = mapper.readTree(result.getRawData());
            
            // 提取摘要数据，添加默认值处理
            JsonNode summaryNode = data.path("summary");
            SummaryData summary = new SummaryData(
                summaryNode.path("totalEnergy").asDouble(0.0),
                summaryNode.path("totalCarbonFootprint").asDouble(0.0),
                summaryNode.path("totalRuntime").asDouble(0.0),
                summaryNode.path("avgCpuUtilization").asDouble(0.0)
            );

            // 提取任务数据
            List<TaskData> tasks = new ArrayList<>();
            if (data.has("tasks") && data.get("tasks").isArray()) {
                data.get("tasks").forEach(taskNode -> {
                    tasks.add(new TaskData(
                        taskNode.path("process").asText("Unknown"),
                        taskNode.path("energyConsumption").asDouble(0.0), // 使用驼峰命名
                        taskNode.path("carbonFootprint").asDouble(0.0),
                        taskNode.path("runtime").asDouble(0.0),
                        taskNode.path("cpu_usage").asDouble(0.0),
                        taskNode.path("memory_allocated").asDouble(0.0),
                        taskNode.path("hardware").asText("Unknown")
                    ));
                });
            }
            
            // 提取主机数据
            HostData hostData = null;
            if (data.has("hostData")) {
                JsonNode hostDataNode = data.get("hostData");
                List<String> hosts = new ArrayList<>();
                List<Integer> taskCounts = new ArrayList<>();
                List<Double> runtimes = new ArrayList<>();
                List<Double> cpuUtilizations = new ArrayList<>();
                List<Double> memoryAllocations = new ArrayList<>();
                List<Double> ioVolumes = new ArrayList<>();
                List<Double> energyConsumptions = new ArrayList<>();
                List<Double> carbonEmissions = new ArrayList<>();
                Map<String, String> processes = new HashMap<>();
                
                if (hostDataNode.has("hosts") && hostDataNode.get("hosts").isArray()) {
                    hostDataNode.get("hosts").forEach(host -> hosts.add(host.asText()));
                }
                
                if (hostDataNode.has("task_counts") && hostDataNode.get("task_counts").isArray()) {
                    hostDataNode.get("task_counts").forEach(count -> taskCounts.add(count.asInt()));
                }
                
                if (hostDataNode.has("runtimes") && hostDataNode.get("runtimes").isArray()) {
                    hostDataNode.get("runtimes").forEach(runtime -> runtimes.add(runtime.asDouble()));
                }
                
                if (hostDataNode.has("cpu_utilizations") && hostDataNode.get("cpu_utilizations").isArray()) {
                    hostDataNode.get("cpu_utilizations").forEach(cpu -> cpuUtilizations.add(cpu.asDouble()));
                }
                
                if (hostDataNode.has("memory_allocations") && hostDataNode.get("memory_allocations").isArray()) {
                    hostDataNode.get("memory_allocations").forEach(mem -> memoryAllocations.add(mem.asDouble()));
                }
                
                if (hostDataNode.has("io_volumes") && hostDataNode.get("io_volumes").isArray()) {
                    hostDataNode.get("io_volumes").forEach(io -> ioVolumes.add(io.asDouble()));
                }
                
                if (hostDataNode.has("energy_consumptions") && hostDataNode.get("energy_consumptions").isArray()) {
                    hostDataNode.get("energy_consumptions").forEach(energy -> energyConsumptions.add(energy.asDouble()));
                }
                
                if (hostDataNode.has("carbon_emissions") && hostDataNode.get("carbon_emissions").isArray()) {
                    hostDataNode.get("carbon_emissions").forEach(carbon -> carbonEmissions.add(carbon.asDouble()));
                }
                
                if (hostDataNode.has("processes")) {
                    JsonNode processesNode = hostDataNode.get("processes");
                    processesNode.fieldNames().forEachRemaining(host -> {
                        processes.put(host, processesNode.get(host).asText());
                    });
                }
                
                hostData = new HostData(hosts, taskCounts, runtimes, cpuUtilizations, 
                                       memoryAllocations, ioVolumes, energyConsumptions, 
                                       carbonEmissions, processes);
            }
            
            // 提取进程数据
            List<ProcessData> processData = new ArrayList<>();
            if (data.has("processData") && data.get("processData").isArray()) {
                data.get("processData").forEach(processNode -> {
                    processData.add(new ProcessData(
                        processNode.path("process").asText("Unknown"),
                        processNode.path("tasks").asInt(0),
                        processNode.path("runtime").asDouble(0.0),
                        processNode.path("cpu_usage").asDouble(0.0),
                        processNode.path("memory_allocated").asDouble(0.0),
                        processNode.path("io_volume").asDouble(0.0),
                        processNode.path("energy_consumption").asDouble(0.0),
                        processNode.path("carbon_footprint").asDouble(0.0),
                        processNode.path("read_gb").asDouble(0.0),
                        processNode.path("write_gb").asDouble(0.0)
                    ));
                });
            }
            
            return new VisualizationData(summary, tasks, hostData, processData);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse analysis data", e);
            throw new RuntimeException("Failed to parse analysis data", e);
        } catch (Exception e) {
            logger.error("Unexpected error parsing analysis data", e);
            throw new RuntimeException("Unexpected error parsing analysis data", e);
        }
    }

    // 静态内部类，用于封装可视化数据
    public static class VisualizationData {
        private final SummaryData summary;
        private final List<TaskData> tasks;
        private final HostData hostData;
        private final List<ProcessData> processData;

        public VisualizationData(SummaryData summary, List<TaskData> tasks, HostData hostData, List<ProcessData> processData) {
            this.summary = summary;
            this.tasks = tasks;
            this.hostData = hostData;
            this.processData = processData;
        }

        // Getters
        public SummaryData getSummary() { return summary; }
        public List<TaskData> getTasks() { return tasks; }
        public HostData getHostData() { return hostData; }
        public List<ProcessData> getProcessData() { return processData; }
    }

    public static class SummaryData {
        private final double totalEnergy;
        private final double totalCarbonFootprint;
        private final double totalRuntime;
        private final double avgCpuUtilization;

        public SummaryData(double totalEnergy, double totalCarbonFootprint, double totalRuntime, double avgCpuUtilization) {
            this.totalEnergy = totalEnergy;
            this.totalCarbonFootprint = totalCarbonFootprint;
            this.totalRuntime = totalRuntime;
            this.avgCpuUtilization = avgCpuUtilization;
        }

        // Getters - 使用驼峰命名法
        public double getTotalEnergy() { return totalEnergy; }
        public double getTotalCarbonFootprint() { return totalCarbonFootprint; }
        public double getTotalRuntime() { return totalRuntime; }
        public double getAvgCpuUtilization() { return avgCpuUtilization; }
    }

    public static class TaskData {
        private final String process;
        private final double energyConsumption;
        private final double carbonFootprint;
        private final double runtime;
        private final double cpuUsage;
        private final double memoryAllocated;
        private final String hardware;

        public TaskData(String process, double energyConsumption, double carbonFootprint, 
                    double runtime, double cpuUsage, double memoryAllocated, String hardware) {
            this.process = process;
            this.energyConsumption = energyConsumption;
            this.carbonFootprint = carbonFootprint;
            this.runtime = runtime;
            this.cpuUsage = cpuUsage;
            this.memoryAllocated = memoryAllocated;
            this.hardware = hardware;
        }

        // Getters - 使用驼峰命名法
        public String getProcess() { return process; }
        public double getEnergyConsumption() { return energyConsumption; }
        public double getCarbonFootprint() { return carbonFootprint; }
        public double getRuntime() { return runtime; }
        public double getCpuUsage() { return cpuUsage; }
        public double getMemoryAllocated() { return memoryAllocated; }
        public String getHardware() { return hardware; }
    }    

    public static class HostData {
        private final List<String> hosts;
        private final List<Integer> taskCounts;
        private final List<Double> runtimes;
        private final List<Double> cpuUtilizations;
        private final List<Double> memoryAllocations;
        private final List<Double> ioVolumes;
        private final List<Double> energyConsumptions;
        private final List<Double> carbonEmissions;
        private final Map<String, String> processes;

        public HostData(List<String> hosts, List<Integer> taskCounts, List<Double> runtimes, 
                       List<Double> cpuUtilizations, List<Double> memoryAllocations, 
                       List<Double> ioVolumes, List<Double> energyConsumptions, 
                       List<Double> carbonEmissions, Map<String, String> processes) {
            this.hosts = hosts;
            this.taskCounts = taskCounts;
            this.runtimes = runtimes;
            this.cpuUtilizations = cpuUtilizations;
            this.memoryAllocations = memoryAllocations;
            this.ioVolumes = ioVolumes;
            this.energyConsumptions = energyConsumptions;
            this.carbonEmissions = carbonEmissions;
            this.processes = processes;
        }

        // Getters
        public List<String> getHosts() { return hosts; }
        public List<Integer> getTaskCounts() { return taskCounts; }
        public List<Double> getRuntimes() { return runtimes; }
        public List<Double> getCpuUtilizations() { return cpuUtilizations; }
        public List<Double> getMemoryAllocations() { return memoryAllocations; }
        public List<Double> getIoVolumes() { return ioVolumes; }
        public List<Double> getEnergyConsumptions() { return energyConsumptions; }
        public List<Double> getCarbonEmissions() { return carbonEmissions; }
        public Map<String, String> getProcesses() { return processes; }
    }
    
    public static class ProcessData {
        private final String process;
        private final int tasks;
        private final double runtime;
        private final double cpuUsage;
        private final double memoryAllocated;
        private final double ioVolume;
        private final double energyConsumption;
        private final double carbonFootprint;
        private final double readGb;
        private final double writeGb;

        public ProcessData(String process, int tasks, double runtime, double cpuUsage, 
                          double memoryAllocated, double ioVolume, double energyConsumption, 
                          double carbonFootprint, double readGb, double writeGb) {
            this.process = process;
            this.tasks = tasks;
            this.runtime = runtime;
            this.cpuUsage = cpuUsage;
            this.memoryAllocated = memoryAllocated;
            this.ioVolume = ioVolume;
            this.energyConsumption = energyConsumption;
            this.carbonFootprint = carbonFootprint;
            this.readGb = readGb;
            this.writeGb = writeGb;
        }

        // Getters
        public String getProcess() { return process; }
        public int getTasks() { return tasks; }
        public double getRuntime() { return runtime; }
        public double getCpuUsage() { return cpuUsage; }
        public double getMemoryAllocated() { return memoryAllocated; }
        public double getIoVolume() { return ioVolume; }
        public double getEnergyConsumption() { return energyConsumption; }
        public double getCarbonFootprint() { return carbonFootprint; }
        public double getReadGb() { return readGb; }
        public double getWriteGb() { return writeGb; }
    }
}