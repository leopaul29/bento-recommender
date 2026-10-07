package com.leopaul29.bento.ordering.domain.ports;

import com.leopaul29.bento.ordering.domain.ShopDay;

import java.time.LocalDate;
import java.util.Optional;

public interface ShopDayRepository {

    Optional<ShopDay> findByDate(LocalDate date);

    void save(ShopDay shopDay);
}
