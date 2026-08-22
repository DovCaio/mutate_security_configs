package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.AuthorizationReplacementOperator;
import com.caio.engine.mutant.mutants.EmptyAuthorizationArgumentOperator;
import com.caio.engine.mutant.mutants.LogicalNegationSecurityOperator;
import com.caio.engine.mutant.mutants.MutantStrategy;
import com.caio.engine.mutant.mutants.SecurityExpressionTypeReplacement;
import com.caio.engine.mutant.mutants.SecurityIdentifierTypeReplacement;

public class SimpleCasePattern extends AbstractDetectPattern implements DetectPattern {

    public SimpleCasePattern(String target, List<String> sameSecurityIdentifier,
            List<String> diffSecurityIdentifier) {
        super("(!?)(?:hasAuthority|hasRole)\\\\(['\\\"]([^'\\\"]+)['\\\"]\\\\)", target);

        addMutantStrategy(new LogicalNegationSecurityOperator(getMatcher()));
        addMutantStrategy(new SecurityExpressionTypeReplacement());
        sameSecurityIdentifier
                .forEach(ra -> addMutantStrategy(new AuthorizationReplacementOperator(getMatcher(), ra)));
        diffSecurityIdentifier
                .forEach(ra -> addMutantStrategy(new SecurityIdentifierTypeReplacement(getMatcher(), ra)));
        addMutantStrategy(new EmptyAuthorizationArgumentOperator());
    }

    @Override
    public List<MutantStrategy> execute() {
        if (this.detect()) {
            return getMutantStrategies();
        }
        return new ArrayList<MutantStrategy>();
    }
}
