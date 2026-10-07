package com.leopaul29.bento.ordering.domain;

import java.util.Objects;

/**
 * A reference to a bento in the catalogue context.
 *
 * <p>An id, never the {@code Bento} entity: the catalogue is a different context with its own
 * lifecycle, and an order that held the entity would be invalidated — or silently changed — by
 * every edit to the menu. This is what makes I3 structurally possible.
 */
public final class BentoId {

    private final long value;

    private BentoId(long value) {
        if (value <= 0) {
            throw new DomainRuleViolation("a bento id must be positive: " + value);
        }
        this.value = value;
    }

    public static BentoId of(long value) {
        return new BentoId(value);
    }

    public long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BentoId other)) return false;
        return value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "bento#" + value;
    }
}
