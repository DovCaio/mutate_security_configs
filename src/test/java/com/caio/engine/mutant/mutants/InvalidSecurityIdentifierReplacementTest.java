package com.caio.engine.mutant.mutants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.exceptions.InvalidClassInitiation;
import com.caio.exceptions.InvalidOperator;

class InvalidSecurityIdentifierReplacementTest {

        @Test
        void shouldMutateEachSecurityIdentifier() throws InvalidClassInitiation {

                InvalidSecurityIdentifierReplacement operator = new InvalidSecurityIdentifierReplacement(
                                "'ADMIN', 'STAFF'");

                List<String> mutants = operator.make("hasAnyRole('ADMIN', 'STAFF')");

                assertEquals(
                                List.of(
                                                "hasAnyRole('NO_ADMIN', 'STAFF')",
                                                "hasAnyRole('ADMIN', 'NO_STAFF')"),
                                mutants);
        }

        @Test
        void shouldRestoreSecurityIdentifierWhenAlreadyInvalid()
                        throws InvalidClassInitiation {

                InvalidSecurityIdentifierReplacement operator = new InvalidSecurityIdentifierReplacement(
                                "'NO_ADMIN'");

                List<String> mutants = operator.make("hasRole('NO_ADMIN')");

                assertEquals(
                                List.of("hasRole('ADMIN')"),
                                mutants);
        }

        @Test
        void shouldMutatePermissionIdentifier()
                        throws InvalidClassInitiation {

                InvalidSecurityIdentifierReplacement operator = new InvalidSecurityIdentifierReplacement(
                                "'user', 'read', 'ADMIN'");

                List<String> mutants = operator.make(
                                "hasPermission('user', 'read', 'ADMIN')");

                assertEquals(
                                List.of(
                                                "hasPermission('user', 'read', 'NO_ADMIN')"),
                                mutants);
        }

        @Test
        void shouldRejectInvalidHasPermission()
                        throws InvalidClassInitiation {

                InvalidSecurityIdentifierReplacement operator = new InvalidSecurityIdentifierReplacement(
                                "'user', 'read'");

                assertThrows(
                                InvalidOperator.class,
                                () -> operator.make(
                                                "hasPermission('user', 'read')"));
        }

        @Test
        void shouldMutateCustomHasPermission()
                        throws InvalidClassInitiation {

                InvalidSecurityIdentifierReplacement operator = new InvalidSecurityIdentifierReplacement(
                                "'user', 'read'",
                                "auth");

                List<String> mutants = operator.make(
                                "@auth.hasPermission('user', 'read')");

                assertEquals(
                                List.of(
                                                "@auth.hasPermission('MUTATED_user', 'MUTATED_read')"),
                                mutants);
        }

        @Test
        void shouldRejectNullInsideQuotes()
                        throws InvalidClassInitiation {

                assertThrows(
                                InvalidClassInitiation.class,
                                () -> new InvalidSecurityIdentifierReplacement(null));
        }

        @Test
        void shouldRejectNullInsideQuotesForCustomPermission() {

                assertThrows(
                                InvalidClassInitiation.class,
                                () -> new InvalidSecurityIdentifierReplacement(
                                                null,
                                                "auth"));
        }

        @Test
        void shouldRejectNullBeanName() {

                assertThrows(
                                InvalidClassInitiation.class,
                                () -> new InvalidSecurityIdentifierReplacement(
                                                "'user', 'read'",
                                                null));
        }
}