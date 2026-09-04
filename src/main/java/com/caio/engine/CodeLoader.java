package com.caio.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.caio.args.ApplicationArguments;
import com.caio.engine.runing_test.RunTest;
import com.caio.engine.runing_test.TestResult;
import com.caio.enums.BuildTool;
import com.caio.models.AnnotationMutationPoint;

public class CodeLoader {

    private RunTest runTest;
    private final Path originalDirectory;
    private final Path workingDirectory;

    public CodeLoader(Path originalDirectory, Path workingDirectory, BuildTool buildTool,
            ApplicationArguments applicationArguments) throws IOException {
        this.runTest = new RunTest(workingDirectory, buildTool, applicationArguments);
        this.originalDirectory = originalDirectory;
        this.workingDirectory = workingDirectory;
    }

    public void verifyTestsPassing() throws IOException, InterruptedException {
        TestResult testResult = runTest.executeTestForVerification();
        if (testResult.getFailed() > 0) {
            throw new IOException(
                    "Nem todos os testes passaram na execução inicial. Impossível continuar com a mutação.");
        }

    }

    private String replace(String content, String original, String newValue, Integer line) {

        String[] aux = content.split("\n");

        aux[line] = aux[line].replace(original, newValue);

        return String.join("\n", aux);
    }

    private Path resolveWorkingFile(Path originalFile) {
        Path relative = originalDirectory.relativize(originalFile);
        return workingDirectory.resolve(relative);
    }

    private void modifyCode(AnnotationMutationPoint amp, Boolean revert) throws ClassNotFoundException, IOException {
        Path workFile = resolveWorkingFile(amp.getFilePath());

        String content = Files.readString(workFile);
        String modifiedContent;

        if (!revert) {
            modifiedContent = replace(content, amp.getOriginalValue(), amp.getMutatedValue(), amp.getLineNumber() - 1);
        } else {
            modifiedContent = replace(content, amp.getMutatedValue(), amp.getOriginalValue(), amp.getLineNumber() - 1);
        }

        Files.writeString(workFile, modifiedContent);
    }

    public void executeOne(AnnotationMutationPoint amp) {
        try {
            modifyCode(amp, false);
            runTest.executeTestForMutation(
                    new ParamsForTestMutationApresentation(amp.getPackageName(), amp.getClassName(),
                            amp.getMethodName(), "", amp.getOriginalValue(), amp.getMutatedValue()));
            modifyCode(amp, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeMultiple(List<AnnotationMutationPoint> mutants) throws IOException, InterruptedException {

        for (AnnotationMutationPoint amp : mutants) {
            executeOne(amp);
        }

    }

}
