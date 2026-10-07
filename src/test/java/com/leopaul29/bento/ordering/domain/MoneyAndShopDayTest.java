package com.leopaul29.bento.ordering.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** The value-object and ShopDay rules that are not one of the ten numbered invariants. */
class MoneyAndShopDayTest {

    private static final LocalDate DATE = LocalDate.of(2026, 10, 9);
    private static final Instant CUTOFF = Instant.parse("2026-10-09T01:00:00Z");

    @Test
    @DisplayName("money is whole yen, never negative, and adds and multiplies")
    void moneyIsWholeYenAndNeverNegative() {
        assertThatThrownBy(() -> Money.yen(-1)).isInstanceOf(DomainRuleViolation.class);
        assertThatThrownBy(() -> Money.yen(100).times(-2)).isInstanceOf(DomainRuleViolation.class);

        assertThat(Money.yen(500).plus(Money.yen(780))).isEqualTo(Money.yen(1280));
        assertThat(Money.yen(500).times(3)).isEqualTo(Money.yen(1500));
        assertThat(Money.yen(500).times(0)).isEqualTo(Money.ZERO);
        assertThat(Money.yen(500)).hasToString("¥500");
    }

    @Test
    @DisplayName("ids reject non-positive values and compare by value")
    void idsRejectNonPositiveValuesAndCompareByValue() {
        assertThatThrownBy(() -> BentoId.of(0)).isInstanceOf(DomainRuleViolation.class);
        assertThatThrownBy(() -> CustomerId.of(-5)).isInstanceOf(DomainRuleViolation.class);

        assertThat(BentoId.of(7)).isEqualTo(BentoId.of(7)).isNotEqualTo(BentoId.of(8));
        assertThat(BentoId.of(7).hashCode()).isEqualTo(BentoId.of(7).hashCode());
        assertThat(CustomerId.of(3)).isEqualTo(CustomerId.of(3));
        assertThat(Quantity.of(4)).isEqualTo(Quantity.of(4)).isNotEqualTo(Quantity.of(5));
    }

    @Test
    @DisplayName("a shop day answers what it offers and how much is left")
    void aShopDayAnswersWhatItOffersAndHowMuchIsLeft() {
        ShopDay day = ShopDay.of(DATE, CUTOFF, Map.of(BentoId.of(1L), 5, BentoId.of(2L), 0));

        assertThat(day.offers(BentoId.of(1L))).isTrue();
        assertThat(day.offers(BentoId.of(99L))).isFalse();
        assertThat(day.remaining(BentoId.of(1L))).isEqualTo(5);
        assertThat(day.remaining(BentoId.of(2L))).isZero();
        assertThat(day.date()).isEqualTo(DATE);
        assertThat(day.orderingClosesAt()).isEqualTo(CUTOFF);

        assertThatThrownBy(() -> day.remaining(BentoId.of(99L)))
                .isInstanceOf(DomainRuleViolation.class)
                .hasMessageContaining("not on the menu");
        assertThatThrownBy(() -> ShopDay.of(DATE, CUTOFF, Map.of(BentoId.of(1L), -1)))
                .isInstanceOf(DomainRuleViolation.class);
    }

    @Test
    @DisplayName("the stock snapshot is a copy a caller cannot use to change the stock")
    void theStockSnapshotIsACopyACallerCannotChange() {
        ShopDay day = ShopDay.of(DATE, CUTOFF, Map.of(BentoId.of(1L), 5, BentoId.of(2L), 0));

        assertThat(day.stockSnapshot())
                .containsExactlyInAnyOrderEntriesOf(Map.of(BentoId.of(1L), 5, BentoId.of(2L), 0));

        assertThatThrownBy(() -> day.stockSnapshot().put(BentoId.of(1L), 999))
                .isInstanceOf(UnsupportedOperationException.class);

        day.reserve(BentoId.of(1L), Quantity.of(2));
        assertThat(day.stockSnapshot().get(BentoId.of(1L))).isEqualTo(3);
    }

    @Test
    @DisplayName("reserving takes stock down, and a reserve for an unknown bento is refused")
    void reservingTakesStockDown() {
        ShopDay day = ShopDay.of(DATE, CUTOFF, Map.of(BentoId.of(1L), 5));

        day.reserve(BentoId.of(1L), Quantity.of(2));
        assertThat(day.remaining(BentoId.of(1L))).isEqualTo(3);

        day.reserve(BentoId.of(1L), Quantity.of(3));
        assertThat(day.remaining(BentoId.of(1L))).isZero();

        assertThatThrownBy(() -> day.reserve(BentoId.of(99L), Quantity.of(1)))
                .isInstanceOf(DomainRuleViolation.class);
    }
}
