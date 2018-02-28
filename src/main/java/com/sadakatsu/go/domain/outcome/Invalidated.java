package com.sadakatsu.go.domain.outcome;

import com.sadakatsu.go.domain.intersection.Player;

public enum Invalidated implements Outcome {
    INVALIDATED;

    @Override
    public boolean isOver() {
        return true;
    }

    @Override
    public double getBlackPointsOnBoard() {
        throw new UnsupportedOperationException("An invalidated game has no scores.");
    }

    @Override
    public double getBlackScore() {
        throw new UnsupportedOperationException("An invalidated game has no scores.");
    }

    @Override
    public double getWhitePointsOnBoard() {
        throw new UnsupportedOperationException("An invalidated game has no scores.");
    }

    @Override
    public double getWhiteScore() {
        throw new UnsupportedOperationException("An invalidated game has no scores.");
    }

    @Override
    public double getMargin() {
        throw new UnsupportedOperationException("An invalidated game has no scores.");
    }

    @Override
    public Player getWinner() {
        throw new UnsupportedOperationException("An invalidated game has no winner.");
    }
}
