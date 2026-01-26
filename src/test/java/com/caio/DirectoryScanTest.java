package com.caio;

import com.caio.directory_scan.DirectoryScan;
import com.caio.enums.BuildTool;
import com.caio.exceptions.NoOneClasseFinded;
import com.caio.exceptions.PathNotExists;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryScanTest {

    private Path tempDir;

    @BeforeEach
    void setup() throws IOException {
        // Cria um diretório temporário para cada teste
        tempDir = Files.createTempDirectory("scan-test-");
    }

    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {}
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
}
