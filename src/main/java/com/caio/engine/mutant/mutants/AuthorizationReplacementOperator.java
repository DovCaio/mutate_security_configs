package com.caio.engine.mutant.mutants;

import java.util.List;
import java.util.regex.Matcher;

public class AuthorizationReplacementOperator implements MutantStrategy {

    private String insideQuotes;
    private String roleOrAuthority;

    public AuthorizationReplacementOperator(Matcher matcher, String roleOrAuthority) {
        this.insideQuotes = matcher.group(2);
        this.roleOrAuthority = roleOrAuthority;
    }

    @Override
    public String make(String value) {
        return value.replace(insideQuotes, roleOrAuthority);
    }

}
