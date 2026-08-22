package com.caio.engine.mutant.mutants;

import java.util.List;
import java.util.regex.Matcher;

public interface MutantStrategy {
    String make(String value);
}
