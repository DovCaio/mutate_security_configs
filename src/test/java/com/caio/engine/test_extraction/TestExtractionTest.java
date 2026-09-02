package com.caio.engine.test_extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.caio.directory_scan.DirectoryScan;
import com.caio.models.tests.TestExecutionReport;
import com.caio.models.tests.TestSuiteResult;

public class TestExtractionTest {

    private Path tempDir;

    private Path createFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file;
    }

    @BeforeEach
    void setup() throws IOException {
        tempDir = Files.createTempDirectory("scan-test-");
    }

    private String fullJUnitXml() {
        return """
                <testsuite name="SuiteA" tests="3" failures="1" errors="1" skipped="1" time="0.5">

                    <testcase classname="A" name="passTest" time="0.1"/>

                    <testcase classname="A" name="failTest" time="0.2">
                        <failure type="AssertionError" message="boom">
                            stacktrace here
                        </failure>
                    </testcase>

                    <testcase classname="A" name="errorTest" time="0.2">
                        <error type="Exception" message="bad">
                            error stack
                        </error>
                    </testcase>

                    <testcase classname="A" name="skipTest" time="0.0">
                        <skipped/>
                    </testcase>

                </testsuite>
                """;
    }

    @Test
    void shouldParseFullTestReport() throws Exception {

        createFile("TEST-report.xml", fullJUnitXml());

        TestExtraction scan = new TestExtraction(tempDir);

        TestExecutionReport report = scan.getTestsReports();

        assertEquals(1, report.getSuites().size());

        TestSuiteResult suite = report.getSuites().get(0);

        assertEquals(3, suite.getTests());
        assertEquals(1, suite.getTotalFailures());
        assertEquals(1, suite.getErrors());
        assertEquals(1, suite.getSkipped());

        assertEquals(4, suite.getTestCases().size());
    }

    @Test
    void shouldExtractFailureDetails()
            throws Exception {

        createFile("TEST-fail.xml", fullJUnitXml());

        TestExtraction scan = new TestExtraction(tempDir);

        TestExecutionReport report = scan.getTestsReports();

        assertNotNull(report.getSuites().get(0).getFailureDetails());
        assertFalse(report.getSuites().get(0).getFailureDetails().isEmpty());
    }

    @Test
    void shouldHandleMissingSkippedAttribute() throws Exception {

        String xml = """
                <testsuite name="S" tests="1" failures="0" errors="0" skipped="" time="0.1">
                    <testcase classname="A" name="t" time="0.1"/>
                </testsuite>
                """;

        createFile("TEST-no-skip.xml", xml);

        TestExtraction scan = new TestExtraction(tempDir);

        TestExecutionReport report = scan.getTestsReports();

        assertEquals(0, report.getSuites().get(0).getSkipped());
    }

    @Test
    void shouldCallExtractSuitCaseViaReflection()
            throws Exception {

        Path xml = createFile("TEST-x.xml", fullJUnitXml());

        TestExtraction scan = new TestExtraction(tempDir);

        Method m = TestExtraction.class
                .getDeclaredMethod("extractSuitCase", File.class);

        m.setAccessible(true);

        Object suite = m.invoke(scan, xml.toFile());

        assertNotNull(suite);
    }
}
