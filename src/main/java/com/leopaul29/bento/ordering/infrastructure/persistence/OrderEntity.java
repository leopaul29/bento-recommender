package com.leopaul29.bento.ordering.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The stored shape of an order.
 *
 * <p>Deliberately not the aggregate. It has a no-arg constructor and mutable fields because
 * Hibernate requires them — exactly what the domain refuses to have — so the two live in
 * different classes with an explicit mapping between them. That mapping is the lesson: the only
 * place a persistence decision can change without the domain noticing, and the only place a
 * domain decision has to be translated rather than obeyed.
 *
 * <p>The status is a {@code String}, not an {@code @Enumerated} domain enum. Mapping it by hand
 * in the adapter means renaming an {@code OrderStatus} constant produces a compile error in one
 * visible place instead of silently failing to read rows written last month.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * Eager, and ordered by an explicit column. Eager because every read of an order reads its
     * lines — the total is meaningless without them — and the ordering context has no use case
     * that fetches an order header alone.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_lines", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "line_number")
    private List<OrderLineRow> lines = new ArrayList<>();

    protected OrderEntity() {
        // for JPA
    }

    OrderEntity(UUID id, long customerId, LocalDate serviceDate, String status,
                List<OrderLineRow> lines) {
        this.id = id;
        this.customerId = customerId;
        this.serviceDate = serviceDate;
        this.status = status;
        this.lines = new ArrayList<>(lines);
    }

    /** Replaces the mutable state for an existing row. Called only by the adapter. */
    void replaceWith(String status, List<OrderLineRow> lines) {
        this.status = status;
        this.lines.clear();
        this.lines.addAll(lines);
    }

    UUID id() {
        return id;
    }

    long customerId() {
        return customerId;
    }

    LocalDate serviceDate() {
        return serviceDate;
    }

    String status() {
        return status;
    }

    List<OrderLineRow> lines() {
        return lines;
    }
}
