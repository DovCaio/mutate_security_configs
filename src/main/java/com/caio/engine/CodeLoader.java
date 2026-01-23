package com.caio.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.caio.models.AnnotationMutationPoint;

public class CodeLoader {

    private RunTest runTest;

    public CodeLoader(RunTest runTest) {
        this.runTest = runTest;
    }
    
    public void verifyTestsPassing() throws IOException, InterruptedException {
        RunTest.TestResult testResult = runTest.executeTestForVerification();
        if (testResult.getFailed() > 0) {
            throw new IOException("Nem todos os testes passaram na execução inicial. Impossível continuar com a mutação.");
        }
    }


    private String replace(String content, String original, String newValue, Integer line){

        String[] aux = content.split("\n");

        aux[line] = aux[line].replace(original, newValue);

        return String.join("\n", aux);
    }

    private void modifyCode(AnnotationMutationPoint amp, Boolean revert) throws ClassNotFoundException, IOException {

        String content = Files.readString(amp.getFilePath());
        String modifiedContent;

        if (!revert) {
            modifiedContent = replace(content, amp.getOriginalValue(), amp.getMutatedValue(), amp.getLineNumber() - 1);
        } else {
            modifiedContent = replace(content, amp.getMutatedValue(), amp.getOriginalValue(), amp.getLineNumber() - 1);
        }

        Files.writeString(amp.getFilePath(), modifiedContent);
    }

    public void start( List<AnnotationMutationPoint> mutants) throws IOException, InterruptedException {

        for (AnnotationMutationPoint amp : mutants) {
            try {
                modifyCode(amp, false);
                runTest.executeTestForMutation(
                        new ParamsForTestMutationApresentation("", "", "", amp.getOriginalValue(), amp.getMutatedValue()));
                modifyCode(amp, true);
            } catch (Exception e) { //Provisório
                e.printStackTrace();
            }
        }

    }

}
