package com.caio.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class HandleWithFile {

    public static Path copyToTemporaryDirectory(Path originalPath) throws IOException {

        if (originalPath == null || !Files.exists(originalPath)) {
            throw new IllegalArgumentException("Arquivo ou diretório inválido");
        }

        Path systemTemp = Path.of(System.getProperty("java.io.tmpdir"));

        Path targetDir = systemTemp.resolve(
                originalPath.getFileName() + "-" + System.nanoTime());

        Files.walk(originalPath).forEach(source -> {
            try {
                Path destination = targetDir.resolve(originalPath.relativize(source));

                if (Files.isDirectory(source)) {
                    Files.createDirectories(destination);
                } else {
                    Files.copy(
                            source,
                            destination,
                            StandardCopyOption.REPLACE_EXISTING);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return targetDir;
    }

    public static void deleteTemporaryDirectory(Path tempPath) throws IOException {
        if (tempPath == null || !Files.exists(tempPath)) {
            throw new IllegalArgumentException("Diretório temporário inválido");
        }

        try (var walk = Files.walk(tempPath)) {
            walk.sorted(java.util.Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Erro ao deletar: " + path, e);
                        }
                    });
        }
    }
}