package com.sadakatsu.go.domain.outcome;

import com.sadakatsu.go.domain.intersection.Stone;

public enum InProgress implements Outcome {
    IN_PROGRESS;
    
    @Override
    public boolean isOver() {
        return false;
    }

    @Override
    public double getMargin() {
        throw new UnsupportedOperationException("A game that is in progress does not have a victory margin.");
    }

    @Override
    public Stone getWinner() {
        throw new UnsupportedOperationException("A game that is in progress does not have a winner.");
    }

    @Override
    public double getBlackPointsOnBoard() {
        throw new UnsupportedOperationException(
            "A game that is in progress does not have points for either player until the game is over."
        );
    }

    @Override
    public double getBlackScore() {
        throw new UnsupportedOperationException(
            "A game that is in progress does not have points for either player until the game is over."
        );
    }

    @Override
    public double getWhitePointsOnBoard() {
        throw new UnsupportedOperationException(
            "A game that is in progress does not have points for either player until the game is over."
        );
    }

    @Override
    public double getWhiteScore() {
        throw new UnsupportedOperationException(
            "A game that is in progress does not have points for either player until the game is over."
        );
    }
}
