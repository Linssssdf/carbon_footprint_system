package com.carbonfootprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.carbonfootprint.model.Script;
import com.carbonfootprint.service.ScriptService;

@RestController
@RequestMapping("/api/data-sources")
public class FileUploadController {
    
    @Autowired
    private ScriptService scriptService;
    
    @PostMapping("/upload")
    public ResponseEntity<Script> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Script script = scriptService.storeTraceFile(file);
            return ResponseEntity.ok(script);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}