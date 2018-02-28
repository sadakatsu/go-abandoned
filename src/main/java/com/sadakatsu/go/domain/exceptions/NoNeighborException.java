package com.sadakatsu.go.domain.exceptions;

import com.sadakatsu.go.domain.Coordinate;
import com.sadakatsu.go.domain.Direction;

public class NoNeighborException extends Exception {
    private static final long serialVersionUID = 1200713882513805457L;

    public NoNeighborException( Coordinate coordinate, Direction direction, int maxComponent ) {
        super(
            String.format(
                "Coordinate %s does not have a neighbor to the %s (max component %d).",
                coordinate,
                direction,
                maxComponent
            )
        );
    }
}
