package com.caio;

import com.caio.cli_controller.CliController;

import java.io.IOException;

import com.caio.args.ApplicationArguments;

public class Main {

    public static void main(String[] args) throws Exception {
        TemporaryDirectoryManager temporaryDirectoryManager = new TemporaryDirectoryManager();
        ApplicationArguments applicationArguments = new ApplicationArguments(args);

        CliController cliController = new CliController(applicationArguments, temporaryDirectoryManager);

        shutDownHook(temporaryDirectoryManager);

        try {
            cliController.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // temporaryDirectoryManager.cleanup();
        }
    }

    private static void shutDownHook(TemporaryDirectoryManager manager) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            try {
                manager.cleanup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

}
