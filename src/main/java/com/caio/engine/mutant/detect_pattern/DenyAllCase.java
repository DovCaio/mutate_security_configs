package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.DenyAllReplacementOperator;
import com.caio.engine.mutant.mutants.MutantStrategy;
import com.caio.engine.mutant.mutants.PermitAllReplacementOperator;

public class DenyAllCase extends AbstractDetectPattern implements DetectPattern {

    public DenyAllCase(String target) {
        super("(?:permitAll())", target);
        addMutantStrategy(new PermitAllReplacementOperator());
    }

    @Override
    public List<MutantStrategy> execute() {
        if (detect()) {
            return getMutantStrategies();
        }
        return new ArrayList<MutantStrategy>(List.of(new DenyAllReplacementOperator()));
    }

}
