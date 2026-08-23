package com.caio.engine.mutant.detect_pattern;

import java.util.List;

import com.caio.engine.mutant.mutants.MutantStrategy;

public class AllPredicatesCasePattern extends AbstractDetectPattern implements DetectPattern {

    public AllPredicatesCasePattern(String target) {
        super("(!?)(hasRole\\\\([^)]*\\\\)|hasAuthority\\\\([^)]*\\\\)|hasAnyRole\\\\([^)]*\\\\)|hasAnyAuthority\\\\([^)]*\\\\)|hasPermission\\\\([^)]*\\\\)|@\\\\w+\\\\.hasPermission\\\\([^)]*\\\\))",
                target);
    }

    @Override
    public List<MutantStrategy> execute() {
        // TODO Auto-generated method stub
        return null;
    }
}
