package com.leopaul29.bento.ordering.application;

import com.leopaul29.bento.ordering.domain.ShopDay;
import com.leopaul29.bento.ordering.domain.ports.ShopDayRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class InMemoryShopDays implements ShopDayRepository {

    private final Map<LocalDate, ShopDay> stored = new HashMap<>();

    @Override
    public Optional<ShopDay> findByDate(LocalDate date) {
        return Optional.ofNullable(stored.get(date));
    }

    @Override
    public void save(ShopDay shopDay) {
        stored.put(shopDay.date(), shopDay);
    }
}
