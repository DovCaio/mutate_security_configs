package com.caio.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.caio.directory_scan.DirectoryScan;
import com.caio.enums.BuildTool;
import com.caio.exceptions.NoOneTestPasses;
import com.caio.models.tests.FailureDetail;
import com.caio.models.tests.TestExecutionReport;

public class RunTest {

        private List<TestResult> testsResults;
        private TestResult verifyTestResult;
        private Path repoDirectory;
        private BuildTool buildTool;
        private String command = "";
        private DirectoryScan directoryScan;
        private String flag;

        public RunTest(Path repoDirectory, BuildTool buildTool, String flag) {
                this.repoDirectory = repoDirectory;
                this.buildTool = buildTool;
                this.flag = flag;
                this.testsResults = new ArrayList<TestResult>();
                String dir = repoDirectory.toAbsolutePath().toString();

                switch (this.buildTool) {
                        case MAVEN:
                                this.command = "mvn -U clean test";
                                dir = dir + "/target/surefire-reports";
                                break;
                        case GRADLE:
                        case GRADLE_WRAPPER:
                                this.command = "./gradlew test --no-daemon --rerun-tasks --no-build-cache";
                                dir = dir + "/build/test-results/test";
                                break;

                        default:
                                throw new IllegalArgumentException("Build tool não suportada: " + this.buildTool);
                }

                Path reportsDir = Path.of(dir);
                this.directoryScan = new DirectoryScan(reportsDir);
        }

        private TestExecutionReport readResult() {
                try {
                        return directoryScan.getTestsReports();
                } catch (Exception e) { //Está genérico porque eu não tenho como controlar muita coisa.
                        throw new RuntimeException("Erro ao ler relatórios de testes: " + e.getMessage(), e);
                }
        }

        private TestResult runAllTestsCorrect(ParamsForTestMutationApresentation params)
                        throws IOException, InterruptedException {

                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.directory(repoDirectory.toFile());
                processBuilder.command("sh", "-c", this.command);

                Process process = processBuilder.start();

                process.waitFor();

                BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream())); //Tem que ser consumido para não travar o processo
                BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                TestExecutionReport testExecutionReport = readResult();

                if (params == null) { // Execução inicial, sem mutations
                        return new TestResult(testExecutionReport);
                } else {

                        TestResult testResult = new TestResult(testExecutionReport, params);

                        if (flag.equals("-v")) {
                                System.out.println(testResult.toString());
                        }
                        return testResult;
                }
        }

        public TestResult executeTestForVerification() throws IOException, InterruptedException {
                TestResult testResult = runAllTestsCorrect(null);
                verifyTestResult = testResult;
                if (testResult.getTotalTest() == testResult.getFailed())
                        throw new NoOneTestPasses();

                return testResult;
        }

        public TestResult executeTestForMutation(ParamsForTestMutationApresentation params)
                        throws IOException, InterruptedException {
                TestResult testResult = runAllTestsCorrect(params);
                this.testsResults.add(testResult);
                return testResult;
        }

        public class TestResult {

                private ParamsForTestMutationApresentation params;
                private TestExecutionReport testExecutionReport;

                public TestResult(TestExecutionReport testExecutionReport) { // Deve ser usado
                                                                             // somente para
                                                                             // verificação
                                                                             // inicial, pois vai
                                                                             // ter menos
                                                                             // informações sobre
                                                                             // as mutações
                        this.testExecutionReport = testExecutionReport;
                }

                public TestResult(TestExecutionReport testExecutionReport,
                                ParamsForTestMutationApresentation params) {
                        this.testExecutionReport = testExecutionReport;
                        this.params = params;
                }

                @Override
                public String toString() {
                        String failuresString = getFailures();
                        String color = whasCaptured() ? "\u001B[32m" : "\u001B[31m";
                        String reset = "\u001B[0m";

                        return color + "=== RESULTADOS DOS TESTES ===\n" +
                                        "ClassName: " + this.params.className + "\n" +
                                        "Method: " + this.params.method + "\n" +
                                        "OriginalValue: " + this.params.originalValue + "\n" +
                                        "MutateValue: " + this.params.mutatedValue + "\n" +
                                        "Total tests: " + this.getTotalTest() + "\n" +
                                        "Succeeded: " + this.getSuccedded() + "\n" +
                                        "Failed: " + this.getFailed() + "\n" +
                                        (this.getFailed() == 0 ? "" : "Failures:\n" + failuresString + "\n") +
                                        "=============================" + reset;
                }

                public boolean equals(TestResult b) {
                        return this.getSuccedded().equals(b.getSuccedded())
                                        && this.getSuccedded().equals(b.getSuccedded())
                                        && this.getFailed().equals(b.getFailed());
                }

                public boolean whasCaptured() {
                        return !this.equals(verifyTestResult);

                }
                public Long getTotalTest() {
                        return testExecutionReport.getTotalTests();
                }

                public Long getSuccedded() {
                        return testExecutionReport.getTotalTests() - testExecutionReport.getTotalFailures()
                                        - testExecutionReport.getTotalErrors();
                }

                public Long getFailed() {
                        return testExecutionReport.getTotalFailures() + testExecutionReport.getTotalErrors();
                }

                private String makeFailureDetailsString(FailureDetail failure) {
                        return "Type: " + failure.getType() + "\n" +
                                        "Message: " + failure.getMessage() + "\n" +
                                        "StackTrace: " + failure.getStackTrace() + "\n";
                }

                public String getFailures() {

                        return testExecutionReport.getFailureDetails()
                                        .stream()
                                        .map(this::makeFailureDetailsString)
                                        .reduce("", (a, b) -> a + "\n" + b);
                }

                public ParamsForTestMutationApresentation getParamsForTestMutationApresentation() {
                        return this.params;
                }
        }

        public List<TestResult> getTestsResults() {
                return testsResults;
        }

}
