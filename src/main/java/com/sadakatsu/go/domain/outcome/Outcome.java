package com.sadakatsu.go.domain.outcome;

import com.sadakatsu.go.domain.intersection.Player;

public interface Outcome {
    boolean isOver();
    double getBlackPointsOnBoard();
    double getBlackScore();
    double getWhitePointsOnBoard();
    double getWhiteScore();
    double getMargin();
    Player getWinner();
}
