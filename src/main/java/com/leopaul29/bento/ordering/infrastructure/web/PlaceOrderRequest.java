package com.leopaul29.bento.ordering.infrastructure.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * The wire shape of a place-order request. A third shape, separate from both the command and the
 * aggregate, because the wire is the one thing an attacker controls.
 *
 * <p>Note what is <em>not</em> here: the customer id. It is taken from the authenticated principal,
 * so no caller can order on someone else's behalf by editing a field.
 */
public record PlaceOrderRequest(
        @NotNull(message = "serviceDate is required") LocalDate serviceDate,
        @NotEmpty(message = "an order needs at least one item") List<@Valid Item> items) {

    public record Item(
            @NotNull(message = "bentoId is required") Long bentoId,
            @NotNull(message = "quantity is required") Integer quantity) {}
}
