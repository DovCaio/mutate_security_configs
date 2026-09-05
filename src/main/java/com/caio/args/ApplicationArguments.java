package com.caio.args;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.caio.args.flags.FlagsConfig;
import com.caio.args.flags.FlagsStrategy;
import com.caio.args.flags.StartTimeOutFlag;
import com.caio.args.flags.VerboseFlag;
import com.caio.args.flags.WorkersQuantityFlag;

public class ApplicationArguments {

    private final Path originalDirectory;
    private final List<String> flags;
    private final List<FlagsStrategy> flagsStrategies = List.of(new VerboseFlag(), new StartTimeOutFlag(),
            new WorkersQuantityFlag());
    private final FlagsConfig flagsConfig = new FlagsConfig();
    private final Set<String> EXISTENT_FLAGS = flagsStrategies.stream()
            .map(FlagsStrategy::getFlagName)
            .collect(Collectors.toSet());

    public ApplicationArguments() {
        this.originalDirectory = null;
        this.flags = null;
    }

    public ApplicationArguments(String[] args) {
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
                    "Muitos argumentos.");
        }
    }

    private void flagsValidation() {
        for (int i = 0; i < this.flags.size() - 1; i++) {
            String flag = this.flags.get(i).split("::")[0];
            if (!EXISTENT_FLAGS.contains(flag)) {
                throw new IllegalArgumentException(
                        "A flag " + this.flags.get(i) + " não existe.");
            }
        }
    }

    private void verifyFlags() {
        flagsValidation();
        flagsStrategies.forEach(strategy -> strategy.execute(flags, flagsConfig));
    }

    public boolean isVerbose() {
        return flagsConfig.isVerbose();
    }

    public Integer getTimeOut() {
        return flagsConfig.timeOut();
    }

    public boolean workersDefined() {
        return flagsConfig.workersDefined();
    }

    public Integer getWorkersQuantity() {
        return flagsConfig.getWorkersQuantity();
    }

    public Path getOriginalDirectory() {
        return originalDirectory;
    }

    public List<String> getFlags() {
        return flags;
    }

}
