package com.sadakatsu.go.domain.outcome;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.sadakatsu.go.domain.intersection.Stone;

public final class Draw implements Outcome {
    private final double blackPointsOnBoard;
    private final double score;
    private final double whitePointsOnBoard;
    
    private Integer hashCode;
    private String representation;
    
    public Draw( double score, double blackPointsOnBoard, double whitePointsOnBoard ) {
        Outcomes.validatePoints(score, blackPointsOnBoard, whitePointsOnBoard);
        this.blackPointsOnBoard = blackPointsOnBoard;
        this.score = score;
        this.whitePointsOnBoard = whitePointsOnBoard;
    }
    
    @Override
    public boolean isOver() {
        return true;
    }

    @Override
    public double getMargin() {
        return 0.;
    }

    @Override
    public Stone getWinner() {
        throw new UnsupportedOperationException("A game that ends in a draw does not have a winner.");
    }

    @Override
    public double getBlackPointsOnBoard() {
        return blackPointsOnBoard;
    }

    @Override
    public double getBlackScore() {
        return score;
    }

    @Override
    public double getWhitePointsOnBoard() {
        return whitePointsOnBoard;
    }

    @Override
    public double getWhiteScore() {
        return score;
    }
    
    @Override
    public boolean equals( Object other ) {
        boolean result = this == other;
        if (!result && other != null && Draw.class.equals(other.getClass())) {
            Draw that = (Draw) other;
            result =
                this.blackPointsOnBoard == that.blackPointsOnBoard &&
                this.score == that.score &&
                this.whitePointsOnBoard == that.whitePointsOnBoard;
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        if (hashCode == null) {
            HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(blackPointsOnBoard);
            builder.append(whitePointsOnBoard);
            builder.append(score);
            hashCode = builder.toHashCode();
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        if (representation == null) {
            representation = String.format(
                "ENDED IN DRAW { BLACK has %.2f with %.2f on board; WHITE has %.2f with %.2f on board }",
                score,
                blackPointsOnBoard,
                score,
                whitePointsOnBoard
            );
        }
        return representation;
    }
}
