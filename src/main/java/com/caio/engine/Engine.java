package com.caio.engine;

import com.caio.TemporaryDirectoryManager;
import com.caio.args.ApplicationArguments;
import com.caio.engine.runing_test.RunTest;
import com.caio.engine.runing_test.TestResult;
import com.caio.engine.workers.DirectoryParallelExecutor;
import com.caio.engine.workers.DirectoryParallelExecutorParams;
import com.caio.models.AnnotationMutationPoint;
import com.caio.worker_count_calculator.WorkerCountCalculator;

import java.util.ArrayList;
import java.util.List;

public class Engine {

    private MutantGeneration mutantGeneration;
    private CodeLoader codeLoader;
    private List<String> roles;
    private List<String> authorities;
    private EngineParams engineParams;
    private DirectoryParallelExecutor directoryParallelExecutor;
    private TemporaryDirectoryManager temporaryDirectoryManager;

    public Engine(EngineParams engineParams) {
        this.engineParams = engineParams;
        this.mutantGeneration = new MutantGeneration(engineParams.amps(), engineParams.applicationArguments());
        this.roles = engineParams.roles();
        this.authorities = engineParams.authorities();
        this.temporaryDirectoryManager = engineParams.temporaryDirectoryManager();

    }

    private void firstExecution() throws Exception {

        this.codeLoader = new CodeLoader(engineParams.applicationArguments().getOriginalDirectory(),
                engineParams.applicationArguments().getOriginalDirectory(), engineParams.buildTool(),
                engineParams.applicationArguments());

        this.codeLoader.verifyTestsPassing();
    }

    private int calculateWorkers(ApplicationArguments arguments) {
        if (arguments.workersDefined()) {
            return arguments.getWorkersQuantity();
        } else {
            WorkerCountCalculator calculator = new WorkerCountCalculator(arguments.getOriginalDirectory());
            return calculator.calculateWorkers();
        }
    }

    public void start() throws Exception {

        firstExecution();

        ApplicationArguments arguments = engineParams.applicationArguments();

        int workers = calculateWorkers(arguments);

        this.directoryParallelExecutor = new DirectoryParallelExecutor(new DirectoryParallelExecutorParams(
                workers,
                arguments.getOriginalDirectory(),
                engineParams.buildTool(),
                arguments, engineParams.temporaryDirectoryManager()));

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
        // TODO - IMPORTANTE
        return new ArrayList<>();
    }

}
