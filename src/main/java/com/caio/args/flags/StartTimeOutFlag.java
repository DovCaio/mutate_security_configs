package com.caio.args.flags;

import java.util.List;

public class StartTimeOutFlag implements FlagsStrategy {

    private static final String FLAG_NAME = "--startTimeOut";

    public StartTimeOutFlag() {
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
                    flagsConfig.setTimeOut(Integer.parseInt(parts[1]));
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
