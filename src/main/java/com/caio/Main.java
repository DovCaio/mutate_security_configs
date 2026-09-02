package com.caio;

import com.caio.cli_controller.CliController;

import static com.caio.util.HandleWithFile.deleteTemporaryDirectory;

import java.io.IOException;

import com.caio.args.ApplicationArguments;

public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationArguments applicationArguments = new ApplicationArguments(args);
        CliController cliController = new CliController(applicationArguments);

        shutDownHook(cliController);

        try {
            cliController.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            cliController.getTemporaryDirectories().stream().forEach(path -> {
                try {
                    if (path != null)
                        deleteTemporaryDirectory(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private static void shutDownHook(CliController cliController) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cliController.getTemporaryDirectories().stream().forEach(path -> {
                try {
                    if (path != null)
                        deleteTemporaryDirectory(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }));
    }
}
