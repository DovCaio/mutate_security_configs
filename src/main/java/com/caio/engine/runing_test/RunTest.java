package com.caio.engine.runing_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.caio.args.ApplicationArguments;
import com.caio.engine.ParamsForTestMutationApresentation;
import com.caio.engine.test_extraction.TestExtraction;
import com.caio.enums.BuildTool;
import com.caio.exceptions.NoOneTestPasses;
import com.caio.models.tests.TestExecutionReport;

public class RunTest {

        private List<TestResult> testsResults;
        private Path repo;
        private BuildTool buildTool;
        private String[] command = new String[] {};
        private TestExtraction testExtraction;
        private long startTestFirstExecutionTime;
        private long totalTestFirstExecutionTime;
        private ApplicationArguments applicationArguments;

        public RunTest(Path repo, BuildTool buildTool, ApplicationArguments applicationArguments) {
                this.repo = repo;
                this.buildTool = buildTool;
                this.applicationArguments = applicationArguments;
                this.testsResults = new ArrayList<TestResult>();
                String dir = repo.toAbsolutePath().toString();
                totalTestFirstExecutionTime = TimeUnit.MINUTES.toMillis(applicationArguments.getTimeOut());

                switch (this.buildTool) {
                        case MAVEN:
                                this.command = new String[] {
                                                "mvn",
                                                "test",
                                                "-B",
                                                "-DforkCount=1",
                                                "-DreuseForks=true"
                                };
                                dir = dir + "/target/surefire-reports";
                                break;
                        case GRADLE:
                        case GRADLE_WRAPPER:
                                this.command = new String[] {
                                                "./gradlew",
                                                "test",
                                                "--no-daemon",
                                                "--console=plain",
                                                "--rerun-tasks",
                                                "--no-build-cache",
                                                "--max-workers=1"
                                };
                                dir = dir + "/build/test-results/test";
                                break;

                        default:
                                throw new IllegalArgumentException("Build tool não suportada: " + this.buildTool);
                }

                Path reportsDir = Path.of(dir);
                this.testExtraction = new TestExtraction(reportsDir);
        }

        private TestExecutionReport readResult() {
                try {
                        return testExtraction.getTestsReports();
                } catch (Exception e) { // Está genérico porque eu não tenho como controlar muita coisa.
                        throw new RuntimeException("Erro ao ler relatórios de testes: " + e.getMessage(), e);
                }
        }

        private void defineTimeOut() {
                long endTestFirstExecutionTime = System.currentTimeMillis();
                long baseline = endTestFirstExecutionTime - this.startTestFirstExecutionTime;

                if (applicationArguments.isVerbose()) {
                        System.out.println("Tempo gasto para execução do primeiro test: " + baseline + " ms");
                }

                long tolerance = TimeUnit.MINUTES.toMillis(1);
                double factor = 1.2;

                this.totalTestFirstExecutionTime = (long) (baseline * factor) + tolerance;
        }

        private void consumeBuffers(Process process) {
                new Thread(() -> {
                        try (BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(process.getInputStream()))) {
                                while ((reader.readLine()) != null) {
                                }
                        } catch (IOException e) {
                                if (applicationArguments.isVerbose()) {
                                        System.out.println("Erro ao ler a saída do processo: " + e.getMessage());
                                }
                        }
                }).start();

                new Thread(() -> {
                        try (BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(process.getErrorStream()))) {
                                while (reader.readLine() != null) {
                                }
                        } catch (IOException e) {
                                if (applicationArguments.isVerbose()) {
                                        System.out.println("Erro ao ler a saída do processo: " + e.getMessage());
                                }
                        }
                }).start();
        }

        private TestExecutionReport runAllTestsCorrect()
                        throws IOException, InterruptedException {

                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.directory(repo.toFile());
                processBuilder.command(this.command);

                Process process = processBuilder.start();

                consumeBuffers(process);

                boolean finished = process.waitFor(totalTestFirstExecutionTime, TimeUnit.MILLISECONDS);

                if (!finished) {
                        if (applicationArguments.isVerbose()) {
                                System.out.println(
                                                "Timeout atingido para execução dos testes. Processo será finalizado.");
                        }
                        process.destroyForcibly();
                        process.waitFor();
                }

                TestExecutionReport testExecutionReport = readResult();
                return testExecutionReport;

        }

        public TestResult executeTestForVerification() throws IOException, InterruptedException {
                this.startTestFirstExecutionTime = System.currentTimeMillis();
                TestResult testResult = new TestResult(runAllTestsCorrect());
                this.defineTimeOut();
                if (testResult.getTotalTest() == testResult.getFailed())
                        throw new NoOneTestPasses();

                return testResult;
        }

        public TestResult executeTestForMutation(ParamsForTestMutationApresentation params)
                        throws IOException, InterruptedException {
                TestResult testResult = new TestResult(runAllTestsCorrect(), params);
                if (applicationArguments.isVerbose()) {
                        System.out.println(testResult.toString());
                }
                this.testsResults.add(testResult);
                return testResult;
        }

        public List<TestResult> getTestsResults() {
                return testsResults;
        }

}
