package com.redplutoanalytics.callpluto.controller;
 
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
 
import com.redplutoanalytics.callpluto.dto.AudioFileDTO;
import com.redplutoanalytics.callpluto.dto.DashboardFilterDTO;
import com.redplutoanalytics.callpluto.service.AudioLibraryService;
 
@RestController
@RequestMapping("/api/quality/audio")
@CrossOrigin
public class AudioLibraryController {
 
    @Autowired
    private AudioLibraryService audioFileService;
 
    private final String uploadDir = System.getProperty("user.dir") + "/python-ai/complete/";
 
    
    
 
    
   
    @GetMapping("/stream/{filename}")
    public ResponseEntity<Resource> streamAudio(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir, filename);
            File file = filePath.toFile();
            if (!file.exists()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
 
            Resource resource = new FileSystemResource(file);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";
 
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
 
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
 
    
    
    @GetMapping("/all")
    public ResponseEntity<List<AudioFileDTO>> getFilteredAudioFiles(
            @RequestParam(required = false) String rmName,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String product,
            @RequestParam(required = false) String callType,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate ,
            @RequestParam(required = false) Boolean mismatchOnly ){
 
        DashboardFilterDTO filterDTO = new DashboardFilterDTO();
        filterDTO.setRmName(rmName);
        filterDTO.setRegion(region);
        filterDTO.setDepartment(department);
        filterDTO.setProduct(product);
        filterDTO.setCallType(callType);
        filterDTO.setMismatchOnly(mismatchOnly);
 
 
        List<AudioFileDTO> result = audioFileService.getFilteredAudioFiles(filterDTO);
        return ResponseEntity.ok(result);
    }
    
    
    
    @PostMapping("/excel-upload/trading")
    public ResponseEntity<String> excelUploadTrading(@RequestParam("file") MultipartFile file) {
        try {
            audioFileService.excelUpload(file, "trading");
            return ResponseEntity.ok("Trading order Excel file uploaded and data saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload trading order: " + e.getMessage());
        }
    }
 
    @PostMapping("/excel-upload/recording")
    public ResponseEntity<String> excelUploadRecording(@RequestParam("file") MultipartFile file) {
        try {
            audioFileService.excelUpload(file, "recording");
            return ResponseEntity.ok("Recording order Excel file uploaded and data saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload recording order: " + e.getMessage());
        }
    }
    
    
}

