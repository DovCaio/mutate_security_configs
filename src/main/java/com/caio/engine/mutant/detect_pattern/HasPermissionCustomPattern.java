package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.InvalidSecurityIdentifierReplacement;
import com.caio.engine.mutant.mutants.MutantStrategy;

public class HasPermissionCustomPattern extends AbstractDetectPattern implements DetectPattern {

    public HasPermissionCustomPattern(String target) {
        super("@(\\w+)\\.hasPermission\\s*\\(\\s*([^)]*)\\)", target);
    }

    @Override
    public List<MutantStrategy> execute() {
        if (!this.detect()) {
            return new ArrayList<MutantStrategy>();
        }
        if (!getMutantStrategies().isEmpty()) {
            return getMutantStrategies();
        }
        addMutantStrategy(new InvalidSecurityIdentifierReplacement(getGroup(2), getGroup(1)));
        return getMutantStrategies();
    }
}
