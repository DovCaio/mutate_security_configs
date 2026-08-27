package com.caio.args;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class ApplicationArguments {

    private final boolean verbose;
    private static final Set<String> EXISTENT_FLAGS = Set.of(
            "-v");

    private final Path originalDirectory;
    private final String flag;

    public ApplicationArguments() {
        this.originalDirectory = null;
        this.flag = null;
        this.verbose = false;
    }

    public ApplicationArguments(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException(
                    "Uso: java Main <flag> <diretorio>");
        }

        if (args.length == 1) {
            this.originalDirectory = Paths.get(args[0]);
            this.flag = null;
        } else if (args.length == 2) {
            this.originalDirectory = Paths.get(args[1]);
            this.flag = args[0];

            if (!EXISTENT_FLAGS.contains(this.flag)) {
                throw new IllegalArgumentException(
                        "A flag " + this.flag + " não existe.");
            }
        } else {
            throw new IllegalArgumentException(
                    "Muitos argumentos, no máximo 2");
        }
        this.verbose = parseVerbose(args);
    }

    private boolean parseVerbose(String[] args) {
        for (String arg : args) {
            if (arg.equals("-v")) {
                return true;
            }
        }

        return false;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public Path getOriginalDirectory() {
        return originalDirectory;
    }

    public String getFlag() {
        return flag;
    }

}
