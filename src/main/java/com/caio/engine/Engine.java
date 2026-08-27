package com.caio.engine;

import com.caio.args.ApplicationArguments;
import com.caio.enums.BuildTool;
import com.caio.models.AnnotationMutationPoint;

import java.nio.file.Path;
import java.util.List;

public class Engine {

    private MutantGeneration mutantGeneration;
    private CodeLoader codeLoader;
    private RunTest runTest;
    private List<String> roles;
    private List<String> authorities;
    private ApplicationArguments applicationArguments;

    public Engine(List<AnnotationMutationPoint> amps, List<AnnotationMutationPoint> mainClasses, Path repoDirectory,
            BuildTool buildTool, List<String> roles, List<String> authorities,
            ApplicationArguments applicationArguments) {
        this.applicationArguments = applicationArguments;
        this.runTest = new RunTest(repoDirectory, buildTool, applicationArguments);
        this.mutantGeneration = new MutantGeneration(amps, applicationArguments);
        this.codeLoader = new CodeLoader(this.runTest);
        this.roles = roles;
        this.authorities = authorities;
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
