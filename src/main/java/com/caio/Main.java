package com.caio;
import com.caio.cli_controller.CliController;

import java.io.IOException;

public class Main
{

    public static void main( String[] args ) throws Exception {

        CliController cliController = new CliController(args);
        cliController.execute();

    }
}
