package com.caio;

import static com.caio.util.HandleWithFile.copyToTemporaryDirectory;
import static com.caio.util.HandleWithFile.deleteTemporaryDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TemporaryDirectoryManager {

    private final List<Path> temporaryDirectories = new ArrayList<>();

    public TemporaryDirectoryManager() {
    }

    public void generate(Path path) throws IOException {
        if (path != null) {
            temporaryDirectories.add(copyToTemporaryDirectory(path));
        }
    }

    public void generate(Path path, Integer quantity) throws IOException {
        if (path != null && quantity != null) {
            for (int i = 0; i < quantity; i++) {
                temporaryDirectories.add(copyToTemporaryDirectory(path));
            }
        }
    }

    public void cleanup() throws IOException {
        for (Path path : temporaryDirectories) {
            try {
                deleteTemporaryDirectory(path);
            } catch (Exception e) {
                System.err.println("Erro ao deletar diretório temporário: " + path);
                e.printStackTrace();
            }
        }
        temporaryDirectories.clear();
    }

    public List<Path> getTemporaryDirectories() {
        return temporaryDirectories;
    }

    public Path getTemporaryDirectory(int index) {
        if (index >= 0 && index < temporaryDirectories.size()) {
            return temporaryDirectories.get(index);
        }
        return null;
    }

}
