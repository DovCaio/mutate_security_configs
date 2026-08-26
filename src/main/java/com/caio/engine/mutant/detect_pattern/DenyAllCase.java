package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.MutantStrategy;
import com.caio.engine.mutant.mutants.PermitAllReplacementOperator;

public class DenyAllCase extends AbstractDetectPattern implements DetectPattern {

    public DenyAllCase(String target) {
        super("(?<!\\w)denyAll(?!\\s*\\()", target);
    }

    @Override
    public List<MutantStrategy> execute() {
        if (!detect()) {
            return new ArrayList<MutantStrategy>();
        }
        if (!getMutantStrategies().isEmpty()) {
            return getMutantStrategies();
        }
        addMutantStrategy(new PermitAllReplacementOperator());
        return getMutantStrategies();
    }

}
