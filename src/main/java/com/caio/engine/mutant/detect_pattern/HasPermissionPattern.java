package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.InvalidSecurityIdentifierReplacement;
import com.caio.engine.mutant.mutants.MutantStrategy;

public class HasPermissionPattern extends AbstractDetectPattern implements DetectPattern {

    public HasPermissionPattern(String target) {
        super("(?<!\\.)hasPermission\\(([^)]*)\\)", target);

    }

    @Override
    public List<MutantStrategy> execute() {
        if (!this.detect()) {
            return new ArrayList<MutantStrategy>();
        }
        addMutantStrategy(new InvalidSecurityIdentifierReplacement(getGroup(1)));
        return getMutantStrategies();
    }

}
