package com.caio.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

import com.caio.exceptions.NoOneTestPasses;

public class RunTest {

        private List<TestResult> testsResults;
        private TestResult verifyTestResult;
        private Map<String, String> failuresFromFirstExecution;
        private Path repoDirectory;


        public RunTest(Path repoDirectory) {
                this.repoDirectory = repoDirectory;     
                this.testsResults = new ArrayList<TestResult>();
        }

        private TestResult runAllTestsCorrect(ParamsForTestMutationApresentation params) throws IOException, InterruptedException {

                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("sh", "-c", "echo 'Hello World!" + repoDirectory.toString() + "'");

                List<String> output = new ArrayList<>();

                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                        output.add(line);
                }
                }

                System.out.println("Output do processo:");
                for (String line : output) {
                        System.out.println(line);
                }

                int exitCode = process.waitFor();

                /* 
                if (params == null){ // Execução inicial, sem mutations
                        
                        return new TestResult(
                                summary.getSummary().getTestsFoundCount(),
                                summary.getSummary().getTestsSucceededCount(),
                                summary.getSummary().getTestsFailedCount(),
                                summary.getSummary().getFailures());
                }else {
                        return new TestResult(
                                summary.getSummary().getTestsFoundCount(),
                                summary.getSummary().getTestsSucceededCount(),
                                summary.getSummary().getTestsFailedCount(),
                                summary.getSummary().getFailures(),
                                params);
                }*/

                return null;
        }

        public TestResult executeTestForVerification() throws IOException, InterruptedException {
                TestResult testResult = runAllTestsCorrect(null);
                verifyTestResult = testResult;
                if (testResult.totalTest == testResult.failed)
                        throw new NoOneTestPasses();
                this.failuresFromFirstExecution = testResult.failures;
                return testResult;
        }

        public TestResult executeTestForMutation(ParamsForTestMutationApresentation params) throws IOException, InterruptedException {
                TestResult testResult = runAllTestsCorrect(params);
                this.testsResults.add(testResult);
                return testResult;
        }

        public class TestResult {

                private Long totalTest;
                private Long succedded;
                private Long failed;
                private Map<String, String> failures;

                private ParamsForTestMutationApresentation params;
                
                public TestResult(Long totalTest, Long succedded, Long failed, List<?> failures) { //Deve ser usado somente para verificação inicial, pois vai ter menos informações sobre as mutações
                        this.totalTest = totalTest;
                        this.succedded = succedded;
                        this.failed = failed;
                        this.failures = new HashMap<String, String>();
                        failures.forEach(f -> {
                                if (f != null && f instanceof Failure ) {
                                        this.failures.put(
                                                ((Failure) f).getTestIdentifier()
                                                                .getDisplayName(),
                                                ((Failure) f).getException().toString());
                                }
                        });
                }


                public TestResult(Long totalTest, Long succedded, Long failed, List<?> failures, ParamsForTestMutationApresentation params) {
                        this.totalTest = totalTest;
                        this.succedded = succedded;
                        this.failed = failed;
                        this.failures = new HashMap<String, String>();
                        this.params = params;
                        failures.forEach(f -> {
                                if (!(f != null && f instanceof Failure && failuresFromFirstExecution != null &&
                                                failuresFromFirstExecution.containsKey(
                                                                ((Failure) f).getTestIdentifier()
                                                                                .getDisplayName()))) {
                                        this.failures.put(
                                                        ((Failure) f).getTestIdentifier()
                                                                        .getDisplayName(),
                                                        ((Failure) f).getException().toString());
                                }
                        });
                }

                @Override()
                public String toString() {
                        String failuresString = getFailures();

                        return "=== RESULTADOS DOS TESTES ===\n" +
                                        "Total tests: " + this.totalTest + "\n" +
                                        "Succeeded: " + this.succedded + "\n" +
                                        "Failed: " + this.failed + "\n" +
                                        (failures.isEmpty() ? "" : "Failures:\n" + failuresString + "\n") +
                                        "=============================";
                }

                public boolean equals(TestResult b) {
                        return this.totalTest.equals(b.totalTest) && this.succedded.equals(b.succedded)
                                        && this.failed.equals(b.failed);
                }

                public boolean whasCaptured() {
                        return !this.equals(verifyTestResult);
                }

                public Long getTotalTest() {
                        return totalTest;
                }

                public void setTotalTest(Long totalTest) {
                        this.totalTest = totalTest;
                }

                public Long getSuccedded() {
                        return succedded;
                }

                public void setSuccedded(Long succedded) {
                        this.succedded = succedded;
                }

                public Long getFailed() {
                        return failed;
                }

                public void setFailed(Long failed) {
                        this.failed = failed;
                }

                public String getFailures() {
                        return failures.entrySet()
                                        .stream()
                                        .map(entry -> entry.getKey() + " -> " + entry.getValue())
                                        .reduce("", (a, b) -> a + "\n" + b);
                }

                public ParamsForTestMutationApresentation getParamsForTestMutationApresentation(){
                        return this.params;
                }


        }

        public List<TestResult> getTestsResults() {
                return testsResults;
        }

}
