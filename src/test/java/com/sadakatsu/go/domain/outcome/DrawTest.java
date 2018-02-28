package com.sadakatsu.go.domain.outcome;

import static com.sadakatsu.go.domain.outcome.OutcomeTestHelper.*;
import static com.sadakatsu.util.TestHelper.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sadakatsu.go.domain.intersection.Player;

public class DrawTest {
    private static interface TestDrawGenerator {
        void test(
            double score,
            double blackPointsOnBoard,
            double whitePointsOnBoard,
            Draw draw
        );
    }
    
    @Test
    public void scoreMustBeEvenlyDivisibleByOneQuarter() {
        for (int run = 0; run <= 100; ++run) {
            double invalid = generateRandomInvalidScore();
            
            try {
                Draw wrong = new Draw(invalid, 0., 0.);
                failForReturn("new Draw", "IllegalArgumentException", wrong, invalid, 0., 0.);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable(
                    "new Win",
                    "IllegalArgumentException",
                    t,
                    invalid,
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
                Draw wrong = new Draw(0., invalid, 0.);
                failForReturn("new Draw", "IllegalArgumentException", wrong, 0., invalid, 0.);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable(
                    "new Win",
                    "IllegalArgumentException",
                    t,
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
                Draw wrong = new Draw(0., 0., invalid);
                failForReturn("new Draw", "IllegalArgumentException", wrong, 0., 0., invalid);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable(
                    "new Win",
                    "IllegalArgumentException",
                    t,
                    0.,
                    0.,
                    invalid
                );
            }
        }
    }
    
    @Test
    public void allScoresDivisibleByOneQuarterGeneratesValidDraw() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) ->
                assertNotNull(draw)
        );
    }
    
    private void testRandomValidDraws( TestDrawGenerator lambda ) {
        for (int run = 0; run <= 100; ++run) {
            double score = generateRandomValidScore();
            double blackPointsOnBoard = generateRandomValidScore();
            double whitePointsOnBoard = generateRandomValidScore();
            
            Draw draw = new Draw(score, blackPointsOnBoard, whitePointsOnBoard);
            lambda.test(score, blackPointsOnBoard, whitePointsOnBoard, draw);
        }
    }
    
    @Test
    public void aDrawIsAlwaysOver() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) ->
                assertTrue(draw.isOver())
        );
    }
    
    @Test
    public void aDrawMarginOfVictoryIsAlwaysZero() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) ->
                assertEquals(0., draw.getMargin(), 0.)
        );
    }
    
    @Test
    public void aDrawNeverHasAWinner() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) -> {
                try {
                    Player wrong = draw.getWinner();
                    failForReturn(draw.toString() + ".getWinner", "UnsupportedOperationException", wrong);
                } catch (UnsupportedOperationException e) {
                    // success
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable t) {
                    failForWrongThrowable(draw.toString() + ".getWinner", "UnsupportedOperationException", t);
                }
            }
        );
    }
    
    @Test
    public void aDrawReturnsItsBlackPointsOnBoard() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) ->
                assertEquals(blackPointsOnBoard, draw.getBlackPointsOnBoard(), 0.)
        );
    }
    
    @Test
    public void aDrawReturnsTheSharedScoreForGetBlackScore() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) ->
                assertEquals(score, draw.getBlackScore(), 0.)
        );
    }
    
    @Test
    public void aDrawReturnsItsWhitePointsOnBoard() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) ->
                assertEquals(whitePointsOnBoard, draw.getWhitePointsOnBoard(), 0.)
        );
    }
    
    @Test
    public void aDrawReturnsTheSharedScoreForGetWhiteScore() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) ->
                assertEquals(score, draw.getWhiteScore(), 0.)
        );
    }
    
    @Test
    public void aDrawAlwaysEqualsItself() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) ->
                assertEquals(draw, draw)
        );
    }
    
    @Test
    public void aDrawOnlyEqualsDrawObjects() {
        Object[] notDraws = { null, "not a Draw", 17 };
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) ->{
                for (Object notADraw : notDraws) {
                    assertNotEquals(notADraw, draw);
                    assertNotEquals(draw, notADraw);
                }
            }
        );
    }
    
    @Test
    public void twoDrawsAreNotEqualIfTheirScoresAreDifferent() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) -> {
                double differentScore = generateDifferentValidScore(score);
                Draw different = new Draw(differentScore, blackPointsOnBoard, whitePointsOnBoard);
                assertNotEquals(different, draw);
            }
        );
    }
    
    @Test
    public void twoDrawsAreNotEqualIfTheirBlackPointsOnBoardAreDifferent() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) -> {
                double differentScore = generateDifferentValidScore(blackPointsOnBoard);
                Draw different = new Draw(score, differentScore, whitePointsOnBoard);
                assertNotEquals(different, draw);
            }
        );
    }
    
    @Test
    public void twoDrawsAreNotEqualIfTheirWhitePointsOnBoardAreDifferent() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) -> {
                double differentScore = generateDifferentValidScore(whitePointsOnBoard);
                Draw different = new Draw(score, blackPointsOnBoard, differentScore);
                assertNotEquals(different, draw);
            }
        );
    }
    
    @Test
    public void twoDrawsAreEqualIfAllTheirScoresAreEqual() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) -> {
                Draw same = new Draw(score, blackPointsOnBoard, whitePointsOnBoard);
                assertEquals(same, draw);
            }
        );
    }
    
    @Test
    public void identicalDrawsReturnIdenticalHashCodes() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) -> {
                Draw same = new Draw(score, blackPointsOnBoard, whitePointsOnBoard);
                assertEquals(same.hashCode(), draw.hashCode());
            }
        );
    }
    
    @Test
    public void hashCodeAlwaysReturnsSameValuePerDrawObject() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) -> {
                int first = draw.hashCode();
                int second = draw.hashCode();
                assertEquals(first, second);
            }
        );
    }
    
    @Test
    public void drawOverridesToString() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) -> {
                String representation = draw.toString();
                String original = getDefaulToString(draw);
                assertNotEquals(original, representation);
            }
        );
    }
    
    @Test
    public void toStringAlwaysReturnsSameTextPerObject() {
        testRandomValidDraws(
            (score, blackPointsOnBoard, whitePointsOnBoard, draw) -> {
                String first = draw.toString();
                String second = draw.toString();
                assertEquals(first, second);
            }
        );
    }
    
    @Test
    public void drawToStringVariesByScoresSoRepresentationsAreEqualOnlyIfDrawsAreEqual() {
        testRandomValidDraws(
            (a, b, c, draw1) -> {
                testRandomValidDraws(
                    (d, e, f, draw2) -> {
                        if (draw1.equals(draw2)) {
                            assertEquals(draw1.toString(), draw2.toString());
                        } else {
                            assertNotEquals(draw1.toString(), draw2.toString());
                        }
                    }
                );
            }
        );
    }
}
