package com.caio.cli_controller;

import com.caio.analize.CodeAnalyzer;
import com.caio.directory_scan.DirectoryScan;
import com.caio.engine.Engine;
import com.caio.report.Report;

import static com.caio.util.Printers.printMutationPoints;
import static com.caio.util.Printers.printPaths;
import static com.caio.util.Printers.printSimpleListString;
import static com.caio.util.HandleWithFile.copyToTemporaryDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CliController {

    private static final List<String> EXISTENT_FLAGS = List.of("-v");
    private Path originalDirectory;
    private Path temporaryDirectory;
    private String flag = "";
    private DirectoryScan directoryScan;
    private CodeAnalyzer bca;
    private Engine engine;
    private Report report;

    public CliController(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Uso: java  Main <flag> <diretorio>");
            System.exit(1);
        }

        if (args.length == 1) {
            this.originalDirectory = Paths.get(args[0]);
        } else if (args.length == 2) {
            this.originalDirectory = Paths.get(args[1]);
            this.flag = args[0];
            if (!EXISTENT_FLAGS.contains(this.flag))
                throw new IllegalArgumentException("A flag " + this.flag + " não existe.");
        } else {
            throw new IllegalArgumentException("Muitos argumentos, no máximo 2");
        }

        temporaryDirectory = copyToTemporaryDirectory(originalDirectory);

        this.bca = new CodeAnalyzer();
        this.directoryScan = new DirectoryScan(temporaryDirectory);
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
            System.out.println("Possíveis pontos de mutação:");
            printMutationPoints(bca.getMutationsPoints());

        }
    }

    private void startEngine() throws Exception {
        this.engine = new Engine(bca.getMutationsPoints(), bca.getmainClasses(), directoryScan.getDirectory(),
                directoryScan.getBuildTool(), bca.getRoles(), bca.getAuthorities(), flag);
        engine.start();
    }

    private void generateReport() {
        this.report = new Report(engine.getTestsResults());
        this.report.generate(originalDirectory);
    }

    public Path getTemporaryDirectory() {
        return temporaryDirectory;
    }

    
}
