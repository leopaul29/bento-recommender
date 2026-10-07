package com.leopaul29.bento.ordering.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/** Spring Data over the stored shape. Not a port — {@code OrderRepository} is the port. */
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    /**
     * How many of each bento a customer has ordered, over orders that were at least accepted.
     * A read model: it returns counts, not aggregates, because the recommender has no business
     * loading an {@code Order} to rank a menu.
     */
    @Query("""
            select line.bentoId as bentoId, sum(line.quantity) as total
            from OrderEntity o join o.lines line
            where o.customerId = :customerId
              and o.status in ('ACCEPTED', 'PREPARING', 'READY', 'COLLECTED')
            group by line.bentoId
            order by sum(line.quantity) desc
            """)
    List<BentoOrderCount> countOrderedBentosFor(long customerId);

    /** Projection for {@link #countOrderedBentosFor}. */
    interface BentoOrderCount {
        long getBentoId();

        long getTotal();
    }
}
