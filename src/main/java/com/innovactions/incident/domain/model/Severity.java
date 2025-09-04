package com.innovactions.incident.domain.model;

import lombok.Getter;

import java.util.Optional;

public enum Severity {
    MINOR(1),
    MAJOR(2),
    URGENT(3);

    @Getter
    private final int rank;

    Severity(int rank) { this.rank = rank; }

    public Optional<Severity> next() {
        return switch (this) {
            case MINOR -> Optional.of(MAJOR);
            case MAJOR -> Optional.of(URGENT);
            case URGENT -> Optional.empty();
        };
    }

    public boolean isHigherThan(Severity other) {
        return this.rank > other.rank;
    }
}
