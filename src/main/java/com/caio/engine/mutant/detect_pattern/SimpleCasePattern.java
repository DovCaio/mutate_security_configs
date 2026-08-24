package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.AuthorizationReplacementOperator;
import com.caio.engine.mutant.mutants.EmptyAuthorizationArgumentOperator;
import com.caio.engine.mutant.mutants.InvalidSecurityIdentifierReplacement;
import com.caio.engine.mutant.mutants.MutantStrategy;
import com.caio.engine.mutant.mutants.SecurityExpressionTypeReplacement;
import com.caio.engine.mutant.mutants.SecurityIdentifierTypeReplacement;

public class SimpleCasePattern extends AbstractDetectPattern implements DetectPattern {

    private List<String> sameSecurityIdentifier;
    private List<String> diffSecurityIdentifier;
    private String insideQuotes;

    public SimpleCasePattern(String target, List<String> sameSecurityIdentifier,
            List<String> diffSecurityIdentifier) {
        super("(!?)(?:hasAuthority|hasRole)\\(['\"]([^'\"]+)['\"]\\)", target);
        this.sameSecurityIdentifier = sameSecurityIdentifier;
        this.diffSecurityIdentifier = diffSecurityIdentifier;
        this.insideQuotes = getGroup(2);
    }

    @Override
    public List<MutantStrategy> execute() {
        if (!this.detect()) {
            return new ArrayList<MutantStrategy>();
        }

        addMutantStrategy(new InvalidSecurityIdentifierReplacement(getMatcher()));
        addMutantStrategy(new SecurityExpressionTypeReplacement());
        sameSecurityIdentifier
                .forEach(ra -> addMutantStrategy(new AuthorizationReplacementOperator(insideQuotes, ra)));
        diffSecurityIdentifier
                .forEach(ra -> addMutantStrategy(new SecurityIdentifierTypeReplacement(insideQuotes, ra)));
        addMutantStrategy(new EmptyAuthorizationArgumentOperator());

        return this.getMutantStrategies();
    }
}
