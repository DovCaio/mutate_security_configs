package com.caio.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;

import com.caio.models.AnnotationMutationPoint;

public class MemoryCodeLoader {

    private RunTest runTest;
    private Map<String, Path> allBytes = new HashMap<>();

    private List<Class<?>> tests = new java.util.ArrayList<>();

    public MemoryCodeLoader(RunTest runTest) {
        this.runTest = runTest;
    }
    
    public void verifyTestsPassing() throws IOException, InterruptedException {
        RunTest.TestResult testResult = runTest.executeTestForVerification();
        //if (testResult.failed > 0) {
            //throw new IOException("Nem todos os testes passaram na execução inicial. Impossível continuar com a mutação.");
        //}
    }

    public void modifyCode(List<AnnotationMutationPoint> mutants) throws ClassNotFoundException {

    }

}
