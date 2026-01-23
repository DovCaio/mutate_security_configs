package com.caio.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.caio.directory_scan.DirectoryScan;
import com.caio.enums.BuildTool;
import com.caio.exceptions.NoOneTestPasses;
import com.caio.models.tests.TestExecutionReport;

public class RunTest {

        private List<TestResult> testsResults;
        private TestResult verifyTestResult;
        private String failuresFromFirstExecution;
        private Path repoDirectory;
        private BuildTool buildTool;
        private String command = "";
        private DirectoryScan directoryScan;
        private Path projectRoot;

        public RunTest(Path repoDirectory, BuildTool buildTool) {
                this.repoDirectory = repoDirectory;
                this.buildTool = buildTool;
                this.testsResults = new ArrayList<TestResult>();
                this.projectRoot = repoDirectory;
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
                } catch (IOException e) { // Talvez tentar personalizar cada um desses seja uma boa.
                        throw new RuntimeException("Erro ao ler relatórios de testes: " + e.getMessage(), e);
                } catch (ParserConfigurationException e) {
                        throw new RuntimeException("Erro ao ler relatórios de testes: " + e.getMessage(), e);
                } catch (SAXException e) {
                        throw new RuntimeException("Erro ao ler relatórios de testes: " + e.getMessage(), e);
                }
        }

        private TestResult runAllTestsCorrect(ParamsForTestMutationApresentation params)
                        throws IOException, InterruptedException {

                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.directory(projectRoot.toFile());
                processBuilder.command("sh", "-c", this.command);

                Process process = processBuilder.start();

                int exitCode = process.waitFor(); //Pode ser um indicio de que os testes falharam

                BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream())); //Tem que ser consumido para não travar o processo
                BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                TestExecutionReport testExecutionReport = readResult();

                if (params == null) { // Execução inicial, sem mutations

                        return new TestResult(testExecutionReport);
                } else {
                        return new TestResult(
                                        testExecutionReport,
                                        params);
                }

        }

        public TestResult executeTestForVerification() throws IOException, InterruptedException {
                TestResult testResult = runAllTestsCorrect(null);
                verifyTestResult = testResult;
                if (testResult.getTotalTest() == testResult.getFailed())
                        throw new NoOneTestPasses();
                this.failuresFromFirstExecution = testResult.getFailures();

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

                @Override()
                public String toString() { // Adaptar tudo isso
                        String failuresString = getFailures();

                        return "=== RESULTADOS DOS TESTES ===\n" +
                                        "Total tests: " + this.getTotalTest() + "\n" +
                                        "Succeeded: " + this.getSuccedded() + "\n" +
                                        "Failed: " + this.getFailed() + "\n" +
                                        (this.getFailed() == 0 ? "" : "Failures:\n" + failuresString + "\n") +
                                        "=============================";
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

                public String getFailures() {
                        return testExecutionReport.getFailureDetails()
                                        .stream()
                                        .map(failure -> failure.getMessage())
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
