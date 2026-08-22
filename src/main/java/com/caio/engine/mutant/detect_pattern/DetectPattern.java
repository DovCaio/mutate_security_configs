package com.caio.engine.mutant.detect_pattern;

import java.util.List;

import com.caio.engine.mutant.mutants.MutantStrategy;

public interface DetectPattern {

    List<MutantStrategy> execute();

}
