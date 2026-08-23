package com.caio.engine.mutant.detect_pattern;

import java.util.List;

import com.caio.engine.mutant.mutants.MutantStrategy;

public class HasPermissionPattern extends AbstractDetectPattern implements DetectPattern {

    public HasPermissionPattern(String target) {
        super("(?<!\\\\.)hasPermission\\\\(([^)]*)\\\\)", target);
    }

    @Override
    public List<MutantStrategy> execute() {
        // TODO Auto-generated method stub
        return null;
    }

}
