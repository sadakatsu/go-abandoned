package com.sadakatsu.go.domain.outcome;

import com.sadakatsu.go.domain.intersection.Player;

public enum CompleteButNotScored implements Outcome {
    COMPLETE_BUT_NOT_SCORED;
    
    @Override
    public boolean isOver() {
        return true;
    }

    @Override
    public double getMargin() {
        throw new UnsupportedOperationException("The score has not yet been counted.");
    }

    @Override
    public Player getWinner() {
        throw new UnsupportedOperationException("The score has not yet been counted.");
    }

    @Override
    public double getBlackPointsOnBoard() {
        throw new UnsupportedOperationException("The score has not yet been counted.");
    }

    @Override
    public double getBlackScore() {
        throw new UnsupportedOperationException("The score has not yet been counted.");
    }

    @Override
    public double getWhitePointsOnBoard() {
        throw new UnsupportedOperationException("The score has not yet been counted.");
    }

    @Override
    public double getWhiteScore() {
        throw new UnsupportedOperationException("The score has not yet been counted.");
    }
}
