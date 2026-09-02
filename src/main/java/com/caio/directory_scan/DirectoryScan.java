package com.caio.directory_scan;

import com.caio.enums.BuildTool;
import com.caio.exceptions.NoOneClasseFinded;
import com.caio.exceptions.PathNotExists;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
