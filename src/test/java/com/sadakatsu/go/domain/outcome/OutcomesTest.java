package com.sadakatsu.go.domain.outcome;

import static com.sadakatsu.go.domain.outcome.OutcomeTestHelper.*;
import static com.sadakatsu.util.TestHelper.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class OutcomesTest {
    private static interface TestOutcomeGenerator {
        void test(
            double blackPointsOnBoard,
            double blackAdjustment,
            double whitePointsOnBoard,
            double whiteAdjustment,
            Outcome outcome
        );
    }
    
    @Test
    public void outcomesDoesNotExposeConstructor() {
        assertEquals(0, Outcomes.class.getConstructors().length);
    }
    
    @Test
    public void blackScoreMustBeEvenlyDivisibleByOneQuarter() {
        for (int run = 0; run <= 100; ++run) {
            double invalid = generateRandomInvalidScore();
            
            try {
                Outcome wrong = Outcomes.getFinalScore(invalid, 0., 0., 0.);
                failForReturn("Outcomes.getFinalScore", "IllegalArgumentException", wrong, invalid, 0., 0., 0.);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable(
                    "Outcomes.getFinalScore",
                    "IllegalArgumentException",
                    t,
                    invalid,
                    0.,
                    0.,
                    0.
                );
            }
        }
    }
    
    @Test
    public void blackPointsOnBoardMustBeEvenlyDivisibleByOneQuarter() {
        for (int run = 0; run <= 100; ++run) {
            double invalid = generateRandomInvalidScore();
            
            try {
                Outcome wrong = Outcomes.getFinalScore(0., invalid, 0., 0.);
                failForReturn("Outcomes.getFinalScore", "IllegalArgumentException", wrong, 0., invalid, 0., 0.);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable(
                    "Outcomes.getFinalScore",
                    "IllegalArgumentException",
                    t,
                    0., 
                    invalid,
                    0.,
                    0.
                );
            }
        }
    }
    
    @Test
    public void whiteScoreMustBeEvenlyDivisibleByOneQuarter() {
        for (int run = 0; run <= 100; ++run) {
            double invalid = generateRandomInvalidScore();
            
            try {
                Outcome wrong = Outcomes.getFinalScore(0., 0., invalid, 0.);
                failForReturn("Outcomes.getFinalScore", "IllegalArgumentException", wrong, 0., 0., invalid, 0.);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable(
                    "Outcomes.getFinalScore",
                    "IllegalArgumentException",
                    t,
                    0.,
                    0.,
                    invalid,
                    0.
                );
            }
        }
    }
    
    @Test
    public void whitePointsOnBoardMustBeEvenlyDivisibleByOneQuarter() {
        for (int run = 0; run <= 100; ++run) {
            double invalid = generateRandomInvalidScore();
            
            try {
                Outcome wrong = Outcomes.getFinalScore(0., 0., 0., invalid);
                failForReturn("Outcomes.getFinalScore", "IllegalArgumentException", wrong, 0., 0., 0., invalid);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable(
                    "Outcomes.getFinalScore",
                    "IllegalArgumentException",
                    t,
                    0.,
                    0.,
                    0.,
                    invalid
                );
            }
        }
    }
    
    @Test
    public void validScoresWithDifferentBlackAndWhiteScoresProducesExpectedWin() {
        testRandomValidWins(
            (blackPoints, blackAdjustment, whitePoints, whiteAdjustment, outcome) -> {
                double blackScore = blackPoints + blackAdjustment;
                double whiteScore = whitePoints + whiteAdjustment;
                Win expected = new Win(blackScore, blackPoints, whiteScore, whitePoints);
                assertEquals(expected, outcome);
            }
        );
    }
    
    private void testRandomValidWins( TestOutcomeGenerator lambda ) {
        for (int run = 0; run <= 100; ++run) {
            double blackScore = generateRandomValidScore();
            double whiteScore = generateDifferentValidScore(blackScore);
            
            double blackAdjustment = generateRandomValidScore();
            double blackPointsOnBoard = blackScore - blackAdjustment;
            
            double whiteAdjustment = generateRandomValidScore();
            double whitePointsOnBoard = whiteScore - whiteAdjustment;
            
            Outcome outcome = Outcomes.getFinalScore(
                blackPointsOnBoard,
                blackAdjustment,
                whitePointsOnBoard,
                whiteAdjustment
            );
            lambda.test(blackPointsOnBoard, blackAdjustment, whitePointsOnBoard, whiteAdjustment, outcome);
        }
    }
    
    @Test
    public void validScoresWithSameBlackAndWhiteScoresProducesExpectedDraw() {
        testRandomValidDraws(
            (blackPoints, blackAdjustment, whitePoints, whiteAdjustment, outcome) -> {
                double score = blackPoints + blackAdjustment;
                Draw expected = new Draw(score, blackPoints, whitePoints);
                assertEquals(expected, outcome);
            }
        );
    }
    
    private void testRandomValidDraws( TestOutcomeGenerator lambda ) {
        for (int run = 0; run <= 100; ++run) {
            double score = generateRandomValidScore();
            
            double blackAdjustment = generateRandomValidScore();
            double blackPointsOnBoard = score - blackAdjustment;
            
            double whiteAdjustment = generateRandomValidScore();
            double whitePointsOnBoard = score - whiteAdjustment;
            
            Outcome outcome = Outcomes.getFinalScore(
                blackPointsOnBoard,
                blackAdjustment,
                whitePointsOnBoard,
                whiteAdjustment
            );
            lambda.test(blackPointsOnBoard, blackAdjustment, whitePointsOnBoard, whiteAdjustment, outcome);
        }
    }
}
