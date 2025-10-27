package com.caio;

import com.caio.directory_scan.DirectoryScan;
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
    @DisplayName("Deve lançar NoOneClasseFinded se o diretório não contiver .class")
    void testThrowsWhenNoClassFiles() throws IOException {
        DirectoryScan scanner = new DirectoryScan(tempDir);
        assertThrows(NoOneClasseFinded.class, scanner::findClasses);
    }

    @Test
    @DisplayName("Deve retornar lista de arquivos .class encontrados")
    void testFindsClassFiles() throws IOException {
        // Cria arquivos simulando classes compiladas
        Files.createFile(tempDir.resolve("Main.class"));
        Files.createFile(tempDir.resolve("Helper.class"));
        Files.createFile(tempDir.resolve("not_a_class.txt"));

        DirectoryScan scanner = new DirectoryScan(tempDir);
        scanner.findClasses();
        List<Path> found = scanner.getFindeds();

        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(p -> p.toString().endsWith(".class")));
    }

    @Test
    @DisplayName("Deve encontrar arquivos .class em subdiretórios também")
    void testFindsClassesInSubdirectories() throws IOException {
        Path subDir = Files.createDirectories(tempDir.resolve("subdir"));
        Files.createFile(subDir.resolve("Inner.class"));

        DirectoryScan scanner = new DirectoryScan(tempDir);
        scanner.findClasses();
        List<Path> found = scanner.getFindeds();

        assertEquals(1, found.size());
        assertTrue(found.get(0).toString().endsWith("Inner.class"));
    }
}
