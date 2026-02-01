package com.caio.cli_controller;

import com.caio.analize.CodeAnalyzer;
import com.caio.directory_scan.DirectoryScan;
import com.caio.engine.Engine;
import com.caio.report.Report;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.caio.utli.Printers.printMutationPoints;
import static com.caio.utli.Printers.printPaths;
import static com.caio.utli.Printers.printSimpleListString;

public class CliController {

    private static final List<String> EXISTENT_FLAGS = List.of("-v");
    private Path directory;
    private String flag = "";
    private DirectoryScan directoryScan;
    private CodeAnalyzer bca;
    private Engine engine;
    private Report report;

    public CliController(String[] args) {
        if (args.length == 0) {
            System.err.println("Uso: java  Main <flag> <diretorio>");
            System.exit(1);
        }

        if (args.length == 1) {
            this.directory = Paths.get(args[0]);
        } else if (args.length == 2) {
            this.directory = Paths.get(args[1]);
            this.flag = args[0];
            if (!EXISTENT_FLAGS.contains(this.flag))
                throw new IllegalArgumentException("A flag " + this.flag + " não existe.");
        } else {
            throw new IllegalArgumentException("Muitos argumentos, no máximo 2");
        }

        this.bca = new CodeAnalyzer();
        this.directoryScan = new DirectoryScan(directory);
    }

    public void execute() throws Exception {
        this.scanForDotFiles();
        this.searchForPossibleMutations();
        this.startEngine();
        this.generateReport();
    }

    private void scanForDotFiles() throws IOException {
        directoryScan.findFiles(".java");
        if (flag.equals("-v"))
            printPaths(directoryScan.getFindeds());
    }

    private void searchForPossibleMutations() throws Exception {
        this.bca.analyze(directoryScan.getFindeds());
        if (flag.equals("-v")){
            printSimpleListString("Roles encontradas", bca.getRoles());
            printSimpleListString("Authorities encontradas", bca.getAuthorities());
            printMutationPoints(bca.getMutationsPoints());

        }
    }

    private void startEngine() throws Exception {
        this.engine = new Engine(bca.getMutationsPoints(), bca.getmainClasses(), directoryScan.getDirectory(),
                directoryScan.getBuildTool(), bca.getRoles(), bca.getAuthorities(), flag);
        engine.start();
        if (flag.equals("-v")) {
            System.out.println("Mutantes");
            printMutationPoints(engine.getMutants());

            List<String> allResults = engine.getTestsResults().stream().map(value -> value.toString())
                    .collect(Collectors.toList());
            ;
            allResults.stream().forEach(value -> System.out.println(value));
        }
    }

    
    private void generateReport() {
        this.report = new Report(engine.getTestsResults());
        this.report.generate(directory);
    }

}
