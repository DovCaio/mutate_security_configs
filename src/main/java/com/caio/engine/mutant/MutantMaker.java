package com.caio.engine.mutant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.caio.engine.mutant.detect_pattern.CompositeCasePattern;
import com.caio.engine.mutant.detect_pattern.DenyAllCase;
import com.caio.engine.mutant.detect_pattern.DetectPattern;
import com.caio.engine.mutant.detect_pattern.LogicalCase;
import com.caio.engine.mutant.detect_pattern.PermitAllCase;
import com.caio.engine.mutant.detect_pattern.SimpleCasePattern;

public class MutantMaker {

    private final String regexHasPermission = "(?<!\\.)hasPermission\\(([^)]*)\\)";
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
    }

    public List<String> genAllMutants() throws Exception {
        List<String> result = new ArrayList<>();

        detectPatterns.forEach(pattern -> {
            pattern.execute().forEach(strategy -> result.addAll(strategy.make(value)));
            ;
        });

        Pattern patternHasPermission = Pattern.compile(regexHasPermission);
        Pattern patternHasPermissionCustom = Pattern.compile(regexHasPermissionCustom);
        Pattern patternAllPredicates = Pattern.compile(regexAllPredicates);

        Matcher matcherHasPermissionCase = patternHasPermission.matcher(this.value);
        Matcher matcherHasPermissionCustomCase = patternHasPermissionCustom.matcher(this.value);
        Matcher matcherAllPredicatesCase = patternAllPredicates.matcher(this.value);

        boolean hasHasPermission = matcherHasPermissionCase.find();
        boolean hasHasPermissionCustom = matcherHasPermissionCustomCase.find();
        boolean hasLogicalOperators = matcherLogicalOperatorsCase.find();
        boolean hasAllPredicates = matcherAllPredicatesCase.find();

        if (hasAllPredicates) {
            matcherAllPredicatesCase.reset();
            result.addAll(mutateNegation(matcherAllPredicatesCase));
        }

        if (hasLogicalOperators) {
            matcherLogicalOperatorsCase.reset();
            result.addAll(mutateLogicalOperators(matcherLogicalOperatorsCase));
        }

        if (hasHasPermissionCustom) {
            matcherHasPermissionCustomCase.reset();
            result.addAll(muteHasPermissionCustom(matcherHasPermissionCustomCase));
        }
        if (hasHasPermission) {

            matcherHasPermissionCase.reset();
            result.addAll(muteHasPermission(matcherHasPermissionCase));
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

    private List<String> mutateLogicalOperators(Matcher matcher) {
        List<String> mutateOperators = new ArrayList<>();

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String operator = matcher.group(1);
            String mutatedOperator = operator.equals("and") ? "or" : "and";

            String mutant = value.substring(0, start) +
                    mutatedOperator +
                    value.substring(end);

            mutateOperators.add(mutant);
        }

        return mutateOperators;
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

    private List<String> muteHasPermission(Matcher matcher) {

        List<String> mutants = new ArrayList<>();

        while (matcher.find()) { // 🔥 ESSENCIAL

            String args = matcher.group(1);

            String[] parts = args.split("\\s*,\\s*");

            for (int i = 0; i < parts.length; i++) {

                if (parts[i].startsWith("'") && parts[i].endsWith("'")) {

                    String original = parts[i];
                    String innerValue = original.substring(1, original.length() - 1);

                    parts[i] = "'MUTATED_" + innerValue + "'";

                    String newArgs = String.join(", ", parts);

                    mutants.add(
                            value.substring(0, matcher.start()) +
                                    "hasPermission(" + newArgs + ")" +
                                    value.substring(matcher.end()));

                    parts[i] = original;
                }
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
