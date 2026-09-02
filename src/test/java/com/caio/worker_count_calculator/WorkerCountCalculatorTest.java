package com.caio.worker_count_calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.caio.directory_scan.DirectoryScan;

public class WorkerCountCalculatorTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldCalculateRepoSize() throws IOException {

        // 2 arquivos de 1MB
        Files.write(tempDir.resolve("a.txt"), new byte[1024 * 1024]);
        Files.write(tempDir.resolve("b.txt"), new byte[1024 * 1024]);

        WorkerCountCalculator calc = new WorkerCountCalculator(tempDir);

        long size = calc.repoSizeMB();

        assertEquals(2, size);
    }

    @Test
    void shouldIgnoreTargetDirectory() throws IOException {

        Path target = Files.createDirectory(tempDir.resolve("target"));

        Files.write(target.resolve("big.bin"), new byte[5 * 1024 * 1024]);

        WorkerCountCalculator calc = new WorkerCountCalculator(tempDir);

        long size = calc.repoSizeMB();

        assertEquals(0, size);
    }

    @Test
    void shouldCalculateWorkersSmallRepo(@TempDir Path dir) throws IOException {

        Files.write(dir.resolve("a.txt"), new byte[10]);

        WorkerCountCalculator calc = spy(new WorkerCountCalculator(dir));

        doReturn(8).when(calc).availableProcessors();
        doReturn(16000L).when(calc).totalMemoryMB();
        doReturn(600L).when(calc).repoSizeMB();

        int workers = calc.calculateWorkers();

        assertTrue(workers >= 1);
    }

    @Test
    void shouldReduceWorkersForLargeRepo(@TempDir Path dir) throws IOException {

        Files.write(dir.resolve("big.bin"),
                new byte[600 * 1024 * 1024]);

        WorkerCountCalculator calc = spy(new WorkerCountCalculator(dir));

        doReturn(8).when(calc).availableProcessors();
        doReturn(16000L).when(calc).totalMemoryMB();
        doReturn(600L).when(calc).repoSizeMB(); // simula repo grande

        int workers = calc.calculateWorkers();

        assertTrue(workers <= 4); // fator 0.5 aplicado
    }

    @Test
    void shouldReturnAtLeastOneWorker(@TempDir Path dir) {

        WorkerCountCalculator calc = spy(new WorkerCountCalculator(dir));

        doReturn(1).when(calc).availableProcessors();
        doReturn(500L).when(calc).totalMemoryMB();
        doReturn(10L).when(calc).repoSizeMB();

        int workers = calc.calculateWorkers();

        assertEquals(1, workers);
    }
}
