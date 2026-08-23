package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.LogicalSecurityOperatorReplacement;
import com.caio.engine.mutant.mutants.MutantStrategy;

public class LogicalCase extends AbstractDetectPattern implements DetectPattern {

    public LogicalCase(String target) {
        super("\\\\b(and|or)\\\\b", target);
        addMutantStrategy(new LogicalSecurityOperatorReplacement(getMatcher()));
    }

    @Override
    public List<MutantStrategy> execute() {
        if (this.detect()) {
            return getMutantStrategies();
        }
        return new ArrayList<MutantStrategy>();
    }

}
