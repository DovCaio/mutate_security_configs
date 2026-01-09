package com.caio.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

import com.caio.exceptions.NoOneTestPasses;
import com.caio.models.AnnotationMutationPoint;

public class RunTest {

        private List<TestResult> testsResults;
        private List<AnnotationMutationPoint> testClasses;
        private TestResult verifyTestResult;
        private Map<String, String> failuresFromFirstExecution;

        public RunTest(List<AnnotationMutationPoint> testClasses) {

                this.testsResults = new ArrayList<TestResult>();
                this.testClasses = testClasses;
        }

        public TestResult runAllTestsCorrect(ClassLoader loader, List<Class<?>> loadedTestClasses) {
                Thread.currentThread().setContextClassLoader(loader);

                Launcher launcher = LauncherFactory.create();
                SummaryGeneratingListener summary = new SummaryGeneratingListener();

                LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();

                for (Class<?> testClass : loadedTestClasses) {
                        builder.selectors(DiscoverySelectors.selectClass(testClass));
                }

                LauncherDiscoveryRequest request = builder.build();
                launcher.execute(request, summary);

                TestResult testResult = new TestResult(
                                summary.getSummary().getTestsFoundCount(),
                                summary.getSummary().getTestsSucceededCount(),
                                summary.getSummary().getTestsFailedCount(),
                                summary.getSummary().getFailures());
                return testResult;
        }

        public TestResult executeTestForVerification(ClassLoader loader, List<Class<?>> loadedTestClasses) {
                TestResult testResult = runAllTestsCorrect(loader, loadedTestClasses);
                verifyTestResult = testResult;
                if (testResult.totalTest == testResult.failed)
                        throw new NoOneTestPasses();
                this.failuresFromFirstExecution = testResult.failures;
                return testResult;
        }

        public TestResult executeTestForMutation(ClassLoader loader, List<Class<?>> loadedTestClasses) {
                TestResult testResult = runAllTestsCorrect(loader, loadedTestClasses);
                this.testsResults.add(testResult);
                return testResult;
        }

        public class TestResult {

                private Long totalTest;
                private Long succedded;
                private Long failed;
                private Map<String, String> failures;

                public TestResult(Long totalTest, Long succedded, Long failed, List<?> failures) {
                        this.totalTest = totalTest;
                        this.succedded = succedded;
                        this.failed = failed;
                        this.failures = new HashMap<String, String>();

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

        }

        public List<TestResult> getTestsResults() {
                return testsResults;
        }

}
