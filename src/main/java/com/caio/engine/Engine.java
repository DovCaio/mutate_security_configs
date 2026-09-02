package com.caio.engine;

import com.caio.engine.runing_test.RunTest;
import com.caio.engine.runing_test.TestResult;
import com.caio.models.AnnotationMutationPoint;

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

    public Engine(EngineParams engineParams) {
        this.engineParams = engineParams;
        this.mutantGeneration = new MutantGeneration(engineParams.amps(), engineParams.applicationArguments());
        this.roles = engineParams.roles();
        this.authorities = engineParams.authorities();
    }

    public void start() throws Exception {

        Path temporaryDirectory = copyToTemporaryDirectory(engineParams.applicationArguments().getOriginalDirectory());

        this.runTest = new RunTest(temporaryDirectory, engineParams.buildTool(),
                engineParams.applicationArguments());
        this.codeLoader = new CodeLoader(this.runTest);

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

    public List<TestResult> getTestsResults() {
        return this.runTest.getTestsResults();
    }

}
