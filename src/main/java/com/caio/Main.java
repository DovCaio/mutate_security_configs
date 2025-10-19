package com.caio;
import com.caio.directory_scan.DirectoryScan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{

    public static void main( String[] args ) throws IOException {

        if (args.length == 0) {
            System.err.println("Uso: java Main <diretorio>");
            System.exit(1);
        }

        Path directory = Paths.get(args[0]);

        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            System.err.println("Erro: caminho inválido ou não é um diretório");
            System.exit(1);
        }

        DirectoryScan directoryScan = new DirectoryScan(directory);
        directoryScan.scan();

    }
}
