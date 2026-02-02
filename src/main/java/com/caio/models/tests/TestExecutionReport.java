package com.caio.models.tests;

import java.util.ArrayList;
import java.util.List;

public class TestExecutionReport {

    private List<TestSuiteResult> suites;
    private Long totalTests;
    private Long totalFailures;
    private Long totalErrors;
    private Long totalSkipped;
    private Double totalTime;

    public TestExecutionReport(List<TestSuiteResult> suites) {
        this.suites = suites;
        this.totalTests = suites.stream().mapToLong(suite -> suite.tests).sum();
        this.totalFailures = suites.stream().mapToLong(suite -> suite.totalFailures).sum();
        this.totalErrors = suites.stream().mapToLong(suite -> suite.errors).sum();
        this.totalSkipped = suites.stream().mapToLong(suite -> suite.skipped).sum();
        this.totalTime = suites.stream().mapToDouble(suite -> suite.time).sum();
    }

    public void addSuite(TestSuiteResult suite) {
        suites.add(suite);
    }

    public List<TestSuiteResult> getSuites() {
        return suites;
    }

    public Long getTotalTests() {
        return totalTests;
    }

    public Long getTotalFailures() {
        return totalFailures;
    }

    public Long getTotalErrors() {
        return totalErrors;
    }

    public Long getTotalSkipped() {
        return totalSkipped;
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public List<FailureDetail> getFailureDetails() {
        return suites.stream().reduce(new ArrayList<>(), (acc, suite) -> {
            List<FailureDetail> failureDetails = suite.getFailureDetails();
            if (failureDetails != null && !failureDetails.isEmpty()) {
                acc.addAll(failureDetails);
            }
            return acc;
        }, (a, b) -> a);
    }

}
