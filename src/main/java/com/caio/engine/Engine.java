package com.caio.engine;

import com.caio.directory_scan.DirectoryScan;
import com.caio.enums.BuildTool;
import com.caio.models.AnnotationMutationPoint;

import java.nio.file.Path;
import java.util.List;

public class Engine {

    private MutantGeneration mutantGeneration;
    private MemoryCodeLoader memoryCodeLoader;
    private RunTest runTest;

    public Engine(List<AnnotationMutationPoint> amps, List<AnnotationMutationPoint> mainClasses, Path repoDirectory, BuildTool buildTool) {
        this.runTest = new RunTest(repoDirectory, buildTool);
        this.mutantGeneration = new MutantGeneration(amps);
        this.memoryCodeLoader = new MemoryCodeLoader(this.runTest);
    }

    public void start() throws Exception {
        this.mutantGeneration.createMutants();
        this.memoryCodeLoader.verifyTestsPassing();
        //this.memoryCodeLoader.loadMutantInMemory(this.mutantGeneration.getMutants());
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
