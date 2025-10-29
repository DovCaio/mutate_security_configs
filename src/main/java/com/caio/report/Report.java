package com.caio.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.caio.engine.RunTest;
import com.caio.utli.GetData;

public class Report {

    private List<RunTest.TestResult> testResults;



    public Report(List<RunTest.TestResult> testResults){
        this.testResults = testResults;
    }

    private void generateCss(Path directory) {
        try {
            Path cssDirectory = directory.resolve("reports/assets/css");

            if (Files.exists(cssDirectory)) return;

            Files.createDirectories(cssDirectory);
                

            Path cssFile = cssDirectory.resolve("style.css");

            String cssContent = Contents.css();

            Files.writeString(cssFile, cssContent, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.err.println("Erro ao gerar CSS: " + e.getMessage());
        }
    }

    

    private void generateHtml(Path directory){
        try {

            Path htmlDirectory = directory.resolve("reports/report_" + GetData.mothDayMinutsAndSecs());
            if (Files.notExists(htmlDirectory)) {
                Files.createDirectories(htmlDirectory);       
            }

            Path cssFile = htmlDirectory.resolve("index.html");

            String content = Contents.html(testResults);

            Files.writeString(cssFile, content, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING);

            
        }catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao gerar o relatório: " + e.getMessage());

        }


    }


    public void generate(Path directory){

        this.generateCss(directory);
        this.generateHtml(directory);
    }
    
}
