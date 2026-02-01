package com.caio;

    import com.caio.cli_controller.CliController;

public class Main {

    public static void main(String[] args) throws Exception {

        try {
            CliController cliController = new CliController(args);
            cliController.execute();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
