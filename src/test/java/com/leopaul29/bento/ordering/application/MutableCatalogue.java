package com.leopaul29.bento.ordering.application;

import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.Money;
import com.leopaul29.bento.ordering.domain.ports.BentoCatalogue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Repriceable on purpose — that is how I3 gets tested rather than asserted. */
final class MutableCatalogue implements BentoCatalogue {

    private final Map<BentoId, Money> prices = new HashMap<>();

    void setPrice(BentoId bentoId, Money price) {
        prices.put(bentoId, price);
    }

    @Override
    public Optional<Money> priceOf(BentoId bentoId) {
        return Optional.ofNullable(prices.get(bentoId));
    }
}
