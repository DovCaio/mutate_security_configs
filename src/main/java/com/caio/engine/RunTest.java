package com.caio.engine;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

import com.caio.exceptions.NoOneTestPasses;
import com.caio.models.AnnotationMutationPoint;

public class RunTest {

        private List<TestResult> testsResults;
        private List<AnnotationMutationPoint> testClasses;
        private TestResult verifyTestResult;

        public RunTest(List<AnnotationMutationPoint> testClasses) {

                this.testsResults = new ArrayList<TestResult>();
                this.testClasses = testClasses;
        }

        private TestResult runAllTests(ClassLoader loader) {
                Thread.currentThread().setContextClassLoader(loader);

                Long totalTests = 0L;
                Long failed = 0L;
                List<Failure> failures = new ArrayList<>();
                for (AnnotationMutationPoint c : this.testClasses) {
                        String className = c.getTargetElement().name.replace('/', '.');

                        try {
                                Class<?> testClass = loader.loadClass(className);

                                LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                                                .selectors(DiscoverySelectors.selectClass(testClass))
                                                .build();

                                Launcher launcher = LauncherFactory.create();
                                SummaryGeneratingListener listener = new SummaryGeneratingListener();

                                launcher.registerTestExecutionListeners(listener);
                                launcher.execute(request);

                                TestExecutionSummary summary = listener.getSummary();

                                totalTests += (long) summary.getTestsFoundCount();
                                failed += (long) summary.getFailures().size();
                                failures.addAll(summary.getFailures());

                        } catch (Throwable t) {
                                // System.out.println(
                                // "💥 Falha ao carregar/executar " + className + ": " + t.getMessage());
                                // t.printStackTrace(System.out);
                        }
                }
                Long succeddeds = totalTests - failed;
                TestResult testResult = new TestResult(totalTests, succeddeds, failed, failures);

                return testResult;

        }

        public void runAllTestsCorrect(ClassLoader loader, List<Class<?>> loadedTestClasses) {
                Thread.currentThread().setContextClassLoader(loader);

                Launcher launcher = LauncherFactory.create();
                SummaryGeneratingListener summary = new SummaryGeneratingListener();

                LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();

                for (Class<?> testClass : loadedTestClasses) {
                        builder.selectors(DiscoverySelectors.selectClass(testClass));
                }

                LauncherDiscoveryRequest request = builder.build();
                launcher.execute(request, summary);

                summary.getSummary().printTo(new PrintWriter(System.out));
        }

        public TestResult executeTestForVerification(ClassLoader loader) {
                TestResult testResult = runAllTests(loader);
                runAllTestsCorrect(loader, null);
                verifyTestResult = testResult;
                if (testResult.totalTest == testResult.failed)
                        throw new NoOneTestPasses();
                return testResult;
        }

        public TestResult executeTestForMutation(ClassLoader loader) {
                TestResult testResult = runAllTests(loader);
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
                        failures.forEach(f -> this.failures.put(((Failure) f).getTestIdentifier().getDisplayName(),
                                        ((Failure) f).getException().toString()));

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
