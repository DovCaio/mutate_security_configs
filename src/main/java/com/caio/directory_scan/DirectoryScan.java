package com.caio.directory_scan;
import com.caio.exceptions.NoOneClasseFinded;
import com.caio.exceptions.PathNotExists;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryScan {

    private Path directory;

    public DirectoryScan(Path baseDir){
        if (baseDir == null)
            throw new IllegalArgumentException("O diretório base não pode ser nulo.");

        if (!Files.exists(baseDir) || !Files.isDirectory(baseDir))
            throw new PathNotExists("Diretório não encontrado: " + baseDir.toAbsolutePath());
        directory = baseDir;

    }

    public List<Path> scan() throws IOException {
        try (Stream<Path> stream = Files.walk(this.directory)) {
            List<Path> finded =  stream
                    .filter(p -> p.toString().endsWith(".class"))
                    .collect(Collectors.toList());

            if (finded.isEmpty()) throw new NoOneClasseFinded("Nenhum arquivo .class encontrado em: " + directory.toAbsolutePath());

            return finded;
        }
    }


}
