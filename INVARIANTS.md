# Ordering invariants

The rules the `ordering` bounded context defends, and the test that pins each one.

This file is **checked by a test**: `InvariantsDocumentationTest` reads the table below and
fails if a row names a test method that does not exist, or if a test in `..ordering..` is not
named by any row. So the table cannot drift away from the code in either direction — which is
the only reason to trust a document like this at all.

| # | Invariant | Pinned by |
|---|---|---|
| I1 | An order must have at least one line to be placed. | `placingAnEmptyOrderIsRefused` |
| I2 | A line quantity is between 1 and 20 inclusive. | `aQuantityOutsideOneToTwentyIsRefused` |
| I3 | A line's unit price is captured when the line is added; a later catalogue change never alters a placed order. | `aLaterCataloguePriceChangeDoesNotAlterAPlacedOrder` |
| I4 | The total is the sum of unit price × quantity over every line, in yen. | `theTotalIsTheSumOfItsLines` |
| I5 | An order can only be placed strictly before the day's ordering cutoff. | `placingAtOrAfterTheCutoffIsRefused` |
| I6 | Only a bento on the service day's menu can be ordered. | `orderingABentoNotOnTheDaysMenuIsRefused` |
| I7 | The lifecycle is DRAFT → PLACED → ACCEPTED → PREPARING → READY → COLLECTED; no step may be skipped. | `aLifecycleStepCannotBeSkipped` |
| I8 | CANCELLED is reachable only from PLACED or ACCEPTED — never once preparation has started. | `cancellingAfterPreparationHasStartedIsRefused` |
| I9 | A COLLECTED order is immutable: no further transition and no new line. | `aCollectedOrderRefusesEveryFurtherChange` |
| I10 | Accepting an order takes stock from the day, and the remaining count can never go negative. | `acceptingMoreThanTheRemainingStockIsRefused` |

## Boundary rules

Not invariants of a single object, so they are enforced by `ArchitectureTest` rather than by a
unit test:

- Nothing in `..ordering.domain..` may depend on Spring, Jakarta/JPA or Lombok. The domain is
  plain Java and compiles without knowing a framework exists.
- Nothing in `..ordering.application..` may depend on Spring either. Wiring belongs in
  infrastructure (Phase 2).
- No class outside the `ordering.domain` package may construct an `OrderLine`: its constructor
  is package-private, so the aggregate is the only way in.

## Why these ten

A recommender has none of this — it is a scoring function over a read model, with no lifecycle
and no rule that can be violated, which is why it taught nothing about DDD. Each row above is a
rule an outside caller can try to break and the aggregate will refuse. That is the whole point,
and it is also what makes the Phase 4 agent eval possible: "restore invariant I8" has a right
answer a command can check.
