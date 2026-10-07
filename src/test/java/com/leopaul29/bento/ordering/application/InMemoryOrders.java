package com.leopaul29.bento.ordering.application;

import com.leopaul29.bento.ordering.domain.Order;
import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.ports.OrderRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** A fake, not a mock: the use-case tests assert on real behaviour, not on calls. */
final class InMemoryOrders implements OrderRepository {

    private final Map<OrderId, Order> stored = new HashMap<>();

    @Override
    public void save(Order order) {
        stored.put(order.id(), order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(stored.get(id));
    }

    int size() {
        return stored.size();
    }
}
