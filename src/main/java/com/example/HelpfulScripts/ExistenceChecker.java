package com.mcl; 

import org.python.util.PythonInterpreter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

public class ExistenceChecker {
    public static Boolean version_exist(String version) {  
        try {
            InputStream pyScriptStream = ExistenceChecker.class.getClassLoader().getResourceAsStream("scripts/path_checker.py");
            if (pyScriptStream == null) {
                System.out.println("Script not found");
                return false;
            }

            java.nio.file.Path tempScript = java.nio.file.Files.createTempFile("path_checker", ".py");
            java.nio.file.Files.copy(pyScriptStream, tempScript, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            pyScriptStream.close();

            String os = System.getProperty("os.name").toLowerCase();
            String pythonCommand = os.contains("win") ? "python" : "python3";

            String[] cmd = {
                pythonCommand,
                tempScript.toString(),
                version
            };

            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            java.nio.file.Files.delete(tempScript);

            if (exitCode == 0) {
                String result = output.toString().trim();
                System.out.println("Existence: " + result);
                return Boolean.parseBoolean(result);
            } else {
                System.out.println("Error: Process exited with non-zero status code " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}

