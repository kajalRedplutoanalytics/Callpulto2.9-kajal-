package com.redplutoanalytics.callpluto.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.redplutoanalytics.callpluto.service.PythonTriggerService;

//@RestController
//@RequestMapping("/api/upload")
//public class FileUploadController {
//
//    private final String uploadDir = new File("python-ai/upload").getAbsolutePath();
//    private final String completeDir = new File("python-ai/complete").getAbsolutePath();
//
//    @Autowired
//    private PythonTriggerService pythonTriggerService;
//
//    @PostMapping("/folder")
//    public ResponseEntity<String> uploadFolder(@RequestParam("files") MultipartFile[] files) {
//        List<String> uploadedAudioFiles = new ArrayList<>();
//
//        try {
//            new File(uploadDir).mkdirs();
//            new File(completeDir).mkdirs();
//
//            for (MultipartFile file : files) {
//                if (file.isEmpty()) continue;
//
//                String originalPath = file.getOriginalFilename();
//                String filename = new File(originalPath).getName(); // Remove folder prefix
//                String contentType = file.getContentType();
//
//                if (contentType == null || !contentType.startsWith("audio/")) {
//                    System.out.println("❌ Skipped non-audio file: " + filename);
//                    continue;
//                }
//
//               File uploadFile = new File(uploadDir, filename);
//                
//               
//
//                
//                file.transferTo(uploadFile);
//                System.out.println("✅ Saved to uploads: " + uploadFile.getAbsolutePath());
//
//                File completeFile = new File(completeDir, filename);
//                Files.copy(uploadFile.toPath(), completeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("✅ Copied to complete: " + completeFile.getAbsolutePath());
//
//                uploadedAudioFiles.add(filename);
//            }
//
//            if (!uploadedAudioFiles.isEmpty()) {
//                pythonTriggerService.runPythonOnFolder(uploadDir);
//            }
//
//            return ResponseEntity.ok("✅ Uploaded audio files: " + String.join(", ", uploadedAudioFiles));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("❌ File upload failed: " + e.getMessage());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return ResponseEntity.status(500).body("❌ Unexpected error: " + ex.getMessage());
//        }
//    }
//}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.redplutoanalytics.callpluto.service.PythonTriggerService;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private PythonTriggerService pythonTriggerService;

    /**
     * Accepts a server-accessible file path from the client, updates UPLOAD_DIR in path_config.py,
     * and triggers the Python script.
     */
    @PostMapping("/folder")
    public ResponseEntity<?> setFilePathAndRun(@RequestParam("path") String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return ResponseEntity.badRequest().body("❌ File does not exist: " + filePath);
            }

            // Update UPLOAD_DIR in path_config.py
            updatePathConfig(file.getAbsolutePath());

            // Trigger Python processing
            pythonTriggerService.runPythonOnFolder(file.getAbsolutePath());

            return ResponseEntity.ok("✅ UPLOAD_DIR set in path_config.py and Python triggered for: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Overwrites UPLOAD_DIR in python-ai/path_config.py with the given path, keeping LOG_DIR unchanged.
     */
    private void updatePathConfig(String newPath) throws Exception {
        String baseDir = System.getProperty("user.dir");
        File configFile = new File(baseDir + File.separator + "python-ai" + File.separator + "config" + File.separator +  "path_config.py");

        List<String> lines = Files.readAllLines(configFile.toPath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, false))) {
            for (String line : lines) {
                if (line.trim().startsWith("UPLOAD_DIR")) {
                    writer.write("UPLOAD_DIR = r'" + newPath.replace("\\", "\\\\") + "'");
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        }
        System.out.println("📄 Updated UPLOAD_DIR in path_config.py to: " + newPath);
    }
}
