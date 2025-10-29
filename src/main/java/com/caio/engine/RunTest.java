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
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

public class RunTest {

    private List<TestResult> testsResults;

    public RunTest(){

        this.testsResults = new ArrayList<TestResult>();

    }

    public void runAllTests(ClassLoader loader) {

    Thread.currentThread().setContextClassLoader(loader);

    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectPackage("pk.habsoft.demo.estore"))
            .build();

    Launcher launcher = LauncherFactory.create();
    SummaryGeneratingListener listener = new SummaryGeneratingListener();
    launcher.registerTestExecutionListeners(listener);

    launcher.execute(request);

    TestExecutionSummary summary = listener.getSummary();


    TestResult testResult = new TestResult(summary.getTestsFoundCount(), summary.getTestsSucceededCount(), summary.getTestsFailedCount(), summary.getFailures());

    System.out.println(testResult.toString());

    this.testsResults.add(testResult);

    }

    public class TestResult {

        private Long totalTest;
        private Long succedded;
        private Long failed;
        private Map<String, String> failures;

        public TestResult(Long totalTest, Long succedded, Long failed, List<?> failures){

                this.totalTest = totalTest;
                this.succedded = succedded;
                this.failed = failed;
                this.failures = new HashMap<String, String>();
                failures.forEach(f -> this.failures.put(((Failure) f).getTestIdentifier().getDisplayName(), ((Failure) f).getException().toString()));

        }

        @Override()
        public String toString(){
                //final String failuresString = failures.entrySet().stream().reduce( f  ->  f.getKey() + " -> " + f.getValue());
                              

                return "=== RESULTADOS DOS TESTES === \n" +
                        "Total tests: " + this.totalTest + "\n" +
                        "Succeeded: " + this.succedded + "\n" +
                        "Failed: " + this.failed + "\n";
        }

        public boolean whasCaptured() {
                return this.totalTest != this.succedded;
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
                return "";
        }


        
    }

    public List<TestResult> getTestsResults() {
        return testsResults;
    }

    
    
}
