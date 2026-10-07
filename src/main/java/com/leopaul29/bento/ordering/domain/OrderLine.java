package com.leopaul29.bento.ordering.domain;

import java.util.Objects;

/**
 * One bento, a quantity, and the price it cost <em>when the order was placed</em>.
 *
 * <p>The captured {@code unitPrice} is I3: the catalogue can reprice the bento tomorrow and
 * this line is unaffected, because it never looks the price up again.
 *
 * <p>The constructor is package-private on purpose. A line has no identity and no life of its
 * own outside its order, so it is not addressable from outside the aggregate — only
 * {@link Order#addLine} can make one.
 */
public final class OrderLine {

    private final BentoId bentoId;
    private final Quantity quantity;
    private final Money unitPrice;

    OrderLine(BentoId bentoId, Quantity quantity, Money unitPrice) {
        this.bentoId = Objects.requireNonNull(bentoId, "a line needs a bento");
        this.quantity = Objects.requireNonNull(quantity, "a line needs a quantity");
        this.unitPrice = Objects.requireNonNull(unitPrice, "a line needs a captured unit price");
    }

    public Money subtotal() {
        return unitPrice.times(quantity.value());
    }

    public BentoId bentoId() {
        return bentoId;
    }

    public Quantity quantity() {
        return quantity;
    }

    public Money unitPrice() {
        return unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLine other)) return false;
        return bentoId.equals(other.bentoId)
                && quantity.equals(other.quantity)
                && unitPrice.equals(other.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bentoId, quantity, unitPrice);
    }

    @Override
    public String toString() {
        return bentoId + " " + quantity + " @ " + unitPrice;
    }
}
