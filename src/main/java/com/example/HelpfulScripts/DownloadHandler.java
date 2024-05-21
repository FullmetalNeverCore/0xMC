package com.mcl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Paths;


public class DownloadHandler {
    public static Boolean downloader() {
        try {
            String scriptPath = "scripts/download.py";
            InputStream pyScriptStream = InitializationHandler.class.getClassLoader().getResourceAsStream(scriptPath);
            if (pyScriptStream == null) {
                System.out.println("Generator script not found!");
                return false;
            }

            java.nio.file.Path tempScript = java.nio.file.Files.createTempFile("download", ".py");
            java.nio.file.Files.copy(pyScriptStream, tempScript, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            pyScriptStream.close();

            String os = System.getProperty("os.name").toLowerCase();
            String pythonCommand = os.contains("win") ? "python" : "python3";

            String[] cmd = {
                pythonCommand,
                tempScript.toString(),
                Paths.get("").toAbsolutePath().toString()
            };

            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = stdInput.readLine()) != null) {
                output.append(line).append("\n");
            }

            StringBuilder errorOutput = new StringBuilder();
            while ((line = stdError.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            java.nio.file.Files.delete(tempScript);

            if (exitCode == 0) {
                System.out.println("Output from downloader: " + output.toString().trim());
                return true;
            } else {
                System.out.println("Error: Process exited with non-zero status code " + exitCode);
                System.out.println("Error output: " + errorOutput.toString().trim());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
