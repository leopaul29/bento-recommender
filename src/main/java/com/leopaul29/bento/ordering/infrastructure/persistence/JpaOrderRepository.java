package com.leopaul29.bento.ordering.infrastructure.persistence;

import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.CustomerId;
import com.leopaul29.bento.ordering.domain.Money;
import com.leopaul29.bento.ordering.domain.Order;
import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.OrderLine;
import com.leopaul29.bento.ordering.domain.OrderStatus;
import com.leopaul29.bento.ordering.domain.Quantity;
import com.leopaul29.bento.ordering.domain.ports.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Translates between the {@link Order} aggregate and {@link OrderEntity}.
 *
 * <p>The dependency points inwards: this class knows the domain, the domain does not know this
 * class exists. Everything JPA needs and the domain refuses — a no-arg constructor, mutable
 * fields, primitive ids, a string status — is absorbed here, in two methods that are the whole
 * cost of keeping the model clean.
 */
@Repository
public class JpaOrderRepository implements OrderRepository {

    private final OrderJpaRepository rows;

    public JpaOrderRepository(OrderJpaRepository rows) {
        this.rows = rows;
    }

    @Override
    public void save(Order order) {
        List<OrderLineRow> lines = order.lines().stream()
                .map(JpaOrderRepository::toRow)
                .toList();

        // An existing row is updated in place rather than replaced, so JPA sees one row whose
        // status changed, not a delete followed by an insert on the same primary key.
        OrderEntity entity = rows.findById(order.id().value())
                .map(existing -> {
                    existing.replaceWith(order.status().name(), lines);
                    return existing;
                })
                .orElseGet(() -> new OrderEntity(
                        order.id().value(),
                        order.customerId().value(),
                        order.serviceDate(),
                        order.status().name(),
                        lines));

        rows.save(entity);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return rows.findById(id.value()).map(JpaOrderRepository::toDomain);
    }

    private static OrderLineRow toRow(OrderLine line) {
        return new OrderLineRow(
                line.bentoId().value(),
                line.quantity().value(),
                line.unitPrice().yen());
    }

    private static Order toDomain(OrderEntity entity) {
        List<Order.LineSnapshot> lines = entity.lines().stream()
                .map(row -> new Order.LineSnapshot(
                        BentoId.of(row.bentoId()),
                        Quantity.of(row.quantity()),
                        Money.yen(row.unitPriceYen())))
                .toList();

        return Order.rehydrate(
                OrderId.of(entity.id()),
                CustomerId.of(entity.customerId()),
                entity.serviceDate(),
                OrderStatus.valueOf(entity.status()),
                lines);
    }
}
