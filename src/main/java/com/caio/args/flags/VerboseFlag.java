package com.caio.args.flags;

import java.util.List;

public class VerboseFlag implements FlagsStrategy {

    private static final String FLAG_NAME = "-v";

    public VerboseFlag() {
    }

    @Override
    public void execute(List<String> flags, FlagsConfig flagsConfig) {
        for (String arg : flags) {
            if (arg.equals(FLAG_NAME)) {
                flagsConfig.setVerbose(true);
                break;
            }
        }
    }

    public String getFlagName() {
        return FLAG_NAME;
    }

}
