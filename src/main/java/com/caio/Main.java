package com.caio;
import com.caio.analize.BytecodeAnalyzer;
import com.caio.directory_scan.DirectoryScan;
import com.caio.models.AnnotationMutationPoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.caio.utli.Printers.printMutationPoints;
import static com.caio.utli.Printers.printPaths;

public class Main
{

    private static final List<String> EXISTENT_FLAGS = List.of("-v");

    public static void main( String[] args ) throws IOException {

        if (args.length == 0) {
            System.err.println("Uso: java  Main <flag> <diretorio>");
            System.exit(1);
        }

        Path directory;
        String flag = "";

        if (args.length == 1) { //provisorio, somente para já ter a opção de flags
            directory = Paths.get(args[0]);;
        }else if (args.length == 2) {
            directory = Paths.get(args[1]);;
            flag = args[0];
            if(!EXISTENT_FLAGS.contains(flag)) throw new IllegalArgumentException("A flag " + flag + " não existe.");
        }else {
            throw new IllegalArgumentException("Muitos argumentos, no máximo 2");
        }

        DirectoryScan directoryScan = new DirectoryScan(directory);
        List<Path> finded_paths = directoryScan.scan();
        if(flag.equals("-v")) printPaths(finded_paths);

        BytecodeAnalyzer bca = new BytecodeAnalyzer(finded_paths);
        List<AnnotationMutationPoint> amp =  bca.analyzeClass();
        if(flag.equals("-v")) printMutationPoints(amp);

    }
}
