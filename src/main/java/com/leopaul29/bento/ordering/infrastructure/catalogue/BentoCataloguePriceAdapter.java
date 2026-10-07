package com.leopaul29.bento.ordering.infrastructure.catalogue;

import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.Money;
import com.leopaul29.bento.ordering.domain.ports.BentoCatalogue;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Reads a price out of the catalogue context for the ordering context.
 *
 * <p>This class is the whole of the coupling between the two: it is the only place that knows both
 * {@code entities.Bento} and {@code ordering.domain.Money}. The ordering domain has never heard of
 * {@code Bento} — it holds a {@link BentoId} and asks this port for a number.
 *
 * <p>A bento with no price is not for sale, so the answer is empty rather than zero. {@code
 * PlaceOrder} then refuses the line, which is the correct outcome and is already pinned by a test.
 */
@Component
public class BentoCataloguePriceAdapter implements BentoCatalogue {

    private final BentoRepository bentos;

    public BentoCataloguePriceAdapter(BentoRepository bentos) {
        this.bentos = bentos;
    }

    @Override
    public Optional<Money> priceOf(BentoId bentoId) {
        return bentos.findById(bentoId.value())
                .map(bento -> bento.getPriceYen())
                .map(Money::yen);
    }
}
