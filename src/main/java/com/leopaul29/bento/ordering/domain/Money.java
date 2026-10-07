package com.leopaul29.bento.ordering.domain;

import java.util.Objects;

/**
 * An amount in Japanese yen.
 *
 * <p>Single-currency on purpose: yen has no minor unit, so the amount is a whole number and
 * there is no rounding to get wrong. It also means "every line of an order is in the same
 * currency" (I4) is true by construction rather than by a check someone has to remember.
 */
public final class Money {

    public static final Money ZERO = new Money(0L);

    private final long yen;

    private Money(long yen) {
        if (yen < 0) {
            throw new DomainRuleViolation("an amount cannot be negative: " + yen);
        }
        this.yen = yen;
    }

    public static Money yen(long amount) {
        return new Money(amount);
    }

    public Money plus(Money other) {
        return new Money(this.yen + other.yen);
    }

    public Money times(int factor) {
        if (factor < 0) {
            throw new DomainRuleViolation("an amount cannot be multiplied by " + factor);
        }
        return new Money(this.yen * factor);
    }

    public long yen() {
        return yen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money other)) return false;
        return yen == other.yen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yen);
    }

    @Override
    public String toString() {
        return "¥" + yen;
    }
}
