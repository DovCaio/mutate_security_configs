package com.caio.models.tests;

import com.caio.enums.TestStatus;

public class TestCaseResult {
    String className;
    String testName;
    double time;
    TestStatus status;
    FailureDetail failure;
    
    public TestCaseResult(String className, String testName, double time, TestStatus status, FailureDetail failure) {
        this.className = className;
        this.testName = testName;
        this.time = time;
        this.status = status;
        this.failure = failure;
    }

    public FailureDetail getFailure() {
        return failure;
    }
    
}
