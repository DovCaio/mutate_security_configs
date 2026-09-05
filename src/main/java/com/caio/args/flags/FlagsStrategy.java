package com.caio.args.flags;

import java.util.List;

public interface FlagsStrategy {
    void execute(List<String> flags, FlagsConfig flagsConfig);

    String getFlagName();
}
