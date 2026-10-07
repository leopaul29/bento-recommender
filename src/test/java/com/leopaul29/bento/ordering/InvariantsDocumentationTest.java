package com.leopaul29.bento.ordering;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Keeps {@code INVARIANTS.md} honest in both directions.
 *
 * <p>A document that says which test pins which rule is worth exactly as much as the check that
 * it is still true. Asserting membership both ways — no row without a test, no test without a
 * row — closes the class of drift rather than one instance of it, so deleting a test or adding
 * one without documenting it both fail here.
 */
class InvariantsDocumentationTest {

    /** Matches `| I7 | … | `theMethodName` |`. */
    private static final Pattern ROW =
            Pattern.compile("^\\|\\s*(I\\d+)\\s*\\|.*\\|\\s*`([A-Za-z0-9_]+)`\\s*\\|\\s*$");

    /**
     * The packages an invariant can live in. Scoped rather than enumerated: a rule of the ordering
     * domain is defended by the domain or by a use case, never by an adapter, so
     * {@code ..ordering.infrastructure..} is out of scope — as are this test and
     * {@code ArchitectureTest}, which sit in the root package and test the documentation and the
     * structure rather than a numbered rule.
     */
    private static final List<String> INVARIANT_TEST_PACKAGES = List.of(
            "com.leopaul29.bento.ordering.domain",
            "com.leopaul29.bento.ordering.application");

    private record DocumentedInvariant(String id, String testMethod) {}

    private static List<DocumentedInvariant> readTable() throws IOException {
        Path file = Path.of("INVARIANTS.md");
        assertThat(file).as("INVARIANTS.md must sit at the repository root").exists();

        return Files.readAllLines(file).stream()
                .map(ROW::matcher)
                .filter(Matcher::matches)
                .map(m -> new DocumentedInvariant(m.group(1), m.group(2)))
                .toList();
    }

    private static Set<String> testMethodsThatCouldPinAnInvariant() {
        JavaClasses classes = new ClassFileImporter().importPackages(INVARIANT_TEST_PACKAGES);

        return classes.stream()
                .filter(c -> c.getSimpleName().endsWith("Test"))
                .flatMap(c -> c.getMethods().stream())
                .filter(m -> m.isAnnotatedWith(org.junit.jupiter.api.Test.class))
                .map(JavaMethod::getName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Test
    @DisplayName("every documented invariant names a test that exists")
    void everyDocumentedInvariantNamesATestThatExists() throws IOException {
        List<DocumentedInvariant> documented = readTable();
        Set<String> actual = testMethodsThatCouldPinAnInvariant();

        assertThat(documented).as("INVARIANTS.md has no rows — the regex or the table changed")
                .isNotEmpty();

        for (DocumentedInvariant invariant : documented) {
            assertThat(actual)
                    .as("%s is documented as pinned by %s(), which does not exist",
                            invariant.id(), invariant.testMethod())
                    .contains(invariant.testMethod());
        }
    }

    @Test
    @DisplayName("the invariant ids run I1..In with no gap and no duplicate")
    void theInvariantIdsRunWithNoGapAndNoDuplicate() throws IOException {
        List<String> ids = readTable().stream().map(DocumentedInvariant::id).toList();

        List<String> expected =
                java.util.stream.IntStream.rangeClosed(1, ids.size()).mapToObj(i -> "I" + i).toList();

        assertThat(ids).as("a row was renumbered, duplicated or dropped").isEqualTo(expected);
    }

    @Test
    @DisplayName("no domain or use-case test is undocumented")
    void noDomainOrUseCaseTestIsUndocumented() throws IOException {
        Set<String> documented =
                readTable().stream().map(DocumentedInvariant::testMethod).collect(Collectors.toSet());

        // Tests that deliberately cover something other than one of the ten numbered rules.
        Set<String> allowedExtras = Set.of(
                "theLineListCannotBeModifiedFromOutside",
                "eventsAreRaisedAndPulledOnce",
                "aPlacedOrderIsNoLongerEditable",
                "moneyIsWholeYenAndNeverNegative",
                "idsRejectNonPositiveValuesAndCompareByValue",
                "aShopDayAnswersWhatItOffersAndHowMuchIsLeft",
                "reservingTakesStockDown",
                "placingThenAcceptingTakesStockOnce",
                "cancellingAPlacedOrder",
                "theUseCasesRefuseWhatTheyCannotFind",
                // Value-object semantics the domain depends on, added because the PIT run
                // showed nothing would notice if equals/hashCode/toString broke.
                "moneyComparesAndPrintsByValue",
                "quantityComparesAndPrintsByValue",
                "aBentoIdComparesByValueAndWorksAsAMapKey",
                "aCustomerIdComparesByValueAndRejectsZero",
                "anOrderIdWrapsAUuidAndComparesByIt",
                "anOrderLineComparesByItsThreePartsAndPrintsThem",
                "aLineRefusesToExistWithoutAllThreeParts",
                "onlyCollectedAndCancelledAreTerminal",
                // The persistence seam added in Phase 2.
                "aRehydratedOrderIsTheOrderThatWasStored",
                "rehydratingRefusesANullStatus",
                "theStockSnapshotIsACopyACallerCannotChange");

        Set<String> undocumented = new TreeSet<>(testMethodsThatCouldPinAnInvariant());
        undocumented.removeAll(documented);
        undocumented.removeAll(allowedExtras);

        assertThat(undocumented)
                .as("these domain or use-case tests exist but no INVARIANTS.md row claims them "
                        + "— either add the row, or list the test as a deliberate extra here")
                .isEmpty();
    }
}
