package com.leopaul29.bento.ordering.domain;

import java.util.Objects;

/** A reference to the user who placed the order, by id, across a context boundary. */
public final class CustomerId {

    private final long value;

    private CustomerId(long value) {
        if (value <= 0) {
            throw new DomainRuleViolation("a customer id must be positive: " + value);
        }
        this.value = value;
    }

    public static CustomerId of(long value) {
        return new CustomerId(value);
    }

    public long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerId other)) return false;
        return value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "customer#" + value;
    }
}
