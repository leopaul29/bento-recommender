package com.leopaul29.bento.ordering.infrastructure.web;

import com.leopaul29.bento.ordering.domain.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** The read shape of an order. Built from the aggregate, which is never serialised directly. */
public record OrderView(
        UUID id,
        long customerId,
        LocalDate serviceDate,
        String status,
        long totalYen,
        List<Line> lines) {

    public record Line(long bentoId, int quantity, long unitPriceYen, long subtotalYen) {}

    static OrderView of(Order order) {
        return new OrderView(
                order.id().value(),
                order.customerId().value(),
                order.serviceDate(),
                order.status().name(),
                order.total().yen(),
                order.lines().stream()
                        .map(line -> new Line(
                                line.bentoId().value(),
                                line.quantity().value(),
                                line.unitPrice().yen(),
                                line.subtotal().yen()))
                        .toList());
    }
}
