package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.DenyAllReplacementOperator;
import com.caio.engine.mutant.mutants.MutantStrategy;

public class PermitAllCase extends AbstractDetectPattern implements DetectPattern {

    public PermitAllCase(String target) {
        super("(?<!\\w)permitAll\\(\\)", target);

    }

    @Override
    public List<MutantStrategy> execute() {
        if (!detect()) {
            return new ArrayList<MutantStrategy>();
        }
        if (!getMutantStrategies().isEmpty()) {
            return getMutantStrategies();
        }
        addMutantStrategy(new DenyAllReplacementOperator());
        return getMutantStrategies();
    }
}
