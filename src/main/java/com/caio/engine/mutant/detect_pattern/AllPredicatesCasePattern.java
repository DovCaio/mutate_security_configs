package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.DenyAllReplacementOperator;
import com.caio.engine.mutant.mutants.LogicalNegationSecurityOperator;
import com.caio.engine.mutant.mutants.MutantStrategy;
import com.caio.engine.mutant.mutants.PermitAllReplacementOperator;

public class AllPredicatesCasePattern extends AbstractDetectPattern implements DetectPattern {

    public AllPredicatesCasePattern(String target) {
        super(
                "!?((?:hasRole|hasAuthority|hasAnyRole|hasAnyAuthority|hasPermission)\\([^)]*\\)|@\\w+\\.hasPermission\\([^)]*\\))",
                target);
    }

    @Override
    public List<MutantStrategy> execute() {
        if (!detect()) {
            return new ArrayList<>();
        }
        addMutantStrategy(new LogicalNegationSecurityOperator(this.getMatcher()));
        addMutantStrategy(new PermitAllReplacementOperator());
        addMutantStrategy(new DenyAllReplacementOperator());
        return getMutantStrategies();
    }
}
