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
        assertTrue(mutants.stream().anyMatch(s -> s.equals("hasRole('NO_ADMIN')")));
        assertTrue(mutants.stream().anyMatch(s -> s.equals("!hasRole('ADMIN')")));
        assertTrue(mutants.stream().anyMatch(s -> s.equals("hasRole('USER')")));
        assertTrue(mutants.stream().anyMatch(s -> s.equals("hasRole('READ')")));
        assertTrue(mutants.stream().anyMatch(s -> s.equals("hasRole('WRITE')")));
        assertTrue(mutants.stream().anyMatch(s -> s.equals("hasAuthority('ADMIN')")));
    }

    @Test
    void shouldMutateSimpleAuthority() throws Exception {

        MutantMaker m = new MutantMaker(
                "hasAuthority('READ')", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertTrue(mutants.contains("hasRole('READ')"));
        assertTrue(mutants.contains("!hasAuthority('READ')"));
        assertTrue(mutants.contains("permitAll()"));
        assertTrue(mutants.contains("denyAll"));
        assertTrue(mutants.contains("hasAuthority('WRITE')"));
        assertTrue(mutants.contains("hasAuthority('ADMIN')"));
        assertTrue(mutants.contains("hasAuthority('USER')"));
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
        assertTrue(mutants.stream().anyMatch(s -> s.equals("denyAll")));

    }

    @Test
    void shouldMutateDenyAll() throws Exception {

        MutantMaker m = new MutantMaker(
                "denyAll", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertEquals(1, mutants.size());

        assertTrue(mutants.stream().anyMatch(s -> s.equals("permitAll()")));

    }

    @Test
    void shouldMutateHasPermissionWithMultipleArgs() throws Exception {
        MutantMaker m = new MutantMaker(
                "hasPermission(#id, 'DOC', 'READ')", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertTrue(mutants.contains("!hasPermission(#id, 'DOC', 'READ')"));

        assertTrue(mutants.contains("hasPermission(#id, 'MUTATED_DOC', 'READ')"));
        assertTrue(mutants.contains("hasPermission(#id, 'DOC', 'MUTATED_READ')"));

    }

    @Test
    void shouldMutateCustomHasPermission() throws Exception {
        MutantMaker m = new MutantMaker(
                "@cps.hasPermission('sys_dept_add')", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertTrue(mutants.contains("!@cps.hasPermission('sys_dept_add')"));

        assertTrue(mutants.contains("permitAll()"));
        assertTrue(mutants.contains("denyAll"));
        assertTrue(mutants.stream().anyMatch(s -> s.equals("@cps.hasPermission('MUTATED_sys_dept_add')")));
    }

    @Test
    void shouldMutateLogicalOperatorsWithOneOperator() throws Exception {
        MutantMaker m = new MutantMaker(
                "hasRole('ADMIN') and hasAuthority('READ')", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertTrue(mutants.contains("hasRole('ADMIN') or hasAuthority('READ')"));
        assertTrue(mutants.contains("!hasRole('ADMIN') and hasAuthority('READ')"));
        assertTrue(mutants.contains("hasRole('ADMIN') and !hasAuthority('READ')"));
    }

    @Test
    void shouldMutateLogicalOperatorsWithMultipleOperators() throws Exception {
        MutantMaker m = new MutantMaker(
                "hasRole('ADMIN') and hasAuthority('READ') or hasRole('USER')", roles, auths);

        List<String> mutants = m.genAllMutants();
        assertTrue(mutants.contains("hasRole('ADMIN') or hasAuthority('READ') or hasRole('USER')"));
        assertTrue(mutants.contains("hasRole('ADMIN') and hasAuthority('READ') and hasRole('USER')"));
    }

    @Test
    void shouldReturnEmptyMutationWhenUnknown() throws Exception {

        MutantMaker m = new MutantMaker(
                "someOtherExpression()", roles, auths);

        Exception exception = assertThrows(Exception.class, () -> {
            m.genAllMutants();
        });

        assertEquals("No recognizable pattern found in the input value.", exception.getMessage());
    }

}
