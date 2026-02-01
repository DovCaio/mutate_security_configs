package com.caio.engine;

import com.caio.directory_scan.DirectoryScan;
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

    public Engine(List<AnnotationMutationPoint> amps, List<AnnotationMutationPoint> mainClasses, Path repoDirectory, BuildTool buildTool, List<String> roles, List<String> authorities, String flag) {
        this.runTest = new RunTest(repoDirectory, buildTool, flag);
        this.mutantGeneration = new MutantGeneration(amps);
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
