package com.caio.directory_scan;

import com.caio.exceptions.DependenciesNotFound;
import com.caio.exceptions.NoOneClasseFinded;
import com.caio.exceptions.PathNotExists;
import com.caio.exceptions.PomFileNotFoundException;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryScan {

    private Path directory;
    private List<Path> dependenciesPath;
    private List<Path> configsPath;
    private List<Path> findeds;

    public DirectoryScan(Path baseDir) {
        if (baseDir == null)
            throw new IllegalArgumentException("O diretório base não pode ser nulo.");

        if (!Files.exists(baseDir) || !Files.isDirectory(baseDir))
            throw new PathNotExists("Diretório não encontrado: " + baseDir.toAbsolutePath());

        directory = baseDir;
    }

    public void findClasses() throws IOException {
        try (Stream<Path> stream = Files.walk(this.directory)) {
            List<Path> finded = stream
                    .filter(p -> p.toString().endsWith(".class"))
                    .collect(Collectors.toList());

            if (finded.isEmpty())
                throw new NoOneClasseFinded("Nenhum arquivo .class encontrado em: " + directory.toAbsolutePath()
                        + "\n Caso tenha passado o diretorio corretamnete, experimente compilar o projeto antes, para que seja gerado os arquivos que serão mutados.");
            this.findeds = finded;
        }
    }

    public void findJarDependencies() throws IOException {
        try (Stream<Path> stream = Files.walk(this.directory)) {
            List<Path> dependenciesFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".jar"))
                    .collect(Collectors.toList());
            if (dependenciesFiles.isEmpty())
                throw new DependenciesNotFound("Dependências jar's não encontradas");
            this.dependenciesPath = dependenciesFiles;

        }
    }

    public void findConfigFiles() throws IOException {
        try (Stream<Path> stream = Files.walk(this.directory)) {
            List<Path> configsFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".properties")
                            || p.getFileName().toString().endsWith(".xml")
                            || p.getFileName().toString().endsWith(".lst")
                            || p.getFileName().toString().endsWith(".yml")
                            || p.getFileName().toString().endsWith(".yaml"))
                    .collect(Collectors.toList());
            if (configsFiles.isEmpty())
                throw new DependenciesNotFound("Arquivos de configurações não encontrados");
            this.configsPath = configsFiles;

        }
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

}
