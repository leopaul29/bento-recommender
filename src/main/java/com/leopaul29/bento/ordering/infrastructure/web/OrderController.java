package com.leopaul29.bento.ordering.infrastructure.web;

import com.leopaul29.bento.ordering.application.CancelOrder;
import com.leopaul29.bento.ordering.application.PlaceOrder;
import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.CustomerId;
import com.leopaul29.bento.ordering.domain.DomainRuleViolation;
import com.leopaul29.bento.ordering.domain.Order;
import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.Quantity;
import com.leopaul29.bento.ordering.domain.ports.OrderRepository;
import com.leopaul29.bento.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * The ordering context's HTTP edge.
 *
 * <p>Thin on purpose: it converts the wire shape into a command, hands it to a use case, and
 * converts the result back. Every domain rule lives behind it — there is no decision in this class
 * that a reviewer has to check against {@code INVARIANTS.md}.
 *
 * <p>What this class <em>is</em> responsible for is authorization, because that is not a property
 * of an order but of who is asking. An order id is a UUID and therefore hard to guess, which is
 * not the same as being protected: every path here establishes ownership against the authenticated
 * principal before answering.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final PlaceOrder placeOrder;
    private final CancelOrder cancelOrder;
    private final OrderRepository orders;

    public OrderController(PlaceOrder placeOrder, CancelOrder cancelOrder, OrderRepository orders) {
        this.placeOrder = placeOrder;
        this.cancelOrder = cancelOrder;
        this.orders = orders;
    }

    @PostMapping
    public ResponseEntity<OrderView> place(@Valid @RequestBody PlaceOrderRequest request,
                                           @AuthenticationPrincipal UserPrincipal principal) {
        CustomerId customerId = customerOf(principal);

        List<PlaceOrder.Item> items = request.items().stream()
                .map(item -> new PlaceOrder.Item(
                        BentoId.of(item.bentoId()), Quantity.of(item.quantity())))
                .toList();

        OrderId id = placeOrder.handle(
                new PlaceOrder.Command(customerId, request.serviceDate(), items));

        return ResponseEntity.status(HttpStatus.CREATED).body(OrderView.of(load(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderView> cancel(@PathVariable("id") UUID id,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        OrderId orderId = OrderId.of(id);
        requireOwnedBy(orderId, principal);

        cancelOrder.handle(orderId);

        return ResponseEntity.ok(OrderView.of(load(orderId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderView> get(@PathVariable("id") UUID id,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        OrderId orderId = OrderId.of(id);
        requireOwnedBy(orderId, principal);

        return ResponseEntity.ok(OrderView.of(load(orderId)));
    }

    private CustomerId customerOf(UserPrincipal principal) {
        return CustomerId.of(principal.getUser().getId());
    }

    /**
     * Refuses an order that belongs to someone else.
     *
     * <p>403 rather than 404: the caller supplied a valid id and is authenticated, so hiding the
     * order's existence buys nothing it could not already infer, and a wrong status code is how a
     * real permission bug gets mistaken for a missing record.
     */
    private void requireOwnedBy(OrderId orderId, UserPrincipal principal) {
        if (!load(orderId).customerId().equals(customerOf(principal))) {
            throw new AccessDeniedException("this order belongs to another customer");
        }
    }

    private Order load(OrderId id) {
        return orders.findById(id)
                .orElseThrow(() -> new DomainRuleViolation("no order " + id));
    }
}
