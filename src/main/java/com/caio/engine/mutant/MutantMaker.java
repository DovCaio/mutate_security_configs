package com.caio.engine.mutant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.caio.engine.mutant.detect_pattern.CompositeCasePattern;
import com.caio.engine.mutant.detect_pattern.DenyAllCase;
import com.caio.engine.mutant.detect_pattern.DetectPattern;
import com.caio.engine.mutant.detect_pattern.HasPermissionPattern;
import com.caio.engine.mutant.detect_pattern.LogicalCase;
import com.caio.engine.mutant.detect_pattern.PermitAllCase;
import com.caio.engine.mutant.detect_pattern.SimpleCasePattern;

public class MutantMaker {

    private final String regexHasPermissionCustom = "@(\\w+)\\.hasPermission\\s*\\(\\s*([^)]*)\\)";

    private final String regexAllPredicates = "(!?)(hasRole\\([^)]*\\)|hasAuthority\\([^)]*\\)|hasAnyRole\\([^)]*\\)|hasAnyAuthority\\([^)]*\\)|hasPermission\\([^)]*\\)|@\\w+\\.hasPermission\\([^)]*\\))";

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
    }

    public List<String> genAllMutants() throws Exception {
        List<String> result = new ArrayList<>();

        detectPatterns.forEach(pattern -> {
            pattern.execute().forEach(strategy -> result.addAll(strategy.make(value)));
            ;
        });

        Pattern patternHasPermissionCustom = Pattern.compile(regexHasPermissionCustom);
        Pattern patternAllPredicates = Pattern.compile(regexAllPredicates);

        Matcher matcherHasPermissionCustomCase = patternHasPermissionCustom.matcher(this.value);
        Matcher matcherAllPredicatesCase = patternAllPredicates.matcher(this.value);

        boolean hasHasPermissionCustom = matcherHasPermissionCustomCase.find();
        boolean hasAllPredicates = matcherAllPredicatesCase.find();

        if (hasAllPredicates) {
            matcherAllPredicatesCase.reset();
            result.addAll(mutateNegation(matcherAllPredicatesCase));
        }

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

    private List<String> mutateNegation(Matcher matcher) {
        List<String> mutants = new ArrayList<>();

        while (matcher.find()) {
            String expr = matcher.group(0);

            if (!expr.startsWith("!")) {

                String mutant = value.substring(0, matcher.start()) +
                        "!" + expr +
                        value.substring(matcher.end());

                mutants.add(mutant);
            } else {
                String mutant = value.substring(0, matcher.start()) +
                        expr.substring(1) +
                        value.substring(matcher.end());

                mutants.add(mutant);
            }
        }

        return mutants;
    }

    private List<String> muteHasPermissionCustom(Matcher matcher) {

        List<String> mutants = new ArrayList<>();

        while (matcher.find()) {

            String beanName = matcher.group(1);
            String params = matcher.group(2);

            String mutatedParams = params.replaceAll("'([^']+)'", "'MUTATED_$1'");
            mutants.add(
                    value.substring(0, matcher.start()) +
                            "@" + beanName + ".hasPermission(" + mutatedParams + ")" +
                            value.substring(matcher.end()));

            mutants.add(
                    value.substring(0, matcher.start()) +
                            "@" + beanName + ".hasPermition(" + params + ")" +
                            value.substring(matcher.end()));
        }

        return mutants;
    }

    private String wildcardMutation(String str) {
        return "!" + str;
    }

}
