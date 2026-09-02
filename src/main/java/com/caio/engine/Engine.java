package com.caio.engine;

import com.caio.args.ApplicationArguments;
import com.caio.enums.BuildTool;
import com.caio.models.AnnotationMutationPoint;
import com.caio.util.ParallelExecutionContext;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Engine {

    private MutantGeneration mutantGeneration;
    private CodeLoader codeLoader;
    private RunTest runTest;
    private List<String> roles;
    private List<String> authorities;

    public Engine(EngineParams engineParams) {
        this.runTest = new RunTest(engineParams.repoDirectory(), engineParams.buildTool(),
                engineParams.applicationArguments());
        this.mutantGeneration = new MutantGeneration(engineParams.amps(), engineParams.applicationArguments());
        this.codeLoader = new CodeLoader(this.runTest);
        this.roles = engineParams.roles();
        this.authorities = engineParams.authorities();
    }

    public void start() throws Exception {
        this.mutantGeneration.createMutants(roles, authorities);
        this.codeLoader.verifyTestsPassing();
        this.codeLoader.start(getMutants());
    }

    public List<AnnotationMutationPoint> getMutants() {
        return this.mutantGeneration.getMutants();
    }

    public void setMutants(List<AnnotationMutationPoint> mutants) {
        this.mutantGeneration.setMutants(mutants);
    }

    public List<RunTest.TestResult> getTestsResults() {
        return this.runTest.getTestsResults();
    }

}
