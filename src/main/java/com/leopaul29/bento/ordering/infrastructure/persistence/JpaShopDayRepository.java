package com.leopaul29.bento.ordering.infrastructure.persistence;

import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.ShopDay;
import com.leopaul29.bento.ordering.domain.ports.ShopDayRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Translates between {@link ShopDay} and {@link ShopDayEntity}. */
@Repository
public class JpaShopDayRepository implements ShopDayRepository {

    private final ShopDayJpaRepository rows;

    public JpaShopDayRepository(ShopDayJpaRepository rows) {
        this.rows = rows;
    }

    @Override
    public Optional<ShopDay> findByDate(LocalDate date) {
        return rows.findById(date).map(JpaShopDayRepository::toDomain);
    }

    @Override
    public void save(ShopDay shopDay) {
        Map<Long, Integer> stock = new HashMap<>();
        shopDay.stockSnapshot().forEach((bentoId, count) -> stock.put(bentoId.value(), count));

        ShopDayEntity entity = rows.findById(shopDay.date())
                .map(existing -> {
                    existing.replaceStock(stock);
                    return existing;
                })
                .orElseGet(() -> new ShopDayEntity(
                        shopDay.date(), shopDay.orderingClosesAt(), stock));

        rows.save(entity);
    }

    private static ShopDay toDomain(ShopDayEntity entity) {
        Map<BentoId, Integer> stock = new HashMap<>();
        entity.remaining().forEach((bentoId, count) -> stock.put(BentoId.of(bentoId), count));
        return ShopDay.of(entity.serviceDate(), entity.orderingClosesAt(), stock);
    }
}
