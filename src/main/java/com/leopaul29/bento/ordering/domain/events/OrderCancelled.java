package com.leopaul29.bento.ordering.domain.events;

import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.OrderStatus;

import java.time.Instant;

public record OrderCancelled(OrderId orderId, OrderStatus cancelledFrom, Instant occurredAt)
        implements DomainEvent {}
