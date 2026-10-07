package com.leopaul29.bento.ordering.domain;

/**
 * A rule of the ordering domain was broken. Deliberately one type rather than a dozen:
 * the message says which rule, and the invariant tests assert on the rule, not the class.
 */
public class DomainRuleViolation extends RuntimeException {

    public DomainRuleViolation(String message) {
        super(message);
    }
}
