package com.leopaul29.bento.ordering.domain.events;

import java.time.Instant;

/** Something the domain decided happened. In-process objects, not an event store. */
public interface DomainEvent {

    Instant occurredAt();
}
