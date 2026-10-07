package com.leopaul29.bento.ordering.domain;

import com.leopaul29.bento.ordering.domain.events.OrderCancelled;
import com.leopaul29.bento.ordering.domain.events.OrderPlaced;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The invariants an {@link Order} defends on its own. One test per rule, named exactly as
 * {@code INVARIANTS.md} names it — see {@code InvariantsDocumentationTest}.
 */
class OrderTest {

    private static final LocalDate SERVICE_DATE = LocalDate.of(2026, 10, 9);
    private static final Instant CUTOFF = Instant.parse("2026-10-09T01:00:00Z"); // 10:00 JST
    private static final Instant BEFORE_CUTOFF = Instant.parse("2026-10-09T00:30:00Z");

    private static Order draft() {
        return Order.draft(OrderId.newId(), CustomerId.of(1L), SERVICE_DATE);
    }

    private static Order placed() {
        Order order = draft();
        order.addLine(BentoId.of(1L), Quantity.of(1), Money.yen(500));
        order.place(BEFORE_CUTOFF, CUTOFF);
        return order;
    }

    @Test
    @DisplayName("I1 — an order with no line cannot be placed")
    void placingAnEmptyOrderIsRefused() {
        Order order = draft();

        assertThatThrownBy(() -> order.place(BEFORE_CUTOFF, CUTOFF))
                .isInstanceOf(DomainRuleViolation.class)
                .hasMessageContaining("at least one line");

        assertThat(order.status()).isEqualTo(OrderStatus.DRAFT);
    }

    @Test
    @DisplayName("I2 — a line quantity outside 1..20 is refused")
    void aQuantityOutsideOneToTwentyIsRefused() {
        assertThatThrownBy(() -> Quantity.of(0)).isInstanceOf(DomainRuleViolation.class);
        assertThatThrownBy(() -> Quantity.of(21)).isInstanceOf(DomainRuleViolation.class);
        assertThatThrownBy(() -> Quantity.of(-1)).isInstanceOf(DomainRuleViolation.class);

        assertThat(Quantity.of(1).value()).isEqualTo(1);
        assertThat(Quantity.of(20).value()).isEqualTo(20);
    }

    @Test
    @DisplayName("I4 — the total is the sum of unit price × quantity over every line")
    void theTotalIsTheSumOfItsLines() {
        Order order = draft();
        order.addLine(BentoId.of(1L), Quantity.of(2), Money.yen(500));
        order.addLine(BentoId.of(2L), Quantity.of(3), Money.yen(780));

        assertThat(order.total()).isEqualTo(Money.yen(2 * 500 + 3 * 780));
        assertThat(draft().total()).isEqualTo(Money.ZERO);
    }

    @Test
    @DisplayName("I5 — placing at or after the cutoff is refused")
    void placingAtOrAfterTheCutoffIsRefused() {
        Order atCutoff = draft();
        atCutoff.addLine(BentoId.of(1L), Quantity.of(1), Money.yen(500));

        assertThatThrownBy(() -> atCutoff.place(CUTOFF, CUTOFF))
                .isInstanceOf(DomainRuleViolation.class)
                .hasMessageContaining("cutoff");

        Order after = draft();
        after.addLine(BentoId.of(1L), Quantity.of(1), Money.yen(500));
        assertThatThrownBy(() -> after.place(CUTOFF.plusSeconds(1), CUTOFF))
                .isInstanceOf(DomainRuleViolation.class);

        Order before = draft();
        before.addLine(BentoId.of(1L), Quantity.of(1), Money.yen(500));
        before.place(CUTOFF.minusSeconds(1), CUTOFF);
        assertThat(before.status()).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    @DisplayName("I7 — no lifecycle step may be skipped")
    void aLifecycleStepCannotBeSkipped() {
        Order order = placed();

        // PLACED cannot jump straight to PREPARING, READY or COLLECTED.
        assertThatThrownBy(order::startPreparing).isInstanceOf(IllegalOrderTransition.class);
        assertThatThrownBy(order::markReady).isInstanceOf(IllegalOrderTransition.class);
        assertThatThrownBy(order::collect).isInstanceOf(IllegalOrderTransition.class);

        // The full happy path, one step at a time.
        order.accept(BEFORE_CUTOFF);
        assertThatThrownBy(order::markReady).isInstanceOf(IllegalOrderTransition.class);
        order.startPreparing();
        assertThatThrownBy(order::collect).isInstanceOf(IllegalOrderTransition.class);
        order.markReady();
        order.collect();

        assertThat(order.status()).isEqualTo(OrderStatus.COLLECTED);
    }

    @Test
    @DisplayName("I8 — cancelling is refused once preparation has started")
    void cancellingAfterPreparationHasStartedIsRefused() {
        Order fromPlaced = placed();
        fromPlaced.cancel(BEFORE_CUTOFF);
        assertThat(fromPlaced.status()).isEqualTo(OrderStatus.CANCELLED);

        Order fromAccepted = placed();
        fromAccepted.accept(BEFORE_CUTOFF);
        fromAccepted.cancel(BEFORE_CUTOFF);
        assertThat(fromAccepted.status()).isEqualTo(OrderStatus.CANCELLED);

        Order preparing = placed();
        preparing.accept(BEFORE_CUTOFF);
        preparing.startPreparing();
        assertThatThrownBy(() -> preparing.cancel(BEFORE_CUTOFF))
                .isInstanceOf(IllegalOrderTransition.class);
        assertThat(preparing.status()).isEqualTo(OrderStatus.PREPARING);

        Order ready = placed();
        ready.accept(BEFORE_CUTOFF);
        ready.startPreparing();
        ready.markReady();
        assertThatThrownBy(() -> ready.cancel(BEFORE_CUTOFF))
                .isInstanceOf(IllegalOrderTransition.class);
    }

    @Test
    @DisplayName("I9 — a collected order refuses every further change")
    void aCollectedOrderRefusesEveryFurtherChange() {
        Order order = placed();
        order.accept(BEFORE_CUTOFF);
        order.startPreparing();
        order.markReady();
        order.collect();

        assertThat(order.status().isTerminal()).isTrue();
        assertThatThrownBy(() -> order.cancel(BEFORE_CUTOFF))
                .isInstanceOf(IllegalOrderTransition.class);
        assertThatThrownBy(order::collect).isInstanceOf(IllegalOrderTransition.class);
        assertThatThrownBy(() -> order.addLine(BentoId.of(9L), Quantity.of(1), Money.yen(100)))
                .isInstanceOf(DomainRuleViolation.class)
                .hasMessageContaining("COLLECTED");

        assertThat(order.lines()).hasSize(1);
    }

    @Test
    @DisplayName("lines() hands out no way to change the order")
    void theLineListCannotBeModifiedFromOutside() {
        Order order = placed();

        assertThatThrownBy(() -> order.lines().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("placing and cancelling raise events, pulled once")
    void eventsAreRaisedAndPulledOnce() {
        Order order = placed();

        assertThat(order.pullEvents())
                .singleElement()
                .isInstanceOfSatisfying(OrderPlaced.class,
                        e -> assertThat(e.total()).isEqualTo(Money.yen(500)));
        assertThat(order.pullEvents()).isEmpty();

        order.cancel(BEFORE_CUTOFF);
        assertThat(order.pullEvents())
                .singleElement()
                .isInstanceOfSatisfying(OrderCancelled.class,
                        e -> assertThat(e.cancelledFrom()).isEqualTo(OrderStatus.PLACED));
    }

    @Test
    @DisplayName("a draft cannot be placed twice, and a placed order takes no new line")
    void aPlacedOrderIsNoLongerEditable() {
        Order order = placed();

        // "cannot place", not just "PLACED": with the requireDraft() guard deleted, the
        // transition check would still throw IllegalOrderTransition with a message containing
        // "PLACED", and this assertion would pass against broken code. A surviving mutant said so.
        assertThatThrownBy(() -> order.place(BEFORE_CUTOFF, CUTOFF))
                .isInstanceOf(DomainRuleViolation.class)
                .isNotInstanceOf(IllegalOrderTransition.class)
                .hasMessageContaining("cannot place an order that is PLACED");
        assertThatThrownBy(() -> order.addLine(BentoId.of(2L), Quantity.of(1), Money.yen(100)))
                .isInstanceOf(DomainRuleViolation.class)
                .hasMessageContaining("cannot add a line to an order that is PLACED");
        assertThat(order.customerId()).isEqualTo(CustomerId.of(1L));
        assertThat(order.serviceDate()).isEqualTo(SERVICE_DATE);
        assertThat(order.id()).isNotNull();
    }
}
