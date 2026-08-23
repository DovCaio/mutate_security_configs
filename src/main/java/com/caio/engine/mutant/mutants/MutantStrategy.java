package com.caio.engine.mutant.mutants;

import java.util.List;

public interface MutantStrategy {
    List<String> make(String value);
}
