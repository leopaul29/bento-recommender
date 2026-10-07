package com.leopaul29.bento.ordering.domain.events;

import com.leopaul29.bento.ordering.domain.Money;
import com.leopaul29.bento.ordering.domain.OrderId;

import java.time.Instant;

public record OrderPlaced(OrderId orderId, Money total, Instant occurredAt) implements DomainEvent {}
