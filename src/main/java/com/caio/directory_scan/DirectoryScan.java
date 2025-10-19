package com.caio.directory_scan;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class DirectoryScan {

    private Path directory;

    public DirectoryScan(Path baseDir){
        directory = baseDir;

    }

    public void scan() throws IOException {

        try ( Stream<Path> stream = Files.walk(this.directory)) {
            stream
                    .filter(p -> p.toString().endsWith(".class"))
                    .forEach(System.out::println);
        }

    }


}
