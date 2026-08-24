package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;

public class DenyAllReplacementOperator implements MutantStrategy {

    @Override
    public List<String> make(String value) {
        return List.of("denyAll");
    }
}
