package com.leopaul29.bento.ordering.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Value-object semantics, pinned because the domain depends on them rather than merely having
 * them: {@link BentoId} is a {@code HashMap} key inside {@link ShopDay}, so an {@code equals}
 * that answered {@code true} too often would silently merge two bentos' stock, and {@link Money}
 * equality is what every total assertion in the suite rests on.
 *
 * <p>These exist because the PIT mutation run said so. Before them the score was 62%: every
 * mutant that flipped an {@code equals} or blanked a {@code toString} survived, which is the
 * mutation report's way of saying "nothing would notice".
 */
class ValueObjectsTest {

    @Test
    @DisplayName("money compares and prints by value")
    void moneyComparesAndPrintsByValue() {
        Money fiveHundred = Money.yen(500);

        assertThat(fiveHundred).isEqualTo(fiveHundred);
        assertThat(fiveHundred).isEqualTo(Money.yen(500));
        assertThat(fiveHundred).isNotEqualTo(Money.yen(501));
        assertThat(fiveHundred).isNotEqualTo("¥500");
        assertThat(fiveHundred).isNotEqualTo(null);
        assertThat(fiveHundred.hashCode()).isEqualTo(Money.yen(500).hashCode());
        assertThat(fiveHundred.hashCode()).isNotEqualTo(Money.yen(501).hashCode());
        assertThat(fiveHundred.yen()).isEqualTo(500L);
        assertThat(fiveHundred).hasToString("¥500");
        assertThat(Money.ZERO.yen()).isZero();
    }

    @Test
    @DisplayName("quantity compares and prints by value")
    void quantityComparesAndPrintsByValue() {
        Quantity four = Quantity.of(4);

        assertThat(four).isEqualTo(four).isEqualTo(Quantity.of(4)).isNotEqualTo(Quantity.of(5));
        assertThat(four).isNotEqualTo(4);
        assertThat(four).isNotEqualTo(null);
        assertThat(four.hashCode()).isEqualTo(Quantity.of(4).hashCode());
        assertThat(four.hashCode()).isNotEqualTo(Quantity.of(5).hashCode());
        assertThat(four.value()).isEqualTo(4);
        assertThat(four).hasToString("×4");
    }

    @Test
    @DisplayName("a bento id compares by value and works as a map key")
    void aBentoIdComparesByValueAndWorksAsAMapKey() {
        BentoId seven = BentoId.of(7);

        assertThat(seven).isEqualTo(seven).isEqualTo(BentoId.of(7)).isNotEqualTo(BentoId.of(8));
        assertThat(seven).isNotEqualTo(7L);
        assertThat(seven).isNotEqualTo(null);
        assertThat(seven.hashCode()).isEqualTo(BentoId.of(7).hashCode());
        assertThat(seven.hashCode()).isNotEqualTo(BentoId.of(8).hashCode());
        assertThat(seven.value()).isEqualTo(7L);
        assertThat(seven).hasToString("bento#7");

        // The property ShopDay actually relies on.
        Map<BentoId, Integer> stock = Map.of(BentoId.of(7), 3, BentoId.of(8), 1);
        assertThat(stock.get(BentoId.of(7))).isEqualTo(3);
        assertThat(stock.get(BentoId.of(8))).isEqualTo(1);
    }

    @Test
    @DisplayName("a customer id compares by value and rejects zero as well as negatives")
    void aCustomerIdComparesByValueAndRejectsZero() {
        CustomerId three = CustomerId.of(3);

        assertThat(three).isEqualTo(three).isEqualTo(CustomerId.of(3)).isNotEqualTo(CustomerId.of(4));
        assertThat(three).isNotEqualTo(3L);
        assertThat(three).isNotEqualTo(null);
        assertThat(three.hashCode()).isEqualTo(CustomerId.of(3).hashCode());
        assertThat(three.hashCode()).isNotEqualTo(CustomerId.of(4).hashCode());
        assertThat(three.value()).isEqualTo(3L);
        assertThat(three).hasToString("customer#3");

        // Zero, not just negatives — the boundary a surviving mutant exposed.
        assertThatThrownBy(() -> CustomerId.of(0)).isInstanceOf(DomainRuleViolation.class);
        assertThatThrownBy(() -> BentoId.of(0)).isInstanceOf(DomainRuleViolation.class);
    }

    @Test
    @DisplayName("an order id wraps a uuid and compares by it")
    void anOrderIdWrapsAUuidAndComparesByIt() {
        UUID uuid = UUID.fromString("0f2b5a66-5a1f-4b0e-9a1c-1f2d3c4b5a60");
        OrderId id = OrderId.of(uuid);

        assertThat(id).isEqualTo(id).isEqualTo(OrderId.of(uuid)).isNotEqualTo(OrderId.newId());
        assertThat(id).isNotEqualTo(uuid);
        assertThat(id).isNotEqualTo(null);
        assertThat(id.hashCode()).isEqualTo(OrderId.of(uuid).hashCode());
        assertThat(id.value()).isEqualTo(uuid);
        assertThat(id).hasToString(uuid.toString());
        assertThat(OrderId.newId()).isNotEqualTo(OrderId.newId());
    }

    @Test
    @DisplayName("an order line compares by its three parts and prints them")
    void anOrderLineComparesByItsThreePartsAndPrintsThem() {
        OrderLine line = new OrderLine(BentoId.of(1), Quantity.of(2), Money.yen(500));

        assertThat(line).isEqualTo(line);
        assertThat(line).isEqualTo(new OrderLine(BentoId.of(1), Quantity.of(2), Money.yen(500)));
        assertThat(line).isNotEqualTo(new OrderLine(BentoId.of(9), Quantity.of(2), Money.yen(500)));
        assertThat(line).isNotEqualTo(new OrderLine(BentoId.of(1), Quantity.of(3), Money.yen(500)));
        assertThat(line).isNotEqualTo(new OrderLine(BentoId.of(1), Quantity.of(2), Money.yen(600)));
        assertThat(line).isNotEqualTo("bento#1 ×2 @ ¥500");
        assertThat(line).isNotEqualTo(null);
        assertThat(line.hashCode())
                .isEqualTo(new OrderLine(BentoId.of(1), Quantity.of(2), Money.yen(500)).hashCode());

        assertThat(line.bentoId()).isEqualTo(BentoId.of(1));
        assertThat(line.quantity()).isEqualTo(Quantity.of(2));
        assertThat(line.unitPrice()).isEqualTo(Money.yen(500));
        assertThat(line.subtotal()).isEqualTo(Money.yen(1000));
        assertThat(line).hasToString("bento#1 ×2 @ ¥500");
    }

    @Test
    @DisplayName("a line refuses to exist without all three parts")
    void aLineRefusesToExistWithoutAllThreeParts() {
        assertThatThrownBy(() -> new OrderLine(null, Quantity.of(1), Money.yen(1)))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("bento");
        assertThatThrownBy(() -> new OrderLine(BentoId.of(1), null, Money.yen(1)))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("quantity");
        assertThatThrownBy(() -> new OrderLine(BentoId.of(1), Quantity.of(1), null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("unit price");
    }

    @Test
    @DisplayName("only COLLECTED and CANCELLED are terminal")
    void onlyCollectedAndCancelledAreTerminal() {
        assertThat(OrderStatus.COLLECTED.isTerminal()).isTrue();
        assertThat(OrderStatus.CANCELLED.isTerminal()).isTrue();

        // The half a surviving mutant proved was missing.
        assertThat(OrderStatus.DRAFT.isTerminal()).isFalse();
        assertThat(OrderStatus.PLACED.isTerminal()).isFalse();
        assertThat(OrderStatus.ACCEPTED.isTerminal()).isFalse();
        assertThat(OrderStatus.PREPARING.isTerminal()).isFalse();
        assertThat(OrderStatus.READY.isTerminal()).isFalse();
    }
}
