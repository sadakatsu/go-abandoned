package com.sadakatsu.go.domain.intersection;

/**
 * This interface indicates that a subclass can be considered as a Player in Go.
 */
public interface Player {
    Player getOpposite();
}
