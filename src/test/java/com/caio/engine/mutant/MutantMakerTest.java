package com.caio.engine.mutant;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MutantMakerTest {

    private List<String> roles = List.of("ADMIN", "USER");
    private List<String> auths = List.of("READ", "WRITE");


    @Test
    void shouldMutateSimpleRole() throws Exception {

        MutantMaker m = new MutantMaker(
                "hasRole('ADMIN')", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertTrue(mutants.contains("permitAll()"));
        assertTrue(mutants.contains("denyAll"));
        assertTrue(mutants.stream().anyMatch(s -> s.contains("NO_ADMIN")));
        assertTrue(mutants.stream().anyMatch(s -> s.contains("hasAuthority")));
    }


    @Test
    void shouldMutateSimpleAuthority() throws Exception {

        MutantMaker m = new MutantMaker(
                "hasAuthority('READ')", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertTrue(mutants.stream().anyMatch(s -> s.contains("hasRole")));
    }


    @Test
    void shouldMutateCompositeRole() throws Exception {

        MutantMaker m = new MutantMaker(
                "hasAnyRole('ADMIN','USER')", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertTrue(mutants.stream().anyMatch(s -> s.contains("hasAnyAuthority")));
        assertTrue(mutants.stream().anyMatch(s -> s.contains("NO_")));
        assertTrue(mutants.size() > 3);
    }


    @Test
    void shouldMutateCompositeAuthority() throws Exception {

        MutantMaker m = new MutantMaker(
                "hasAnyAuthority('READ','WRITE')", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertTrue(mutants.stream().anyMatch(s -> s.contains("hasAnyRole")));
    }


    @Test
    void shouldMutatePermitAll() throws Exception {

        MutantMaker m = new MutantMaker(
                "permitAll()", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertEquals(1, mutants.size());
        assertEquals("denyAll", mutants.get(0));
    }


    @Test
    void shouldMutateDenyAll() throws Exception {

        MutantMaker m = new MutantMaker(
                "denyAll", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertEquals("permitAll()", mutants.get(0));
    }


    @Test
    void shouldReturnEmptyMutationWhenUnknown() throws Exception {

        MutantMaker m = new MutantMaker(
                "someOtherExpression()", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertTrue(mutants.contains(""));
    }

}
