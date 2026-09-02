package com.caio.engine.workers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.caio.args.ApplicationArguments;
import com.caio.engine.CodeLoader;
import com.caio.engine.runing_test.RunTest;
import com.caio.enums.BuildTool;
import com.caio.models.AnnotationMutationPoint;

import static com.caio.util.HandleWithFile.copyToTemporaryDirectory;

public class DirectoryParallelExecutor {

    private final ExecutorService executor;
    private final List<RunTest> runTests = new ArrayList<>();
    private final List<CodeLoader> codeLoaders = new ArrayList<>();
    private final List<Path> temporaryDirectories = new ArrayList<>();

    public DirectoryParallelExecutor(DirectoryParallelExecutorParams params) throws IOException {
        this.executor = Executors.newFixedThreadPool(params.workerCount());
        for (int i = 0; i < params.workerCount(); i++) {
            Path temporaryDirectory = copyToTemporaryDirectory(params.originalDirectory());
            temporaryDirectories.add(temporaryDirectory);
            RunTest runTest = new RunTest(temporaryDirectory, params.buildTool(), params.applicationArguments());
            runTests.add(runTest);
            CodeLoader codeLoader = new CodeLoader(runTest);
            codeLoaders.add(codeLoader);
        }
    }

    public void process(List<AnnotationMutationPoint> mutationPoints) {
        Queue<AnnotationMutationPoint> mutationQueue = new ConcurrentLinkedQueue<>(mutationPoints);
        for (CodeLoader codeLoader : codeLoaders) {
            executor.submit(new DirectoryWorker(codeLoader, mutationQueue));
        }
    }

    public List<Path> getTemporaryDirectories() {
        return temporaryDirectories;
    }
}
