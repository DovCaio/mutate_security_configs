package com.caio.models.tests;

import java.util.List;

public class TestSuiteResult {
    String name;
    int tests;
    int totalFailures;
    int errors;
    int skipped;
    double time;
    List<FailureDetail> failureDetails;
    List<TestCaseResult> testCases;

    public TestSuiteResult(String name, int tests, int totalFailures, int errors, int skipped, double time,
            List<TestCaseResult> testCases, List<FailureDetail> failureDetails) {
        this.name = name;
        this.tests = tests;
        this.totalFailures = totalFailures;
        this.errors = errors;
        this.skipped = skipped;
        this.testCases = testCases;
        this.time = time;
        this.failureDetails = failureDetails;
    }

    public List<FailureDetail> getFailureDetails() {
        return this.failureDetails;
    }
}
