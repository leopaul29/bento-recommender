package com.leopaul29.bento.ordering.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * The stored shape of one day of service. The date is the key, because there is one shop and one
 * service day per date — the domain's {@code ShopDay} is identified the same way, so no surrogate
 * id is invented here just because JPA usually gets one.
 */
@Entity
@Table(name = "shop_days")
public class ShopDayEntity {

    @Id
    @Column(name = "service_date", nullable = false, updatable = false)
    private LocalDate serviceDate;

    @Column(name = "ordering_closes_at", nullable = false)
    private Instant orderingClosesAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shop_day_stock", joinColumns = @JoinColumn(name = "service_date"))
    @MapKeyColumn(name = "bento_id")
    @Column(name = "remaining")
    private Map<Long, Integer> remaining = new HashMap<>();

    protected ShopDayEntity() {
        // for JPA
    }

    ShopDayEntity(LocalDate serviceDate, Instant orderingClosesAt, Map<Long, Integer> remaining) {
        this.serviceDate = serviceDate;
        this.orderingClosesAt = orderingClosesAt;
        this.remaining = new HashMap<>(remaining);
    }

    void replaceStock(Map<Long, Integer> remaining) {
        this.remaining.clear();
        this.remaining.putAll(remaining);
    }

    LocalDate serviceDate() {
        return serviceDate;
    }

    Instant orderingClosesAt() {
        return orderingClosesAt;
    }

    Map<Long, Integer> remaining() {
        return remaining;
    }
}
