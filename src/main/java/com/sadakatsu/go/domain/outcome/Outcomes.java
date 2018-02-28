package com.sadakatsu.go.domain.outcome;

public class Outcomes {
    public static Outcome getFinalScore(
        double blackPointsOnBoard,
        double blackPointAdjustment,
        double whitePointsOnBoard,
        double whitePointAdjustment
    ) {
        validatePoints(blackPointsOnBoard, blackPointAdjustment, whitePointsOnBoard, whitePointAdjustment);
        double finalBlack = blackPointsOnBoard + blackPointAdjustment;
        double finalWhite = whitePointsOnBoard + whitePointAdjustment;
        double difference = finalBlack - finalWhite;
        
        Outcome outcome;
        if (difference == 0.) {
            outcome = new Draw(finalBlack, blackPointsOnBoard, whitePointsOnBoard);
        } else {
            outcome = new Win(finalBlack, blackPointsOnBoard, finalWhite, whitePointsOnBoard);
        }
        return outcome;
    }
    
    static void validatePoints( double...points ) {
        for (double point : points) {
            if (point * 4 != (double) (int) (point * 4)) {
                throw new IllegalArgumentException("Received points that were not even multiples of 0.25: " + points);
            }
        }
    }
    
    private Outcomes() {}
}
