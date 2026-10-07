package com.leopaul29.bento.ordering.domain;

import com.leopaul29.bento.ordering.domain.events.DomainEvent;
import com.leopaul29.bento.ordering.domain.events.OrderAccepted;
import com.leopaul29.bento.ordering.domain.events.OrderCancelled;
import com.leopaul29.bento.ordering.domain.events.OrderPlaced;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The ordering aggregate root.
 *
 * <p>Every rule this class defends is listed in {@code INVARIANTS.md} with the name of the test
 * that pins it. There are no setters: the only way to change an order is to ask it to do
 * something it has a word for, and it may refuse.
 *
 * <p>{@link OrderLine} lives entirely inside this boundary — nothing outside can build one or
 * reach a mutable view of the list. The bento and the customer are referenced by id, because
 * they belong to other contexts.
 */
public final class Order {

    private final OrderId id;
    private final CustomerId customerId;
    private final LocalDate serviceDate;
    private final List<OrderLine> lines = new ArrayList<>();
    private final List<DomainEvent> events = new ArrayList<>();

    private OrderStatus status = OrderStatus.DRAFT;

    private Order(OrderId id, CustomerId customerId, LocalDate serviceDate) {
        this.id = Objects.requireNonNull(id, "an order needs an id");
        this.customerId = Objects.requireNonNull(customerId, "an order needs a customer");
        this.serviceDate = Objects.requireNonNull(serviceDate, "an order needs a service date");
    }

    public static Order draft(OrderId id, CustomerId customerId, LocalDate serviceDate) {
        return new Order(id, customerId, serviceDate);
    }

    /**
     * Adds a line, capturing the unit price as it is now (I3).
     *
     * <p>Only while the order is a draft: once it is placed the shop has committed to it, and
     * after collection nothing about it may change at all (I9).
     */
    public void addLine(BentoId bentoId, Quantity quantity, Money unitPrice) {
        requireDraft("add a line to");
        lines.add(new OrderLine(bentoId, quantity, unitPrice));
    }

    /**
     * Places the order: the point at which it stops being editable and becomes a promise.
     *
     * @throws DomainRuleViolation if the order has no lines (I1) or the cutoff has passed (I5)
     */
    public void place(Instant now, Instant cutoff) {
        requireDraft("place");
        if (lines.isEmpty()) {
            throw new DomainRuleViolation("an order must have at least one line to be placed");
        }
        if (!now.isBefore(cutoff)) {
            throw new DomainRuleViolation(
                    "the cutoff for " + serviceDate + " passed at " + cutoff + " (now " + now + ")");
        }
        transitionTo(OrderStatus.PLACED);
        events.add(new OrderPlaced(id, total(), now));
    }

    public void accept(Instant now) {
        transitionTo(OrderStatus.ACCEPTED);
        events.add(new OrderAccepted(id, now));
    }

    public void startPreparing() {
        transitionTo(OrderStatus.PREPARING);
    }

    public void markReady() {
        transitionTo(OrderStatus.READY);
    }

    public void collect() {
        transitionTo(OrderStatus.COLLECTED);
    }

    /**
     * Cancels the order. Reachable only from {@code PLACED} or {@code ACCEPTED} (I8) — once
     * preparation has started the food exists and the cost is sunk.
     */
    public void cancel(Instant now) {
        OrderStatus from = status;
        transitionTo(OrderStatus.CANCELLED);
        events.add(new OrderCancelled(id, from, now));
    }

    /** The sum of the lines, at the prices captured when they were added (I4). */
    public Money total() {
        Money total = Money.ZERO;
        for (OrderLine line : lines) {
            total = total.plus(line.subtotal());
        }
        return total;
    }

    /** Hands the raised events to the caller and forgets them, so they are published once. */
    public List<DomainEvent> pullEvents() {
        List<DomainEvent> pulled = List.copyOf(events);
        events.clear();
        return pulled;
    }

    public List<OrderLine> lines() {
        return Collections.unmodifiableList(lines);
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public LocalDate serviceDate() {
        return serviceDate;
    }

    public OrderStatus status() {
        return status;
    }

    private void requireDraft(String what) {
        if (status != OrderStatus.DRAFT) {
            throw new DomainRuleViolation("cannot " + what + " an order that is " + status);
        }
    }

    private void transitionTo(OrderStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new IllegalOrderTransition(status, next);
        }
        status = next;
    }
}
