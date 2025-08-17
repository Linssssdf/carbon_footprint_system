package com.carbonfootprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.carbonfootprint.model.AnalysisResult;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
}