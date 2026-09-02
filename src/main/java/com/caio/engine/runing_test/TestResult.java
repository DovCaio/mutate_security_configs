package com.caio.engine.runing_test;

import com.caio.engine.ParamsForTestMutationApresentation;
import com.caio.models.tests.FailureDetail;
import com.caio.models.tests.TestExecutionReport;

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
        String color = wasCaptured() ? "\u001B[32m" : "\u001B[31m";
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

    public boolean wasCaptured() {
        return !this.getSuccedded().equals(this.getTotalTest());
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
