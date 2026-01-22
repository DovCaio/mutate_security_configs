package com.caio.models.tests;

import java.util.List;

public class TestSuiteResult {
    String name;
    int tests;
    int failures;
    int errors;
    int skipped;
    double time;
    List<TestCaseResult> testCases;

    public TestSuiteResult(String name, int tests, int failures, int errors, int skipped, double time, List<TestCaseResult> testCases) {
        this.name = name;
        this.tests = tests;
        this.failures = failures;
        this.errors = errors;
        this.skipped = skipped;
        this.testCases = testCases;
        this.time = time;
    }

    public List<FailureDetail> getFailures(){
        testCases.stream().reduce((List<FailureDetail>) null, (acc, testCase) -> {
            if(testCase.status == com.caio.enums.TestStatus.FAILED){
                acc.add(testCase.getFailure());
            }
            return acc;
        }, (a, b) -> a);
        return null;
    }    
}
