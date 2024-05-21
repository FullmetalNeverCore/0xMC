package com.mcl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class InitializationHandler {
    public static void init_client(String name, String version, int allocram, String action) {
        try {
            // Get the path to the Python script
            String scriptPath = "scripts/init_client.py";
            InputStream pyScriptStream = InitializationHandler.class.getClassLoader().getResourceAsStream(scriptPath);
            if (pyScriptStream == null) {
                System.out.println("Generator script not found!");
                return;
            }

            // Create a temporary file for the script
            java.nio.file.Path tempScript = java.nio.file.Files.createTempFile("init_client", ".py");
            java.nio.file.Files.copy(pyScriptStream, tempScript, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            pyScriptStream.close();

            // Determine the OS and set the python command accordingly
            String os = System.getProperty("os.name").toLowerCase();
            String pythonCommand = os.contains("win") ? "python" : "python3";

            // Command to run the Python script with arguments
            String[] cmd = {
                pythonCommand,
                tempScript.toString(),
                name,
                version,
                Integer.toString(allocram),
                action
            };

            // Execute the command
            Process process = Runtime.getRuntime().exec(cmd);

            // Read the output of the process
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = stdInput.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Read any errors from the attempted command
            StringBuilder errorOutput = new StringBuilder();
            while ((line = stdError.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();

            // Clean up the temporary script file
            java.nio.file.Files.delete(tempScript);

            // Check if the process exited normally and print the output
            if (exitCode == 0) {
                System.out.println("Output from generator: " + output.toString().trim());
            } else {
                System.out.println("Error: Process exited with non-zero status code " + exitCode);
                System.out.println("Error output: " + errorOutput.toString().trim());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
