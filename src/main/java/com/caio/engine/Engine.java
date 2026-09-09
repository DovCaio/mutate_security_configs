package com.caio.engine;

import com.caio.args.ApplicationArguments;
import com.caio.engine.runing_test.TestResult;
import com.caio.engine.workers.DirectoryParallelExecutor;
import com.caio.engine.workers.DirectoryParallelExecutorParams;
import com.caio.models.AnnotationMutationPoint;
import com.caio.worker_count_calculator.WorkerCountCalculator;

import java.util.List;

public class Engine {

    private MutantGeneration mutantGeneration;
    private CodeLoader codeLoader;
    private EngineParams engineParams;
    private DirectoryParallelExecutor directoryParallelExecutor;

    public Engine(EngineParams engineParams) {
        this.engineParams = engineParams;
        this.mutantGeneration = new MutantGeneration(engineParams.amps(), engineParams.applicationArguments());
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

        this.directoryParallelExecutor = new DirectoryParallelExecutor(new DirectoryParallelExecutorParams(
                calculateWorkers(arguments),
                arguments.getOriginalDirectory(),
                engineParams.buildTool(),
                arguments, engineParams.temporaryDirectoryManager()));

        this.mutantGeneration.createMutants(engineParams.roles(), engineParams.authorities());
        this.directoryParallelExecutor.process(getMutants());

    }

    public List<AnnotationMutationPoint> getMutants() {
        return this.mutantGeneration.getMutants();
    }

    public void setMutants(List<AnnotationMutationPoint> mutants) {
        this.mutantGeneration.setMutants(mutants);
    }

    public List<TestResult> getTestsResults() {
        return this.directoryParallelExecutor.getTestsResults();
    }

}
