package com.leopaul29.bento.ordering.domain.ports;

import com.leopaul29.bento.ordering.domain.Order;
import com.leopaul29.bento.ordering.domain.OrderId;

import java.util.Optional;

/**
 * The port the domain needs to persist orders. An interface here, an implementation in
 * infrastructure — which is why the domain compiles without knowing JPA exists.
 */
public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(OrderId id);
}
