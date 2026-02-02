package com.caio;

import com.caio.cli_controller.CliController;

import static com.caio.util.HandleWithFile.deleteTemporaryDirectory;

public class Main {

    public static void main(String[] args) throws Exception {
        CliController cliController = new CliController(args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                deleteTemporaryDirectory(cliController.getTemporaryDirectory());
            } catch (Exception ignored) {
            }
        }));

        try {
            cliController.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            deleteTemporaryDirectory(cliController.getTemporaryDirectory());
        }

    }
}
