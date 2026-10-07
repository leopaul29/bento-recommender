package com.leopaul29.bento.ordering.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * One day of service: what the shop offers, how many of each are left, and when ordering closes.
 *
 * <p>A second aggregate root, not part of {@link Order}. Stock is this object's invariant (I10)
 * and the menu of the day is its answer to give (I6); an order that owned either would let two
 * concurrent orders each believe they took the last bento. Coordinating the two is the job of
 * a use case, which is exactly the lesson.
 */
public final class ShopDay {

    private final LocalDate date;
    private final Instant orderingClosesAt;
    private final Map<BentoId, Integer> remaining = new HashMap<>();

    private ShopDay(LocalDate date, Instant orderingClosesAt, Map<BentoId, Integer> stock) {
        this.date = Objects.requireNonNull(date, "a shop day needs a date");
        this.orderingClosesAt = Objects.requireNonNull(orderingClosesAt, "a shop day needs a cutoff");
        stock.forEach((bentoId, count) -> {
            if (count < 0) {
                throw new DomainRuleViolation("stock cannot start negative for " + bentoId);
            }
            this.remaining.put(bentoId, count);
        });
    }

    public static ShopDay of(LocalDate date, Instant orderingClosesAt, Map<BentoId, Integer> stock) {
        return new ShopDay(date, orderingClosesAt, stock);
    }

    /** Whether the bento is on this day's menu at all (I6). */
    public boolean offers(BentoId bentoId) {
        return remaining.containsKey(bentoId);
    }

    public int remaining(BentoId bentoId) {
        Integer count = remaining.get(bentoId);
        if (count == null) {
            throw new DomainRuleViolation(bentoId + " is not on the menu for " + date);
        }
        return count;
    }

    /**
     * Takes stock for an accepted order.
     *
     * @throws DomainRuleViolation if the bento is not offered, or fewer than {@code quantity}
     *     remain — the count can never go negative (I10)
     */
    public void reserve(BentoId bentoId, Quantity quantity) {
        int left = remaining(bentoId);
        if (quantity.value() > left) {
            throw new DomainRuleViolation(
                    "only " + left + " of " + bentoId + " left on " + date
                            + ", cannot reserve " + quantity.value());
        }
        remaining.put(bentoId, left - quantity.value());
    }

    /**
     * The remaining stock, as an unmodifiable copy, for a persistence adapter to write.
     *
     * <p>The same seam as {@code Order.LineSnapshot}: the aggregate describes its own state rather
     * than letting storage reach into it. Returning the live map would hand any caller the ability
     * to set stock to anything, which is the one thing {@link #reserve} exists to prevent.
     */
    public Map<BentoId, Integer> stockSnapshot() {
        return Map.copyOf(remaining);
    }

    public LocalDate date() {
        return date;
    }

    public Instant orderingClosesAt() {
        return orderingClosesAt;
    }
}
