package com.leopaul29.bento.ordering.domain;

import java.util.Objects;

/**
 * How many of one bento a line orders. Bounded because a lunch shop making them by hand
 * cannot honour an order of 500 (I2) — the upper bound is a domain rule, not a guard against
 * integer overflow.
 */
public final class Quantity {

    public static final int MIN = 1;
    public static final int MAX = 20;

    private final int value;

    private Quantity(int value) {
        if (value < MIN || value > MAX) {
            throw new DomainRuleViolation(
                    "a line quantity must be between " + MIN + " and " + MAX + ", not " + value);
        }
        this.value = value;
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity other)) return false;
        return value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "×" + value;
    }
}
