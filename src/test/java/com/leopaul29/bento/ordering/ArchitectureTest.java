package com.leopaul29.bento.ordering;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * The boundary rules from {@code INVARIANTS.md}. These are not unit-testable properties of one
 * object, so they are asserted over the compiled classes instead — which is the only way they
 * stay true after the tenth person adds "just one" import.
 *
 * <p><strong>Known blind spot, found by probing these rules on purpose:</strong> ArchUnit reads
 * bytecode, and an <em>unused</em> Java import leaves no trace there. Adding
 * {@code import org.springframework.stereotype.Component;} to a domain class without using it
 * does <em>not</em> fail this test. That is correct — an unused import is not a dependency — but
 * a probe has to create a real reference (an annotation, a field, a call) to see these rules go
 * red. Do not conclude from a stray import that the gate is broken.
 */
class ArchitectureTest {

    private static JavaClasses productionClasses;

    @BeforeAll
    static void importClasses() {
        productionClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.leopaul29.bento");
    }

    @Test
    @DisplayName("the ordering domain knows no framework")
    void theOrderingDomainKnowsNoFramework() {
        noClasses()
                .that().resideInAPackage("..ordering.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta..",
                        "javax.persistence..",
                        "lombok..",
                        "org.hibernate..",
                        "com.fasterxml.jackson..")
                .because("the domain must compile and be testable without a framework; "
                        + "persistence and web concerns belong in infrastructure")
                .check(productionClasses);
    }

    @Test
    @DisplayName("the ordering application layer knows no framework either")
    void theOrderingApplicationLayerKnowsNoFrameworkEither() {
        noClasses()
                .that().resideInAPackage("..ordering.application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta..",
                        "lombok..",
                        "org.hibernate..")
                .because("wiring belongs in infrastructure, not in the use cases")
                .check(productionClasses);
    }

    @Test
    @DisplayName("the ordering application layer does not depend on infrastructure either")
    void theOrderingApplicationLayerDoesNotDependOnInfrastructureEither() {
        noClasses()
                .that().resideInAPackage("..ordering.application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..ordering.infrastructure..")
                .because("the use cases depend on the ports, and infrastructure implements them — "
                        + "an arrow the other way makes the fakes in the unit tests a fiction")
                .check(productionClasses);
    }

    @Test
    @DisplayName("the ordering domain does not depend on the rest of the application")
    void theOrderingDomainDoesNotDependOnTheRestOfTheApplication() {
        noClasses()
                .that().resideInAPackage("..ordering.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "com.leopaul29.bento.entities..",
                        "com.leopaul29.bento.repositories..",
                        "com.leopaul29.bento.services..",
                        "com.leopaul29.bento.controllers..",
                        "com.leopaul29.bento.dtos..",
                        "com.leopaul29.bento.mappers..",
                        "com.leopaul29.bento.security..",
                        "com.leopaul29.bento.config..",
                        "..ordering.application..",
                        "..ordering.infrastructure..")
                .because("the bento catalogue is a separate context, referenced by id through a "
                        + "port — and a domain that imports its own application layer has the "
                        + "dependency arrow backwards")
                .check(productionClasses);
    }
}
