package com.caio.engine;

import com.caio.engine.runing_test.RunTest;
import com.caio.engine.runing_test.TestResult;
import com.caio.engine.workers.DirectoryParallelExecutor;
import com.caio.engine.workers.DirectoryParallelExecutorParams;
import com.caio.models.AnnotationMutationPoint;
import com.caio.worker_count_calculator.WorkerCountCalculator;

import java.nio.file.Path;
import java.util.List;

import static com.caio.util.HandleWithFile.copyToTemporaryDirectory;

public class Engine {

    private MutantGeneration mutantGeneration;
    private CodeLoader codeLoader;
    private RunTest runTest;
    private List<String> roles;
    private List<String> authorities;
    private EngineParams engineParams;
    private DirectoryParallelExecutor directoryParallelExecutor;

    public Engine(EngineParams engineParams) {
        this.engineParams = engineParams;
        this.mutantGeneration = new MutantGeneration(engineParams.amps(), engineParams.applicationArguments());
        this.roles = engineParams.roles();
        this.authorities = engineParams.authorities();

    }

    private void firstExecution() throws Exception {

        this.runTest = new RunTest(engineParams.applicationArguments().getOriginalDirectory(), engineParams.buildTool(),
                engineParams.applicationArguments());
        this.codeLoader = new CodeLoader(this.runTest);

        this.codeLoader.verifyTestsPassing();
    }

    public void start() throws Exception {

        firstExecution();

        WorkerCountCalculator workerCountCalculator = new WorkerCountCalculator(
                engineParams.applicationArguments().getOriginalDirectory());
        this.directoryParallelExecutor = new DirectoryParallelExecutor(new DirectoryParallelExecutorParams(
                workerCountCalculator.calculateWorkers(),
                engineParams.applicationArguments().getOriginalDirectory(),
                engineParams.buildTool(),
                engineParams.applicationArguments()));

        this.mutantGeneration.createMutants(roles, authorities);
        this.directoryParallelExecutor.process(getMutants());

    }

    public List<AnnotationMutationPoint> getMutants() {
        return this.mutantGeneration.getMutants();
    }

    public void setMutants(List<AnnotationMutationPoint> mutants) {
        this.mutantGeneration.setMutants(mutants);
    }

    public List<TestResult> getTestsResults() {
        return this.runTest.getTestsResults();
    }

    public DirectoryParallelExecutor getDirectoryParallelExecutor() {
        return this.directoryParallelExecutor;
    }

}
