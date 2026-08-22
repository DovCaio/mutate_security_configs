package com.caio.engine.mutant.mutants;

import java.util.regex.Matcher;

public class SecurityIdentifierTypeReplacement implements MutantStrategy { // faz exatamente a mesma coisa que o
                                                                           // AuthorizationReplacementOperator, fiz isso
                                                                           // para ficar mais de acordo com o artigo

    private String insideQuotes;
    private String roleOrAuthority;

    public SecurityIdentifierTypeReplacement(Matcher matcher, String roleOrAuthority) {
        this.insideQuotes = matcher.group(2);
        this.roleOrAuthority = roleOrAuthority;
    }

    @Override
    public String make(String value) {
        return value.replace(insideQuotes, roleOrAuthority);
    }
}
