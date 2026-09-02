package com.caio.worker_count_calculator;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.caio.util.ParallelExecutionContext;

public class WorkerCountCalculator {

    private static final Set<String> IGNORE_DIRS = Set.of(
            ".git", "target", "build", ".gradle", "node_modules");

    private Path directory;

    public WorkerCountCalculator(Path directory) {
        this.directory = directory;
    }

    protected Long repoSizeMB() {

        try (Stream<Path> walk = Files.walk(directory)) {

            long bytes = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> IGNORE_DIRS.stream()
                            .noneMatch(dir -> path.toString().contains(dir)))
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();

            return bytes / (1024 * 1024);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao calcular tamanho do repositório", e);
        }
    }

    protected int availableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    protected long totalMemoryMB() {
        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return os.getTotalMemorySize() / (1024 * 1024);
    }

    public int calculateWorkers() {
        int cores = availableProcessors();

        int workersByCpu = Math.max(1, cores / 2);

        long totalMemMB = totalMemoryMB();

        int workerMemoryMB = 800;

        int workersByMemory = (int) ((totalMemMB * 0.6) / workerMemoryMB);

        long repoSizeMBVar = repoSizeMB();

        double repoFactor = repoSizeMBVar > 500 ? 0.5 : repoSizeMBVar > 200 ? 0.7 : 1.0;

        int workers = (int) (Math.min(workersByCpu, workersByMemory) * repoFactor);

        return Math.max(workers, 1);
    }
}
