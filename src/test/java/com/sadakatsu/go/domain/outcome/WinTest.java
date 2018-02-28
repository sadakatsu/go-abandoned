package com.sadakatsu.go.domain.outcome;

import static com.sadakatsu.go.domain.intersection.Stone.*;
import static com.sadakatsu.go.domain.outcome.OutcomeTestHelper.*;
import static com.sadakatsu.util.TestHelper.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sadakatsu.go.domain.intersection.Player;

public class WinTest {
    private static interface TestWinGenerator {
        void test(
            double blackScore,
            double blackPointsOnBoard,
            double whiteScore,
            double whitePointsOnBoard,
            Win win
        );
    }
    
    @Test
    public void blackScoreMustBeEvenlyDivisibleByOneQuarter() {
        for (int run = 0; run <= 100; ++run) {
            double invalid = generateRandomInvalidScore();
            
            try {
                Win wrong = new Win(invalid, 0., 0., 0.);
                failForReturn("new Win", "IllegalArgumentException", wrong, invalid, 0., 0., 0.);
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
                Win wrong = new Win(0., invalid, 0., 0.);
                failForReturn("new Win", "IllegalArgumentException", wrong, 0., invalid, 0., 0.);
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
                Win wrong = new Win(0., 0., invalid, 0.);
                failForReturn("new Win", "IllegalArgumentException", wrong, 0., 0., invalid, 0.);
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
                Win wrong = new Win(0., 0., 0., invalid);
                failForReturn("new Win", "IllegalArgumentException", wrong, 0., 0., 0., invalid);
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
                    0.,
                    invalid
                );
            }
        }
    }
    
    @Test
    public void blackScoreAndWhiteScoreMustBeUnequal() {
        for (int run = 0; run <= 100; ++run) {
            double validScore = generateRandomValidScore();
            double blackPointsOnBoard = generateRandomValidScore();
            double whitePointsOnBoard = generateRandomValidScore();
            
            try {
                Win wrong = new Win(validScore, blackPointsOnBoard, validScore, whitePointsOnBoard);
                failForReturn(
                    "new Win",
                    "IllegalArgumentException",
                    wrong,
                    validScore,
                    blackPointsOnBoard,
                    validScore,
                    whitePointsOnBoard
                );
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable(
                    "new Win",
                    "IllegalArgumentException",
                    t,
                    validScore,
                    blackPointsOnBoard,
                    validScore,
                    whitePointsOnBoard
                );
            }
        }
    }
    
    @Test
    public void allScoresDivisibleByOneQuarterAndTwoOverallScoresUnequalGeneratesValidWin() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) ->
                assertNotNull(win)
        );
    }
    
    private void testRandomValidWins( TestWinGenerator lambda ) {
        for (int run = 0; run <= 100; ++run) {
            double blackScore, whiteScore;
            do {
                blackScore = generateRandomValidScore();
                whiteScore = generateRandomValidScore();
            } while (blackScore == whiteScore);
            
            double blackPointsOnBoard = generateRandomValidScore();
            double whitePointsOnBoard = generateRandomValidScore();
            
            Win win = new Win(blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard);
            lambda.test(blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win);
        }
    }
    
    @Test
    public void aWinIsAlwaysOver() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) ->
                assertTrue(win.isOver())
        );
    }
    
    @Test
    public void aWinCanReturnItsBlackScore() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) ->
                assertEquals(blackPointsOnBoard, win.getBlackPointsOnBoard(), 0.)
        );
    }
    
    @Test
    public void aWinCanReturnsItsBlackPointsOnBoard() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) ->
                assertEquals(blackScore, win.getBlackScore(), 0.)
        );
    }
    
    @Test
    public void aWinCanReturnItsWhiteScore() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) ->
                assertEquals(whiteScore, win.getWhiteScore(), 0.)
        );
    }
    
    @Test
    public void aWinCanReturnsItsWhitePointsOnBoard() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) ->
                assertEquals(whitePointsOnBoard, win.getWhitePointsOnBoard(), 0.)
        );
    }
    
    @Test
    public void aWinReturnsTheExpectedWinner() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                Player winner = (blackScore > whiteScore ? BLACK : WHITE);
                assertEquals(winner, win.getWinner());
            }
        );
    }
    
    @Test
    public void aWinReturnsTheMarginOfVictory() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                double margin = Math.abs(blackScore - whiteScore);
                assertEquals(margin, win.getMargin(), 0.);
            }
        );
    }
    
    @Test
    public void aWinAlwaysEqualsItself() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) ->
                assertEquals(win, win)
        );
    }
    
    @Test
    public void aWinOnlyEqualsWinObjects() {
        Object[] notWins = { null, "not a Win", 17 };
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                for (Object notAWin : notWins) {
                    assertNotEquals(notAWin, win);
                    assertNotEquals(win, notAWin);
                }
            }
        );
    }
    
    @Test
    public void twoWinsAreNotEqualIfTheirBlackScoresAreDifferent() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                double differentBlackScore;
                if (blackScore > whiteScore) {
                    differentBlackScore = generateRandomScoreGreaterThanExcluding(whiteScore, blackScore);
                } else {
                    differentBlackScore = generateRandomScoreLessThanExcluding(whiteScore, blackScore);
                }
                Win differentWin = new Win(differentBlackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard);
                assertNotEquals(win, differentWin);
            }
        );
    }
    
    @Test
    public void twoWinsAreNotEqualIfTheirBlackPointsOnBoardAreDifferent() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                double differentBlackPointsOnBoard = generateDifferentValidScore(blackPointsOnBoard);
                Win differentWin = new Win(blackScore, differentBlackPointsOnBoard, whiteScore, whitePointsOnBoard);
                assertNotEquals(win, differentWin);
            }
        );
    }
    
    @Test
    public void twoWinsAreNotEqualIfTheirWhiteScoresAreDifferent() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                double differentWhiteScore;
                if (blackScore > whiteScore) {
                    differentWhiteScore = generateRandomScoreLessThanExcluding(blackScore, whiteScore);
                } else {
                    differentWhiteScore = generateRandomScoreLessThanExcluding(blackScore, whiteScore);
                }
                Win differentWin = new Win(blackScore, blackPointsOnBoard, differentWhiteScore, whitePointsOnBoard);
                
                assertNotEquals(win, differentWin);
            }
        );
    }
    
    @Test
    public void twoWinsAreNotEqualIfTheirWhitePointsOnBoardAreDifferent() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                double differentWhitePointsOnBoard = generateDifferentValidScore(whitePointsOnBoard);
                Win differentWin = new Win(blackScore, blackPointsOnBoard, whiteScore, differentWhitePointsOnBoard);
                assertNotEquals(win, differentWin);
            }
        );
    }
    
    @Test
    public void twoWinsAreIdenticalIfTheyReceivedIdenticalScores() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                Win identical = new Win(blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard);
                assertEquals(win, identical);
            }
        );
    }
    
    @Test
    public void identicalWinsReturnIdenticalHashCodes() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                Win identical = new Win(blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard);
                assertEquals(win.hashCode(), identical.hashCode());
            }
        );
    }
    
    @Test
    public void hashCodeAlwaysReturnsSameValuePerWinObject() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                int first = win.hashCode();
                int second = win.hashCode();
                assertEquals(first, second);
            }
        );
    }
    
    @Test
    public void winOverridesToString() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                String representation = win.toString();
                String original = getDefaulToString(win);
                assertNotEquals(original, representation);
            }
        );
    }
    
    @Test
    public void toStringAlwaysReturnsSameTextPerObject() {
        testRandomValidWins(
            (blackScore, blackPointsOnBoard, whiteScore, whitePointsOnBoard, win) -> {
                String first = win.toString();
                String second = win.toString();
                assertEquals(first, second);
            }
        );
    }
    
    @Test
    public void winToStringVariesByScoresSoRepresentationsAreEqualOnlyIfWinsAreEqual() {
        testRandomValidWins(
            (a, b, c, d, win1) -> {
                testRandomValidWins(
                    (e, f, g, h, win2) -> {
                        if (win1.equals(win2)) {
                            assertEquals(win1.toString(), win2.toString());
                        } else {
                            assertNotEquals(win1.toString(), win2.toString());
                        }
                    }
                );
            }
        );
    }
}
