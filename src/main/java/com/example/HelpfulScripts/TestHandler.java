package com.mcl; 

import org.python.util.PythonInterpreter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class TestHandler {
    public static void pytest(){
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            pyInterp.setOut(outputStream);

            InputStream pyScript = App.class.getClassLoader().getResourceAsStream("scripts/test.py");
            if (pyScript != null) {
                pyInterp.execfile(pyScript);
                String output = outputStream.toString("UTF-8");
                System.out.println("Output from Python script: " + output);
            } else {
                System.out.println("Python script not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
