package com.leopaul29.bento.ordering.infrastructure;

import com.leopaul29.bento.ordering.application.AcceptOrder;
import com.leopaul29.bento.ordering.application.CancelOrder;
import com.leopaul29.bento.ordering.application.PlaceOrder;
import com.leopaul29.bento.ordering.domain.ports.BentoCatalogue;
import com.leopaul29.bento.ordering.domain.ports.OrderRepository;
import com.leopaul29.bento.ordering.domain.ports.ShopDayRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * The single place where the ordering context is wired.
 *
 * <p>The use cases are plain objects with constructor parameters, so they are built here rather
 * than annotated. That is what lets the same three classes be unit-tested in 0.086 s with
 * in-memory fakes and run in production against Postgres, without a line of difference between
 * the two — and it is why a reader can see the whole dependency graph of the context on one
 * screen instead of inferring it from annotations scattered across packages.
 */
@Configuration
public class OrderingConfiguration {

    @Bean
    public Clock orderingClock() {
        return Clock.systemUTC();
    }

    @Bean
    public PlaceOrder placeOrder(OrderRepository orders, ShopDayRepository shopDays,
                                 BentoCatalogue catalogue, Clock clock) {
        return new PlaceOrder(orders, shopDays, catalogue, clock);
    }

    @Bean
    public AcceptOrder acceptOrder(OrderRepository orders, ShopDayRepository shopDays, Clock clock) {
        return new AcceptOrder(orders, shopDays, clock);
    }

    @Bean
    public CancelOrder cancelOrder(OrderRepository orders, Clock clock) {
        return new CancelOrder(orders, clock);
    }
}
