package com.caio;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {


    @Test
    public void testJarExecution() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", "target/mutate_security_configs-1.0-SNAPSHOT.jar", "."
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        assertEquals(0, exitCode);
        assertTrue(output.toString().contains("Classe encontrada"));
    }
}
