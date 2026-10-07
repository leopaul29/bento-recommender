package com.leopaul29.bento.ordering.infrastructure.web;

import com.leopaul29.bento.ordering.application.CancelOrder;
import com.leopaul29.bento.ordering.application.PlaceOrder;
import com.leopaul29.bento.ordering.domain.CustomerId;
import com.leopaul29.bento.ordering.domain.DomainRuleViolation;
import com.leopaul29.bento.ordering.domain.OrderId;
import com.leopaul29.bento.ordering.domain.Quantity;
import com.leopaul29.bento.ordering.domain.ports.OrderRepository;
import com.leopaul29.bento.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * converts the result back. Every rule lives behind it — there is no decision in this class that
 * a reviewer has to check against {@code INVARIANTS.md}, which is the point of having put them in
 * the aggregate.
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
        CustomerId customerId = CustomerId.of(principal.getUser().getId());

        List<PlaceOrder.Item> items = request.items().stream()
                .map(item -> new PlaceOrder.Item(
                        com.leopaul29.bento.ordering.domain.BentoId.of(item.bentoId()),
                        Quantity.of(item.quantity())))
                .toList();

        OrderId id = placeOrder.handle(
                new PlaceOrder.Command(customerId, request.serviceDate(), items));

        return ResponseEntity.status(HttpStatus.CREATED).body(view(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderView> cancel(@PathVariable("id") UUID id) {
        OrderId orderId = OrderId.of(id);
        cancelOrder.handle(orderId);
        return ResponseEntity.ok(view(orderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderView> get(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(view(OrderId.of(id)));
    }

    private OrderView view(OrderId id) {
        return OrderView.of(orders.findById(id)
                .orElseThrow(() -> new DomainRuleViolation("no order " + id)));
    }
}
