package com.carbonfootprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.carbonfootprint.model.Script;

public interface ScriptRepository extends JpaRepository<Script, Long> {
}