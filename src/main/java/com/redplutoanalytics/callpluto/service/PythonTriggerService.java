package com.redplutoanalytics.callpluto.service;




import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class PythonTriggerService {
 
    private static final Logger logger = LoggerFactory.getLogger(PythonTriggerService.class);
 
    public void runPythonOnFolder(String folderPath) {
        try {
            logger.info("🚀 Starting Python script on folder: {}", folderPath);
 
            String baseDir = System.getProperty("user.dir");
            String workingDir = baseDir + File.separator + "python-ai";
            String venvDir = workingDir + File.separator + "venv";
 
 
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String pythonExePath = isWindows
                    ? venvDir + File.separator + "Scripts" + File.separator + "python.exe"
                    : venvDir + File.separator + "bin" + File.separator + "python";
 
            File pythonExe = new File(pythonExePath);
 
     
            if (!pythonExe.exists()) {
                logger.info("⚡ Virtual environment not found. Creating one...");
                createVirtualEnv(workingDir, venvDir, isWindows);
            }
 
       
//            File requirementsFile = new File(workingDir + File.separator + "requirements.txt");
//            if (requirementsFile.exists()) {
//                installRequirements(pythonExePath, requirementsFile);
//            }
// 
     
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonExePath,
                    "main.py",
                    folderPath
            );
            processBuilder.directory(new File(workingDir));
            processBuilder.redirectErrorStream(true);
 
            Process process = processBuilder.start();
 
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("PYTHON OUTPUT: {}", line);
                }
            }
 
            int exitCode = process.waitFor();
            logger.info("✅ Python script exited with code: {}", exitCode);
 
        } catch (Exception e) {
            logger.error("❌ Error running Python script: ", e);
        }
    }
 
    private void createVirtualEnv(String workingDir, String venvDir, boolean isWindows) throws IOException, InterruptedException {
        String pythonCmd = isWindows ? "python" : "python3"; // System Python
        ProcessBuilder pb = new ProcessBuilder(pythonCmd, "-m", "venv", venvDir);
        pb.directory(new File(workingDir));
        pb.redirectErrorStream(true);
 
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("VENV OUTPUT: {}", line);
            }
        }
 
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            logger.info("✅ Virtual environment created successfully at {}", venvDir);
        } else {
            throw new RuntimeException("❌ Failed to create virtual environment. Exit code: " + exitCode);
        }
    }
 
    private void installRequirements(String pythonExePath, File requirementsFile) throws IOException, InterruptedException {
        logger.info(" Installing Python dependencies from requirements.txt...");
 
        ProcessBuilder pb = new ProcessBuilder(
                pythonExePath,
                "-m",
                "pip",
                "install",
                "-r",
                requirementsFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
 
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("PIP OUTPUT: {}", line);
            }
        }
 
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            logger.info("✅ Dependencies installed successfully.");
        } else {
            throw new RuntimeException("❌ Failed to install dependencies. Exit code: " + exitCode);
        }
    }
}
