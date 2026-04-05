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


        assertEquals(9, mutants.size());
        assertTrue(mutants.contains("permitAll()"));
        assertTrue(mutants.contains("hasRole('')"));
        assertTrue(mutants.contains("denyAll"));
        assertTrue(mutants.contains("permitAll()"));
        assertTrue(mutants.contains("hasRole('NO_ADMIN')"));
        assertTrue(mutants.contains("!hasRole('ADMIN')"));
        assertTrue(mutants.contains("hasRole('USER')"));
        assertTrue(mutants.contains("hasRole('READ')"));
        assertTrue(mutants.contains("hasRole('WRITE')"));
        assertTrue(mutants.contains("hasAuthority('ADMIN')"));
    }

    @Test
    void shouldMutateSimpleAuthority() throws Exception {

        MutantMaker m = new MutantMaker(
                "hasAuthority('READ')", roles, auths);

        List<String> mutants = m.genAllMutants();

        assertEquals(9, mutants.size());
        assertTrue(mutants.contains("hasRole('READ')"));
        assertTrue(mutants.contains("!hasAuthority('READ')"));
        assertTrue(mutants.contains("permitAll()"));
        assertTrue(mutants.contains("denyAll"));
        assertTrue(mutants.contains("hasAuthority('WRITE')"));
        assertTrue(mutants.contains("hasAuthority('ADMIN')"));
        assertTrue(mutants.contains("hasAuthority('USER')"));
        assertTrue(mutants.contains("hasAuthority('')"));
    }

    @Test
    void shouldMutateCompositeRole() throws Exception {

        MutantMaker m = new MutantMaker(
                "hasAnyRole('ADMIN','USER')", roles, auths);

        List<String> mutants = m.genAllMutants();


        assertEquals(mutants.size(), 11);
        assertTrue(mutants.contains("permitAll()"));
        assertTrue(mutants.contains("denyAll"));
        assertTrue(mutants.contains("hasAnyAuthority('ADMIN','USER')"));
        assertTrue(mutants.contains("hasAnyRole('ADMIN','USER', 'READ')"));
        assertTrue(mutants.contains("hasAnyRole('ADMIN','USER', 'WRITE')"));
        assertTrue(mutants.contains("hasAnyRole('NO_ADMIN', 'USER')"));
        assertTrue(mutants.contains("hasAnyRole('ADMIN', 'NO_USER')"));
        assertTrue(mutants.contains("hasAnyRole('')"));
        assertTrue(mutants.contains("hasAnyRole('USER')"));
        assertTrue(mutants.contains("hasAnyRole('ADMIN')"));

    }

    @Test
    void shouldMutateCompositeAuthority() throws Exception {

        MutantMaker m = new MutantMaker(
                "hasAnyAuthority('READ','WRITE')", roles, auths);

        List<String> mutants = m.genAllMutants();

        mutants.forEach(System.out::println);

        assertEquals(mutants.size(), 11);
        assertTrue(mutants.contains("hasAnyRole('READ','WRITE')"));
        assertTrue(mutants.contains("!hasAnyAuthority('READ','WRITE')"));
        assertTrue(mutants.contains("hasAnyAuthority('NO_READ', 'WRITE')"));
        assertTrue(mutants.contains("hasAnyAuthority('READ', 'NO_WRITE')"));
        assertTrue(mutants.contains("permitAll()"));
        assertTrue(mutants.contains("denyAll"));
        assertTrue(mutants.contains("hasAnyAuthority('')"));
        assertTrue(mutants.contains("hasAnyAuthority('READ')"));
        assertTrue(mutants.contains("hasAnyAuthority('WRITE')"));
        assertTrue(mutants.contains("hasAnyAuthority('READ','WRITE', 'ADMIN')"));
        assertTrue(mutants.contains("hasAnyAuthority('READ','WRITE', 'USER')"));

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
