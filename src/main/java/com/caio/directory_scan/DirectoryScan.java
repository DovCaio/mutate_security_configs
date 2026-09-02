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
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.management.OperatingSystemMXBean;

public class DirectoryScan { // Meu pai amado, isso precisa urgentemente ser refatorado, está muito grande e
                             // com muitas responsabilidades, talvez seja melhor criar uma classe para cada
                             // responsabilidade, como por exemplo, uma classe para scanear os arquivos,
                             // outra para identificar a ferramenta de build, outra para ler os relatórios de
                             // teste, etc.

    private Path directory;
    private List<Path> dependenciesPath;
    private List<Path> configsPath;
    private List<Path> findeds;
    private BuildTool buildTool;

    private static final Set<String> IGNORE_DIRS = Set.of(
            ".git", "target", "build", ".gradle", "node_modules");

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

    protected Long repoSizeMB() {

        try (Stream<Path> walk = Files.walk(directory)) {

            long bytes = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> IGNORE_DIRS.stream()
                            .noneMatch(dir -> path.toString().contains(dir)))
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();

            return bytes / (1024 * 1024);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao calcular tamanho do repositório", e);
        }
    }

    protected int availableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    protected long totalMemoryMB() {
        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return os.getTotalMemorySize() / (1024 * 1024);
    }

    public int calculateWorkers() {
        int cores = availableProcessors();

        int workersByCpu = Math.max(1, cores / 2);

        long totalMemMB = totalMemoryMB();

        int workerMemoryMB = 800;

        int workersByMemory = (int) ((totalMemMB * 0.6) / workerMemoryMB);

        long repoSizeMBVar = repoSizeMB();

        double repoFactor = repoSizeMBVar > 500 ? 0.5 : repoSizeMBVar > 200 ? 0.7 : 1.0;

        int workers = (int) (Math.min(workersByCpu, workersByMemory) * repoFactor);

        return Math.max(workers, 1);
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
