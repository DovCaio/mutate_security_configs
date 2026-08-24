package com.caio.engine.mutant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.caio.engine.mutant.detect_pattern.AllPredicatesCasePattern;
import com.caio.engine.mutant.detect_pattern.CompositeCasePattern;
import com.caio.engine.mutant.detect_pattern.DenyAllCase;
import com.caio.engine.mutant.detect_pattern.DetectPattern;
import com.caio.engine.mutant.detect_pattern.HasPermissionCustomPattern;
import com.caio.engine.mutant.detect_pattern.HasPermissionPattern;
import com.caio.engine.mutant.detect_pattern.LogicalCase;
import com.caio.engine.mutant.detect_pattern.PermitAllCase;
import com.caio.engine.mutant.detect_pattern.SimpleCasePattern;

public class MutantMaker {

    private String value;
    private List<String> rolesAndAuthorities;

    private List<DetectPattern> detectPatterns;

    public MutantMaker(String value, List<String> roles, List<String> authorities) {

        this.value = value;

        this.rolesAndAuthorities = new ArrayList<>();
        rolesAndAuthorities.addAll(roles);
        rolesAndAuthorities.addAll(authorities);

        List<String> sameSecurityIdentifier = new ArrayList<>();
        List<String> diffSecurityIdentifier = new ArrayList<>();

        if (value.contains("hasRole") || value.contains("hasAnyRole")) {
            sameSecurityIdentifier.addAll(roles);
            diffSecurityIdentifier.addAll(authorities);

        } else {
            sameSecurityIdentifier.addAll(authorities);
            diffSecurityIdentifier.addAll(roles);

        }

        detectPatterns.add(new SimpleCasePattern(value, sameSecurityIdentifier, diffSecurityIdentifier));
        detectPatterns.add(new CompositeCasePattern(value, sameSecurityIdentifier, diffSecurityIdentifier));
        detectPatterns.add(new PermitAllCase(value));
        detectPatterns.add(new DenyAllCase(value));
        detectPatterns.add(new LogicalCase(value));
        detectPatterns.add(new HasPermissionPattern(value));
        detectPatterns.add(new AllPredicatesCasePattern(value));
        detectPatterns.add(new HasPermissionCustomPattern(value));
    }

    public List<String> genAllMutants() throws Exception {
        List<String> result = new ArrayList<>();

        detectPatterns.forEach(pattern -> {
            pattern.execute().forEach(strategy -> result.addAll(strategy.make(value)));
            ;
        });

        if (hasHasPermissionCustom) {
            matcherHasPermissionCustomCase.reset();
            result.addAll(muteHasPermissionCustom(matcherHasPermissionCustomCase));
        }

        /*
         * boolean hasAnyPattern = hasSimple ||
         * hasCompost ||
         * hasPermitAll ||
         * hasDenyAll ||
         * hasHasPermission ||
         * hasHasPermissionCustom ||
         * hasLogicalOperators;
         * 
         * if (!hasAnyPattern) {
         * result.add(wildcardMutation(value));
         * 
         * }
         */

        return result.stream().distinct().toList();
    }

    private String wildcardMutation(String str) {
        return "!" + str;
    }

}
