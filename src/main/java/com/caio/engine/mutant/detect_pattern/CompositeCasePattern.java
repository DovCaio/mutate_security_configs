package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;

import com.caio.engine.mutant.mutants.AuthorizationParameterReductionOperator;
import com.caio.engine.mutant.mutants.AuthorizationReplacementOperator;
import com.caio.engine.mutant.mutants.DenyAllReplacementOperator;
import com.caio.engine.mutant.mutants.EmptyAuthorizationArgumentOperator;
import com.caio.engine.mutant.mutants.InvalidSecurityIdentifierReplacement;
import com.caio.engine.mutant.mutants.LogicalNegationSecurityOperator;
import com.caio.engine.mutant.mutants.MutantStrategy;
import com.caio.engine.mutant.mutants.PermitAllReplacementOperator;
import com.caio.engine.mutant.mutants.SecurityExpressionTypeReplacement;
import com.caio.engine.mutant.mutants.SecurityIdentifierTypeReplacement;

public class CompositeCasePattern extends AbstractDetectPattern implements DetectPattern {

    private List<String> sameSecurityIdentifier;
    private List<String> diffSecurityIdentifier;

    public CompositeCasePattern(String target, List<String> sameSecurityIdentifier,
            List<String> diffSecurityIdentifier) {
        super("(!?)(?:hasAnyRole|hasAnyAuthority)\\s*\\(\\s*([^)]*)\\s*\\)", target);
        this.sameSecurityIdentifier = sameSecurityIdentifier;
        this.diffSecurityIdentifier = diffSecurityIdentifier;

    }

    @Override
    public List<MutantStrategy> execute() {
        if (!this.detect()) {
            return new ArrayList<MutantStrategy>();
        }

        if (!getMutantStrategies().isEmpty()) {
            return getMutantStrategies();
        }

        String insideQuotes = getGroup(2);
        addMutantStrategy(new SecurityExpressionTypeReplacement());
        addMutantStrategy(new AuthorizationReplacementOperator(insideQuotes, sameSecurityIdentifier));
        addMutantStrategy(new SecurityIdentifierTypeReplacement(insideQuotes, diffSecurityIdentifier));
        addMutantStrategy(new InvalidSecurityIdentifierReplacement(insideQuotes));
        addMutantStrategy(new EmptyAuthorizationArgumentOperator());
        addMutantStrategy(new AuthorizationParameterReductionOperator(insideQuotes));

        return getMutantStrategies();
    }

}
