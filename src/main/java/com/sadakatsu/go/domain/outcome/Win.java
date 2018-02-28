package com.sadakatsu.go.domain.outcome;

import static com.sadakatsu.go.domain.intersection.Stone.BLACK;
import static com.sadakatsu.go.domain.intersection.Stone.WHITE;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.sadakatsu.go.domain.intersection.Player;

public final class Win implements Outcome {
    private final double blackPointsOnBoard;
    private final double blackScore;
    private final double margin;
    private final double whitePointsOnBoard;
    private final double whiteScore;
    private final Player winner;
    
    private Integer hashCode;
    private String representation;
    
    public Win( double blackScore, double blackPointsOnBoard, double whiteScore, double whitePointsOnBoard ) {
        Outcomes.validatePoints(blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard);
        validateFinalScores(blackScore, whiteScore);
        this.blackPointsOnBoard = blackPointsOnBoard;
        this.blackScore = blackScore;
        this.whitePointsOnBoard = whitePointsOnBoard;
        this.whiteScore = whiteScore;
        
        if (blackScore > whiteScore) {
            winner = BLACK;
            margin = blackScore - whiteScore;
        } else {
            winner = WHITE;
            margin = whiteScore - blackScore;
        }
    }
    
    private void validateFinalScores( double blackScore, double whiteScore ) {
        if (blackScore == whiteScore) {
            throw new IllegalArgumentException(
                "The final scores for a Win must be unequal; consider using Draw instead: " +
                blackScore +
                ", " +
                whiteScore
            );
        }
    }
    
    @Override
    public boolean isOver() {
        return true;
    }

    @Override
    public double getBlackPointsOnBoard() {
        return blackPointsOnBoard;
    }

    @Override
    public double getBlackScore() {
        return blackScore;
    }

    @Override
    public double getWhitePointsOnBoard() {
        return whitePointsOnBoard;
    }

    @Override
    public double getWhiteScore() {
        return whiteScore;
    }

    @Override
    public double getMargin() {
        return margin;
    }

    @Override
    public Player getWinner() {
        return winner;
    }
    
    @Override
    public boolean equals( Object other ) {
        boolean result = this == other;
        if (!result && other != null && Win.class.equals(other.getClass())) {
            Win that = (Win) other;
            result =
                this.blackPointsOnBoard == that.blackPointsOnBoard &&
                this.blackScore == that.blackScore &&
                this.whitePointsOnBoard == that.whitePointsOnBoard &&
                this.whiteScore == that.whiteScore;
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        if (hashCode == null) {
            HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(this.blackPointsOnBoard);
            builder.append(this.blackScore);
            builder.append(this.whitePointsOnBoard);
            builder.append(this.whiteScore);
            hashCode = builder.toHashCode();
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        if (representation == null) {
            representation = String.format(
                "WON by %s by %.2f { BLACK has %.2f with %.2f on board; WHITE has %.2f with %.2f on board }",
                winner,
                margin,
                blackScore,
                blackPointsOnBoard,
                whiteScore,
                whitePointsOnBoard
            );
        }
        return representation;
    }
}
