package com.caio.args;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class ApplicationArguments {

    private boolean verbose;
    private Integer timeOut;
    private static final Set<String> EXISTENT_FLAGS = Set.of(
            "-v", "--startTimeOut");// , "--maxWorkers", "--minWorkers", "--maxRepoSizeMB", "--minRepoSizeMB");

    private final Path originalDirectory;
    private final List<String> flags;

    public ApplicationArguments() {
        this.originalDirectory = null;
        this.flags = null;
        this.verbose = false;
    }

    public ApplicationArguments(String[] args) {
        timeOut = 3;
        this.verbose = false;
        if (args.length == 0) {
            throw new IllegalArgumentException(
                    "Uso: java Main <flag> <diretorio>");
        }

        if (args.length == 1) {
            this.originalDirectory = Paths.get(args[0]);
            this.flags = null;
        } else if (args.length >= 2 && args.length <= EXISTENT_FLAGS.size() + 1) {
            this.flags = List.of(args);
            this.originalDirectory = Paths.get(flags.get(flags.size() - 1));

            verifyFlags();
        } else {
            throw new IllegalArgumentException(
                    "Muitos argumentos, no máximo 2");
        }
    }

    private void verifyFlags() {
        for (int i = 0; i < this.flags.size() - 1; i++) { // vai virar uma strategy, Open closed principle, e talvez um
                                                          // factory method para criar a strategy correta.
            String flag = this.flags.get(i).split("::")[0];
            if (!EXISTENT_FLAGS.contains(flag)) {
                throw new IllegalArgumentException(
                        "A flag " + this.flags.get(i) + " não existe.");
            }

            if (flag.equals("--startTimeOut")) {
                String[] parts = this.flags.get(i).split("::");
                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                            "A flag --startTimeOut deve ser seguida de um valor, ex: --startTimeOut::3");
                }
                try {
                    this.timeOut = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "O valor da flag --startTimeOut deve ser um número inteiro.");
                }
            }

            if (flag.equals("-v")) {
                if (this.flags.get(i).contains("::")) {
                    throw new IllegalArgumentException(
                            "A flag -v não deve ser seguida de um valor, ex: -v");
                }
                this.verbose = true;
            }

        }

    }

    public boolean isVerbose() {
        return verbose;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public Path getOriginalDirectory() {
        return originalDirectory;
    }

    public List<String> getFlags() {
        return flags;
    }

}
