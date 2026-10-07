package com.leopaul29.bento.ordering.domain.ports;

import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.Money;

import java.util.Optional;

/**
 * Reads the current price of a bento from the catalogue context. Called once, when a line is
 * added, and never again — the price is then the order's (I3).
 */
public interface BentoCatalogue {

    Optional<Money> priceOf(BentoId bentoId);
}
