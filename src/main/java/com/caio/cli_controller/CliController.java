package com.caio.cli_controller;

import com.caio.TemporaryDirectoryManager;
import com.caio.analize.CodeAnalyzer;
import com.caio.args.ApplicationArguments;
import com.caio.directory_scan.DirectoryScan;
import com.caio.engine.Engine;
import com.caio.engine.EngineParams;
import com.caio.report.Report;

import static com.caio.util.Printers.printMutationPoints;
import static com.caio.util.Printers.printPaths;
import static com.caio.util.Printers.printSimpleListString;

import java.io.IOException;

public class CliController {

    private ApplicationArguments applicationArguments;
    private DirectoryScan directoryScan;
    private CodeAnalyzer bca;
    private Engine engine;
    private Report report;
    private TemporaryDirectoryManager temporaryDirectoryManager;

    public CliController(ApplicationArguments applicationArguments, TemporaryDirectoryManager temporaryDirectoryManager)
            throws IOException {

        this.applicationArguments = applicationArguments;
        this.temporaryDirectoryManager = temporaryDirectoryManager;

        this.bca = new CodeAnalyzer();
        this.directoryScan = new DirectoryScan(applicationArguments.getOriginalDirectory());

    }

    public void execute() throws Exception {
        this.scanForDotFiles();
        this.searchForPossibleMutations();
        this.startEngine();
        this.generateReport();
    }

    private void scanForDotFiles() throws IOException {
        directoryScan.findFiles(".java");
        if (applicationArguments.isVerbose())
            printPaths(directoryScan.getFindeds());
    }

    private void searchForPossibleMutations() throws Exception {
        this.bca.analyze(directoryScan.getFindeds());
        if (applicationArguments.isVerbose()) {
            printSimpleListString("Roles encontradas", bca.getRoles());
            printSimpleListString("Authorities encontradas", bca.getAuthorities());
            System.out.println("Possíveis pontos de mutação:");
            printMutationPoints(bca.getMutationsPoints());

        }
    }

    private void startEngine() throws Exception {
        this.engine = new Engine(new EngineParams(bca.getMutationsPoints(), bca.getmainClasses(),
                directoryScan.getBuildTool(), bca.getRoles(), bca.getAuthorities(),
                applicationArguments, temporaryDirectoryManager));
        engine.start();
    }

    private void generateReport() {
        this.report = new Report(engine.getTestsResults());
        this.report.generate(applicationArguments.getOriginalDirectory());
    }

}
