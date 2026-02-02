package com.caio.directory_scan;

import com.caio.enums.BuildTool;
import com.caio.enums.TestStatus;
import com.caio.exceptions.NoOneClasseFinded;
import com.caio.exceptions.PathNotExists;
import com.caio.models.tests.FailureDetail;
import com.caio.models.tests.TestCaseResult;
import com.caio.models.tests.TestExecutionReport;
import com.caio.models.tests.TestSuiteResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

public class DirectoryScan {

    private Path directory;
    private List<Path> dependenciesPath;
    private List<Path> configsPath;
    private List<Path> findeds;
    private BuildTool buildTool;

    public DirectoryScan(Path baseDir) {
        if (baseDir == null)
            throw new IllegalArgumentException("O diretório base não pode ser nulo.");

        if (!Files.exists(baseDir) || !Files.isDirectory(baseDir))
            throw new PathNotExists("Diretório não encontrado: " + baseDir.toAbsolutePath());

        directory = baseDir;
    }

    public void findFiles(String extension) throws IOException {
        try (Stream<Path> stream = Files.walk(this.directory)) {
            List<Path> finded = stream
                    .filter(p -> p.toString().endsWith(extension))
                    .collect(Collectors.toList());

            if (finded.isEmpty())
                throw new NoOneClasseFinded("Nenhum arquivo " + extension + " encontrado em: "
                        + directory.toAbsolutePath()
                        + "\n Caso tenha passado o diretorio corretamnete, experimente compilar o projeto antes, para que seja gerado os arquivos que serão mutados.");
            this.findeds = finded;
        }
    }

    public BuildTool getBuildTool() {

        if (Files.exists(this.directory.resolve("pom.xml")))
            this.buildTool = BuildTool.MAVEN;
        else if (Files.exists(this.directory.resolve("build.gradle")))
            this.buildTool = BuildTool.GRADLE;
        else if (Files.exists(this.directory.resolve("gradlew")))
            this.buildTool = BuildTool.GRADLE_WRAPPER;
        else
            throw new IllegalArgumentException(
                    "Não foi possível identificar a ferramenta de build do projeto. Nenhum arquivo pom.xml, build.gradle ou gradlew encontrado no diretório raiz. Talvez esse não seja o diretório raiz do projeto.");

        return this.buildTool;
    }

    private FailureDetail extractFailure(Element element) { // Acho que vou ter que criar uma nova classe para tudo isso
                                                            // daqui
        FailureDetail fd = new FailureDetail(element.getAttribute("type"), element.getAttribute("message"),
                element.getTextContent());
        return fd;
    }

    private TestCaseResult extractTestCase(Element tc) {
        String className = tc.getAttribute("classname");
        String name = tc.getAttribute("name");
        double timeTestCase = Double.parseDouble(tc.getAttribute("time"));

        TestStatus status = TestStatus.PASSED;
        FailureDetail failure = null;

        if (tc.getElementsByTagName("failure").getLength() > 0) {
            status = TestStatus.FAILED;
            Element f = (Element) tc.getElementsByTagName("failure").item(0);
            failure = extractFailure(f);
        } else if (tc.getElementsByTagName("error").getLength() > 0) {
            status = TestStatus.ERROR;
            Element e = (Element) tc.getElementsByTagName("error").item(0);
            failure = extractFailure(e);
        } else if (tc.getElementsByTagName("skipped").getLength() > 0) {
            status = TestStatus.SKIPPED;
        }

        return new TestCaseResult(className, name, timeTestCase, status, failure);
    }

    private FailureDetail extractFailureDetail(Element element) {

        String message = element.getAttribute("message");
        String type = element.getAttribute("type");
        String stacktrace = element.getTextContent().trim();

        FailureDetail fd = new FailureDetail(type, message, stacktrace);

        return fd;
    }


    private List<FailureDetail> getFailureDetails(org.w3c.dom.Document document) {

        NodeList failures = document.getElementsByTagName("failure");

        if (failures.getLength() == 0) {
            return null; // ou ""
        }

        List<FailureDetail> failureDetails = new ArrayList<>();

        for (int i = 0; i < failures.getLength(); i++) {
            Element failure = (Element) failures.item(i);
            FailureDetail fd = extractFailureDetail(failure);
            failureDetails.add(fd);
        }

        return failureDetails;
    }

    private TestSuiteResult extractSuitCase(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(file);
        document.getDocumentElement().normalize();

        String testSuiteName = document.getDocumentElement().getAttribute("name");
        String tests = document.getDocumentElement().getAttribute("tests");
        String totalFailures = document.getDocumentElement().getAttribute("failures");
        String errors = document.getDocumentElement().getAttribute("errors");
        int skipped = Integer.parseInt(
                document.getDocumentElement().getAttribute("skipped").isEmpty() ? "0"
                        : document.getDocumentElement().getAttribute("skipped"));
        double time = Double.parseDouble(document.getDocumentElement().getAttribute("time"));
        List<FailureDetail> failureDetails = getFailureDetails(document);


        NodeList testCases = document.getElementsByTagName("testcase");

        List<TestCaseResult> testCaseResults = new ArrayList<>();

        for (int i = 0; i < testCases.getLength(); i++) {
            Element tc = (Element) testCases.item(i);
            TestCaseResult testCase = extractTestCase(tc);
            testCaseResults.add(testCase);
        }

        TestSuiteResult suiteResult = new TestSuiteResult(testSuiteName, Integer.parseInt(tests),
                Integer.parseInt(totalFailures), Integer.parseInt(errors), skipped, time, testCaseResults, failureDetails);
        return suiteResult;

    }

    public TestExecutionReport getTestsReports() throws IOException, ParserConfigurationException, SAXException {
        // Ler os arquivos de relatório de teste gerados pela ferramenta de build
        findFiles(".xml");

        List<File> reportFiles = this.findeds.stream()
                .filter(path -> path.getFileName().toString().startsWith("TEST-"))
                .map(Path::toFile)
                .collect(Collectors.toList());

        List<TestSuiteResult> testSuiteResults = new ArrayList<>();

        for (File file : reportFiles) {
            testSuiteResults.add(extractSuitCase(file));
        }

        TestExecutionReport testReport = new TestExecutionReport(testSuiteResults);

        return testReport;
    }

    public List<Path> getFindeds() {
        return findeds;
    }

    public void setFindeds(List<Path> findeds) {
        this.findeds = findeds;
    }

    public List<Path> getDependenciesPath() {
        return dependenciesPath;
    }

    public void setDependenciesPath(List<Path> dependenciesPath) {
        this.dependenciesPath = dependenciesPath;
    }

    public List<Path> getConfigsPath() {
        return configsPath;
    }

    public Path getDirectory() {
        return directory;
    }

}
