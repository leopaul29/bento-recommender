package com.leopaul29.bento.ordering.domain.events;

import com.leopaul29.bento.ordering.domain.OrderId;

import java.time.Instant;

public record OrderAccepted(OrderId orderId, Instant occurredAt) implements DomainEvent {}
