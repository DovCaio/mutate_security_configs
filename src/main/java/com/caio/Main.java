package com.caio;
import com.caio.analize.BytecodeAnalyzer;
import com.caio.directory_scan.DirectoryScan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main
{

    public static void main( String[] args ) throws IOException {

        if (args.length == 0) {
            System.err.println("Uso: java Main <diretorio>");
            System.exit(1);
        }

        Path directory = Paths.get(args[0]);

        DirectoryScan directoryScan = new DirectoryScan(directory);
        List<Path> finded_paths = directoryScan.scan();

        BytecodeAnalyzer bca = new BytecodeAnalyzer();

        for (Path path :  finded_paths) {
            bca.analyzeClass(path);
        }

    }
}
