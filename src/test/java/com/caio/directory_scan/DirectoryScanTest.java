package com.caio.directory_scan;

import com.caio.directory_scan.DirectoryScan;
import com.caio.enums.BuildTool;
import com.caio.exceptions.NoOneClasseFinded;
import com.caio.exceptions.PathNotExists;
import com.caio.models.tests.TestExecutionReport;
import com.caio.models.tests.TestSuiteResult;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryScanTest {

    private Path tempDir;

    @BeforeEach
    void setup() throws IOException {
        // Cria um diretório temporário para cada teste
        tempDir = Files.createTempDirectory("scan-test-");
    }

    private Path createFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file;
    }

    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @Test
    @DisplayName("Deve lançar PathNotExists se o diretório não existir")
    void testThrowsWhenDirectoryDoesNotExist() {
        Path invalidDir = tempDir.resolve("nao-existe");
        assertThrows(PathNotExists.class, () -> new DirectoryScan(invalidDir));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se o diretório for nulo")
    void testThrowsWhenNullPath() {
        assertThrows(IllegalArgumentException.class, () -> new DirectoryScan(null));
    }

    @Test
    @DisplayName("Deve lançar NoOneClasseFinded se o diretório não contiver .java")
    void testThrowsWhenNoClassFiles() throws IOException {
        DirectoryScan scanner = new DirectoryScan(tempDir);
        assertThrows(NoOneClasseFinded.class, () -> scanner.findFiles(".java"));
    }

    @Test
    @DisplayName("Deve retornar lista de arquivos .java encontrados")
    void testFindsClassFiles() throws IOException {
        // Cria arquivos simulando classes compiladas
        Files.createFile(tempDir.resolve("Main.java"));
        Files.createFile(tempDir.resolve("Helper.java"));
        Files.createFile(tempDir.resolve("not_a_class.txt"));

        DirectoryScan scanner = new DirectoryScan(tempDir);
        scanner.findFiles(".java");
        List<Path> found = scanner.getFindeds();

        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(p -> p.toString().endsWith(".java")));
    }

    @Test
    @DisplayName("Deve encontrar arquivos .java em subdiretórios também")
    void testFindsClassesInSubdirectories() throws IOException {
        Path subDir = Files.createDirectories(tempDir.resolve("subdir"));
        Files.createFile(subDir.resolve("Inner.java"));

        DirectoryScan scanner = new DirectoryScan(tempDir);
        scanner.findFiles(".java");
        List<Path> found = scanner.getFindeds();

        assertEquals(1, found.size());
        assertTrue(found.get(0).toString().endsWith("Inner.java"));
    }

    @Test
    @DisplayName("Deve identificar o build tool como MAVEN se pom.xml existir")
    void testGetBuildToolMaven() throws IOException {
        Files.createFile(tempDir.resolve("pom.xml"));
        DirectoryScan scanner = new DirectoryScan(tempDir);
        assertEquals(BuildTool.MAVEN, scanner.getBuildTool());
    }

    @Test
    @DisplayName("Deve identificar o build tool como GRADLE se build.gradle existir")
    void testGetBuildToolGradle() throws IOException {
        Files.createFile(tempDir.resolve("build.gradle"));
        DirectoryScan scanner = new DirectoryScan(tempDir);
        assertEquals(BuildTool.GRADLE, scanner.getBuildTool());
    }

    @Test
    @DisplayName("Deve identificar o build tool como GRADLE_WRAPPER se gradlew existir")
    void testGetBuildToolGradleWrapper() throws IOException {
        Files.createFile(tempDir.resolve("gradlew"));
        DirectoryScan scanner = new DirectoryScan(tempDir);
        assertEquals(BuildTool.GRADLE_WRAPPER, scanner.getBuildTool());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se nenhum arquivo de build for encontrado")
    void testGetBuildToolThrows() {
        DirectoryScan scanner = new DirectoryScan(tempDir);
        assertThrows(IllegalArgumentException.class, scanner::getBuildTool);
    }

    @Test
    @DisplayName("getFindeds e setFindeds devem funcionar corretamente")
    void testGetAndSetFindeds() throws IOException {
        DirectoryScan scanner = new DirectoryScan(tempDir);
        Path file = Files.createFile(tempDir.resolve("A.java"));
        scanner.setFindeds(List.of(file));
        assertEquals(1, scanner.getFindeds().size());
        assertEquals(file, scanner.getFindeds().get(0));
    }

    @Test
    @DisplayName("getDependenciesPath e setDependenciesPath devem funcionar corretamente")
    void testGetAndSetDependenciesPath() throws IOException {
        DirectoryScan scanner = new DirectoryScan(tempDir);
        Path dep = Files.createFile(tempDir.resolve("dep.jar"));
        scanner.setDependenciesPath(List.of(dep));
        assertEquals(1, scanner.getDependenciesPath().size());
        assertEquals(dep, scanner.getDependenciesPath().get(0));
    }

    @Test
    @DisplayName("getDirectory deve retornar o diretório base correto")
    void testGetDirectory() {
        DirectoryScan scanner = new DirectoryScan(tempDir);
        assertEquals(tempDir, scanner.getDirectory());
    }

    @Test
    void shouldThrowWhenDirectoryIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new DirectoryScan(null));
    }

    @Test
    void shouldThrowWhenDirectoryNotExists() {
        Path fake = Path.of("nao_existe_123");

        assertThrows(PathNotExists.class,
                () -> new DirectoryScan(fake));
    }

    @Test
    void shouldCreateWithValidDirectory() {
        DirectoryScan scan = new DirectoryScan(tempDir);
        assertNotNull(scan);
    }

    // ===============================
    // FIND FILES
    // ===============================

    @Test
    void shouldFindJavaFiles() throws Exception {
        createFile("A.java", "class A {}");
        createFile("B.java", "class B {}");

        DirectoryScan scan = new DirectoryScan(tempDir);
        scan.findFiles(".java");

        assertEquals(2, scan.getFindeds().size());
    }

    @Test
    void shouldThrowWhenNoFilesFound() {
        DirectoryScan scan = new DirectoryScan(tempDir);

        assertThrows(NoOneClasseFinded.class,
                () -> scan.findFiles(".class"));
    }

    // ===============================
    // BUILD TOOL DETECTION
    // ===============================

    @Test
    void shouldDetectMaven() throws Exception {
        createFile("pom.xml", "<project/>");

        DirectoryScan scan = new DirectoryScan(tempDir);

        assertEquals(BuildTool.MAVEN, scan.getBuildTool());
    }

    @Test
    void shouldDetectGradle() throws Exception {
        createFile("build.gradle", "");

        DirectoryScan scan = new DirectoryScan(tempDir);

        assertEquals(BuildTool.GRADLE, scan.getBuildTool());
    }

    @Test
    void shouldDetectGradleWrapper() throws Exception {
        createFile("gradlew", "");

        DirectoryScan scan = new DirectoryScan(tempDir);

        assertEquals(BuildTool.GRADLE_WRAPPER, scan.getBuildTool());
    }

    @Test
    void shouldThrowWhenNoBuildToolFound() {
        DirectoryScan scan = new DirectoryScan(tempDir);

        assertThrows(IllegalArgumentException.class,
                scan::getBuildTool);
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

        Path xml = createFile("TEST-report.xml", fullJUnitXml());

        DirectoryScan scan = new DirectoryScan(tempDir);

        TestExecutionReport report = scan.getTestsReports();

        assertEquals(1, report.getSuites().size());

        TestSuiteResult suite = report.getSuites().get(0);

        assertEquals(3, suite.getTests());
        assertEquals(1, suite.getTotalFailures());
        assertEquals(1, suite.getErrors());
        assertEquals(1, suite.getSkipped());

        assertEquals(4, suite.getTestCases().size());
    }

    // ===============================
    // PRIVATE METHOD COVERAGE
    // ===============================

    @Test
    void shouldCallExtractSuitCaseViaReflection()
            throws Exception {

        Path xml = createFile("TEST-x.xml", fullJUnitXml());

        DirectoryScan scan = new DirectoryScan(tempDir);

        Method m = DirectoryScan.class
                .getDeclaredMethod("extractSuitCase", File.class);

        m.setAccessible(true);

        Object suite = m.invoke(scan, xml.toFile());

        assertNotNull(suite);
    }

    // ===============================
    // FAILURE DETAIL EXTRACTION
    // ===============================

    @Test
    void shouldExtractFailureDetails()
            throws Exception {

        Path xml = createFile("TEST-fail.xml", fullJUnitXml());

        DirectoryScan scan = new DirectoryScan(tempDir);

        TestExecutionReport report = scan.getTestsReports();

        assertNotNull(report.getSuites().get(0).getFailureDetails());
        assertFalse(report.getSuites().get(0).getFailureDetails().isEmpty());
    }

    // ===============================
    // SETTERS / GETTERS
    // ===============================

    @Test
    void shouldSetFindeds() {
        DirectoryScan scan = new DirectoryScan(tempDir);

        scan.setFindeds(List.of(tempDir));

        assertEquals(1, scan.getFindeds().size());
    }

    @Test
    void shouldSetDependencies() {
        DirectoryScan scan = new DirectoryScan(tempDir);

        scan.setDependenciesPath(List.of(tempDir));

        assertEquals(1, scan.getDependenciesPath().size());
    }

    @Test
    void shouldHandleMissingSkippedAttribute() throws Exception {

        String xml = """
                <testsuite name="S" tests="1" failures="0" errors="0" skipped="" time="0.1">
                    <testcase classname="A" name="t" time="0.1"/>
                </testsuite>
                """;

        createFile("TEST-no-skip.xml", xml);

        DirectoryScan scan = new DirectoryScan(tempDir);

        TestExecutionReport report = scan.getTestsReports();

        assertEquals(0, report.getSuites().get(0).getSkipped());
    }

    @Nested
    public class WorkersQuantities {

        @TempDir
        Path tempDir;

        @Test
        void shouldCalculateRepoSize() throws IOException {

            // 2 arquivos de 1MB
            Files.write(tempDir.resolve("a.txt"), new byte[1024 * 1024]);
            Files.write(tempDir.resolve("b.txt"), new byte[1024 * 1024]);

            DirectoryScan calc = new DirectoryScan(tempDir);

            long size = calc.repoSizeMB();

            assertEquals(2, size);
        }

        @Test
        void shouldIgnoreTargetDirectory() throws IOException {

            Path target = Files.createDirectory(tempDir.resolve("target"));

            Files.write(target.resolve("big.bin"), new byte[5 * 1024 * 1024]);

            DirectoryScan calc = new DirectoryScan(tempDir);

            long size = calc.repoSizeMB();

            assertEquals(0, size);
        }

        @Test
        void shouldCalculateWorkersSmallRepo(@TempDir Path dir) throws IOException {

            Files.write(dir.resolve("a.txt"), new byte[10]);

            DirectoryScan scan = spy(new DirectoryScan(dir));

            doReturn(8).when(scan).availableProcessors();
            doReturn(16000L).when(scan).totalMemoryMB();
            doReturn(600L).when(scan).repoSizeMB();

            int workers = scan.calculateWorkers();

            assertTrue(workers >= 1);
        }

        @Test
        void shouldReduceWorkersForLargeRepo(@TempDir Path dir) throws IOException {

            Files.write(dir.resolve("big.bin"),
                    new byte[600 * 1024 * 1024]);

            DirectoryScan calc = spy(new DirectoryScan(dir));

            doReturn(8).when(calc).availableProcessors();
            doReturn(16000L).when(calc).totalMemoryMB();
            doReturn(600L).when(calc).repoSizeMB(); // simula repo grande

            int workers = calc.calculateWorkers();

            assertTrue(workers <= 4); // fator 0.5 aplicado
        }

        @Test
        void shouldReturnAtLeastOneWorker(@TempDir Path dir) {

            DirectoryScan calc = spy(new DirectoryScan(dir));

            doReturn(1).when(calc).availableProcessors();
            doReturn(500L).when(calc).totalMemoryMB();
            doReturn(10L).when(calc).repoSizeMB();

            int workers = calc.calculateWorkers();

            assertEquals(1, workers);
        }

    }

}
