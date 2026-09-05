package com.caio.args.flags;

import java.util.List;

import com.caio.worker_count_calculator.WorkerCountCalculator;

public class WorkersQuantityFlag implements FlagsStrategy {

    private static final String FLAG_NAME = "--workersQuantity";

    public WorkersQuantityFlag() {
    }

    @Override
    public void execute(List<String> flags, FlagsConfig flagsConfig) {
        for (String arg : flags) {
            if (arg.startsWith(FLAG_NAME)) {
                String[] parts = arg.split("::");
                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                            "A flag " + FLAG_NAME + " deve ser seguida de um valor, ex: " + FLAG_NAME + "::5");
                }

                try {
                    int workersQuantity = Integer.parseInt(parts[1]);
                    if (workersQuantity <= 0) {
                        throw new IllegalArgumentException(
                                "O valor da flag " + FLAG_NAME + " deve ser um número inteiro positivo, ex: "
                                        + FLAG_NAME + "::5");
                    }
                    int maxWorkers = WorkerCountCalculator.logicalCores();
                    if (workersQuantity > maxWorkers) {
                        throw new IllegalArgumentException(
                                "O valor da flag " + FLAG_NAME
                                        + " não pode ser maior que o número de núcleos lógicos da máquina, que é "
                                        + maxWorkers);
                    }
                    flagsConfig.setWorkersQuantity(workersQuantity);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "O valor da flag " + FLAG_NAME + " deve ser um número inteiro, ex: " + FLAG_NAME + "::5");
                }
                break;
            }
        }
    }

    public String getFlagName() {
        return FLAG_NAME;
    }

}
