package com.leopaul29.bento.ordering.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * A stored order line. An {@code @Embeddable}, not an entity, because a line has no identity of
 * its own — which is the same reason {@code OrderLine}'s constructor is package-private in the
 * domain. The storage shape agrees with the model here by coincidence of good design, not by
 * being the same class.
 */
@Embeddable
public class OrderLineRow {

    @Column(name = "bento_id", nullable = false)
    private long bentoId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    /** Whole yen, captured at order time. Never re-read from the catalogue. */
    @Column(name = "unit_price_yen", nullable = false)
    private long unitPriceYen;

    protected OrderLineRow() {
        // for JPA
    }

    OrderLineRow(long bentoId, int quantity, long unitPriceYen) {
        this.bentoId = bentoId;
        this.quantity = quantity;
        this.unitPriceYen = unitPriceYen;
    }

    long bentoId() {
        return bentoId;
    }

    int quantity() {
        return quantity;
    }

    long unitPriceYen() {
        return unitPriceYen;
    }
}
