// I got frustrated with how I had been handling the unit tests for the Game class.  There are really multiple states of
// Games that need to be considered for each method of the class that make it "interesting" to design good tests to
// prove correct functionality.  I decided to try starting over; this is a copy of my previous attempt.

/*
package com.sadakatsu.go.domain;

import static com.sadakatsu.go.domain.Coordinate.*;
import static com.sadakatsu.go.domain.Pass.PASS;
import static com.sadakatsu.go.domain.intersection.Empty.EMPTY;
import static com.sadakatsu.go.domain.intersection.Stone.*;
import static com.sadakatsu.go.domain.intersection.TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE;
import static com.sadakatsu.go.domain.outcome.CompleteButNotScored.COMPLETE_BUT_NOT_SCORED;
import static com.sadakatsu.go.domain.outcome.Invalidated.INVALIDATED;
import static com.sadakatsu.util.TestHelper.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sadakatsu.go.domain.Game.GameBuilder;
import com.sadakatsu.go.domain.intersection.Intersection;
import com.sadakatsu.go.domain.intersection.Player;
import com.sadakatsu.go.domain.outcome.CompleteButNotScored;
import com.sadakatsu.go.domain.outcome.Invalidated;

public class CopyOfGameTest {
    private static final int[] VALID_DIMENSIONS = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
    
    // These moves come from a game between Yoda Norimoto 9p and Hikosaka Naoto 9p on 2002-05-09.  The game ended with a
    // triple ko in Japanese moves.  It also has multiple captures and situations where intersections are illegal for
    // reasons of self-capture or basic ko rules.
    private static final Move[] TRIPLE_KO_GAME = {
        C17_R04, C04_R04, C16_R17, C04_R17, C06_R03, C03_R06, C11_R03, C16_R14, C17_R12, C14_R16, C17_R16, C17_R15,
        C15_R15, C15_R16, C16_R16, C16_R15, C14_R15, C13_R16, C13_R15, C12_R16, C15_R13, C18_R12, C17_R13, C18_R16,
        C18_R17, C18_R15, C16_R18, C18_R13, C17_R10, C17_R11, C16_R11, C18_R11, C16_R10, C18_R10, C11_R14, C14_R18,
        C18_R09, C19_R18, C18_R18, C19_R17, C10_R16, C10_R17, C09_R17, C10_R18, C19_R10, C19_R11, C19_R13, C17_R14,
        C19_R14, C18_R08, C17_R09, C19_R09, C17_R07, C18_R07, C17_R06, C17_R03, C16_R03, C18_R04, C18_R06, C16_R04,
        C17_R05, C11_R04, C10_R03, C12_R03, C15_R03, C10_R04, C09_R04, C09_R05, C08_R04, C12_R04, C08_R05, C09_R06,
        C19_R16, C19_R15, C17_R19, C05_R03, C06_R02, C17_R02, C15_R19, C15_R18, C16_R02, C14_R05, C15_R05, C14_R04,
        C15_R04, C03_R15, C15_R14, C14_R19, C16_R13, C18_R14, C19_R08, C19_R07, C19_R06, C16_R19, C11_R06, C10_R06,
        C15_R19, C19_R16, C17_R08, C16_R19, C11_R08, C08_R06, C06_R05, C09_R09, C15_R19, C18_R19, C03_R03, C05_R02,
        C05_R04, C05_R05, C06_R04, C04_R03, C04_R05, C03_R05, C05_R06, C03_R04, C07_R09, C08_R08, C11_R10, C09_R16,
        C07_R11, C06_R07, C04_R07, C10_R10, C11_R11, C06_R10, C07_R10, C05_R08, C04_R14, C03_R14, C04_R12, C08_R16,
        C04_R10, C12_R06, C11_R07, C07_R08, C19_R08, C16_R19, C11_R05, C10_R05, C15_R19, C17_R18, C17_R17, C16_R19,
        C03_R17, C03_R16, C15_R19, C15_R17, C19_R10, C16_R19, C19_R19, C19_R09, C15_R19, C18_R19
    };
    private static final int[] MOVES_WITH_CAPTURES = {
        51, 73, 91, 93, 96, 99, 104, 114, 136, 137, 140, 142, 143, 146, 148, 149, 150, 151, 152
    };
    private static final int[] NUMBER_OF_STONES_CAPTURED = { 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
    private static final int[] MOVES_WITH_SELF_CAPTURES = {
        51, 53, 55, 57, 59, 61, 63, 65, 67, 69, 71, 73, 75, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 89, 91, 93,
        95, 97, 99, 101, 103, 105, 107, 109, 111, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125,
        126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146,
        147, 148, 149, 150, 151, 152
    };
    private static final int[] MOVES_WITH_KOS = {
        93, 96, 99, 104, 137, 140, 143, 146, 148, 149, 150, 151, 152
    };
    private static final Coordinate[][] LOCATIONS_FOR_KOS = {
        { C15_R19 },
        { C16_R19 },
        { C15_R19 },
        { C16_R19 },
        { C15_R19 },
        { C16_R19 },
        { C15_R19 },
        { C16_R19 },
        { C19_R09 },
        { C15_R19 },
        { C18_R19 },
        { C19_R10 },
        { C16_R19, C18_R19 }
    };
    private static final Coordinate[][] LOCATIONS_FOR_SELF_CAPTURES = {
        { C19_R10 },
        { C19_R10 },
        { C19_R10 },
        { C19_R10 },
        { C19_R10 },
        { C19_R10 },
        { C19_R10 },
        { C19_R10 },
        { C19_R10 },
        { C19_R10 },
        { C19_R10 },
        { C19_R10, C19_R16 },
        { C19_R10, C19_R16 },
        { C19_R10, C19_R16 },
        { C16_R19 },
        { C19_R10, C19_R16 },
        { C16_R19 },
        { C19_R10, C19_R16 },
        { C16_R19 },
        { C19_R10, C19_R16 },
        { C16_R19 },
        { C19_R10, C19_R16 },
        { C16_R19 },
        { C19_R10, C19_R16 },
        { C19_R10, C19_R12, C19_R16 },
        { C19_R08, C19_R10, C19_R12, C19_R16 },
        { C19_R08, C19_R10, C19_R12, C19_R16 },
        { C19_R08, C19_R10, C19_R12, C19_R16 },
        { C19_R08, C19_R10, C19_R12 },
        { C19_R10, C19_R12 },
        { C19_R10, C19_R12 },
        { C19_R10, C19_R12 },
        { C19_R10, C19_R12 },
        { C19_R10, C19_R12 },
        { C19_R10, C19_R12 },
        { C19_R10, C19_R12 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R10, C19_R12 },
        { C05_R05 },
        { C19_R12 },
        { C05_R05 },
        { C19_R12 },
        { C05_R05 },
        { C19_R12 },
        { C05_R05, C17_R18 },
        { C19_R12 },
        { C05_R05 },
        { C19_R12 },
        { C05_R05, C17_R18 },
        { C19_R12 },
        { C05_R05, C17_R18 },
        { C17_R18, C19_R12 },
        { C05_R05, C17_R18 },
        { C19_R12 },
        { C05_R05, C17_R18 }
    };
    
    
    private static class GameDelta {
        int newCapturesByBlack;
        int newCapturesByWhite;
        List<BoardDifference> positionDifferences;
        List<BoardDifference> stateDifferences;
        
        GameDelta( Game previous, Game current ) {
            newCapturesByBlack = current.getCapturesFor(BLACK) - previous.getCapturesFor(BLACK);
            newCapturesByWhite = current.getCapturesFor(WHITE) - previous.getCapturesFor(WHITE);
            
            positionDifferences = new ArrayList<>();
            stateDifferences = new ArrayList<>();
            for (Coordinate coordinate : Coordinate.iterateOverBoard(previous.getDimension())) {
                Intersection previousValue = previous.get(coordinate);
                Intersection currentValue = current.get(coordinate);
                if (previousValue != currentValue) {
                    BoardDifference difference = new BoardDifference(coordinate, previousValue, currentValue);
                    stateDifferences.add(difference);
                    if (!previousValue.countsAsLiberty() || !currentValue.countsAsLiberty()) {
                        positionDifferences.add(difference);
                    }
                }
            }
        }
    }
    
    private static class BoardDifference {
        final Coordinate coordinate;
        final Intersection previousValue;
        final Intersection currentValue;
        
        BoardDifference( Coordinate coordinate, Intersection previousValue, Intersection currentValue ) {
            this.coordinate = coordinate;
            this.currentValue = currentValue;
            this.previousValue = previousValue;
        }
    }
    
    //==================================================================================================================
    // GameBuilderTest captured nearly all the information that needs to be validated for a new Game.  All that is
    // missing is verification that the constructor correctly distinguishes between empty playable intersections and
    // intersections that are unplayable due to self-capture.
    //==================================================================================================================
    @Test
    public void allIntersectionsAreEmptyOnNewBoardWithNoHandicapStones() {
        GameBuilder builder = Game.newBuilder();
        Game game = builder.build();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            assertEquals(EMPTY, game.get(coordinate));
        }
    }
    
    @Test
    public void gameConstructorDifferentiatesBetweenPlayableAndUnplayableIntersections() {
        // This test is simpler than its code makes it look.  Basically, the 19x19 space is iterated over, placing just
        // enough handicap stones to guarantee that the current coordinate is temporarily unplayable.  This takes two
        // stones in the corners, three on the sides, and four elsewhere.  At the 2-2, 2-18, 18-2, and 18-18 points,
        // this creates an additional unplayable point since two of the stones also surround a corner.  This code then
        // develops its expectation for what should be found at every Coordinate given the GameBuilder state, then tests
        // that expectation.  Thus, the Game class can be proven by induction that it properly checks for zero to many
        // unplayable locations at the start of a game.
        for (Coordinate unplayable : Coordinate.iterateOverBoard()) {
            Iterable<Coordinate> neighbors = unplayable.getNeighbors();
            Set<Coordinate> handicapStones = Sets.newHashSet(neighbors);
            
            GameBuilder builder = Game.newBuilder();
            builder.setHandicapStones(handicapStones);
            Game game = builder.build();
            
            for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
                Intersection expected;
                
                if (coordinate == unplayable) {
                    expected = TEMPORARILY_UNPLAYABLE;
                } else if (handicapStones.contains(coordinate)) {
                    expected = BLACK;
                } else {
                    Iterable<Coordinate> currentNeighborsIterable = coordinate.getNeighbors();
                    Set<Coordinate> currentNeighborsSet = Sets.newHashSet(currentNeighborsIterable);
                    currentNeighborsSet.removeAll(handicapStones);
                    
                    if (currentNeighborsSet.isEmpty()) {
                        expected = TEMPORARILY_UNPLAYABLE;
                    } else {
                        expected = EMPTY;
                    }
                }
                
                Intersection actual = game.get(coordinate);
                assertEquals(expected, actual);
            }
        }
    }
    
    @Test
    public void gameConstructorCorrectlyMakesSingleEmptyIntersectionPlayableWhenTheRestOfTheBoardIsFilledWithHandicapStones() {
        // This test ensures that the game's starting position can correctly determine capturable positions.
        Iterable<Coordinate> coordinateIterable = Coordinate.iterateOverBoard();
        Set<Coordinate> coordinateSet = Sets.newHashSet(coordinateIterable);
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Set<Coordinate> handicapStones = new HashSet<>(coordinateSet);
            handicapStones.remove(coordinate);
            
            GameBuilder builder = Game.newBuilder();
            builder.setHandicapStones(handicapStones);
            Game game = builder.build();
            for (Coordinate current : Coordinate.iterateOverBoard()) {
                Intersection expected = current == coordinate ? EMPTY : BLACK;
                Intersection actual = game.get(current);
                assertEquals(expected, actual);
            }
        }
    }
    
    @Test
    public void a1x1GameHasNoPlayableIntersections() {
        GameBuilder builder = Game.newBuilder(1);
        Game game = builder.build();
        for (Coordinate coordinate : Coordinate.iterateOverBoard(1)) {
            Intersection intersection = game.get(coordinate);
            assertEquals(TEMPORARILY_UNPLAYABLE, intersection);
        }
    }
    
    //==================================================================================================================
    // It is good to be able to create Games, but the point of the Game is to be able to play.  However, if AIs are to
    // be able to use this class to read out move trees, a Game instance needs to be immutable.
    //
    // The move() method satisfies both the need to play a Game out and to make each Game immutable.  It accepts a Move
    // as an argument (either a Coordinate at which to place a stone or Pass.PASS to allow the next player to make a
    // move).  Its execution paths are as follows:
    // 1) If the argument is null, a Coordinate not on the board, or a Coordinate that is not a legal move, it throws an
    //    IllegalArgumentException.
    // 2) If the argument is a legal move Coordinate, it creates a new Game that represents an updated state of this
    //    Game after resolving this move.  This new Game will have an incremented number of moves player, the next
    //    Player set as the current player, an updated board position reflecting the added stone, any stones removed for
    //    the capture, and the playable (empty) and unplayable (temporarily unplayable) intersections properly set for
    //    the current player based upon the rules of self-capture and positional super ko.*  If the player captured
    //    stones, his capture count is incremented by the number of stones removed from the board.  The previous
    //    player's capture count, the compensation, the handicap stones' original positions, and the Outcome will be the
    //    same between the two states.
    // 3) If the argument is a Pass, and the previous move was not a Pass, then this method creates and returns a new
    //    Game that reflects the current Game's state after a Pass.  The new Game's move counter will be incremented and
    //    the current player switched.  No stones will be moved, but the playable and unplayable intersections will be
    //    updated for the current player based upon self-capture and positional super ko rules.  The previous player's
    //    capture count, the compensation, the handicap stones' original positions, and the Outcome will be the same
    //    between the two states.
    // 4) If the argument is a Pass, and the previous move was also a Pass, then this method creates and returns a new
    //    Game that reflects that the Game is completed.  The Game is placed in a state where no further moves are
    //    permitted and where it is waiting to receive the Set of dead Groups both players agree upon before counting
    //    the final score.  The "current player" no longer makes sense, so the returned Game will throw an
    //    IllegalStateException if queried for its current player.  The move counter, the player capture counts,
    //    compensation, handicap stones' original positions, and the board will be identical between the two states.
    //
    // * Currently, only Chinese rules are supported.  Some day, I would like to extend this to include Japanese rules,
    //   Logical Japanese Rules of Go, AGA, BGA, New Zealand, Ing, and Tromp-Taylor.  For now, I need to get SOMETHING
    //   done so I can stop stalling and work toward my planned AI.
    //==================================================================================================================
    @Test
    public void gameMoveThrowsAnExceptionForNullArgumentAndDoesNotChangeState() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            Game expected = builder.build();
            
            try {
                Game wrong = game.move(null);
                failForReturn("game.move", "IllegalArgumentException", wrong, (Object) null);
            } catch (IllegalArgumentException e) {
                assertEquals(expected, game);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("game.move", "IllegalArgumentException", t, (Object) null);
            }
        }
    }
    
    private GameBuilder createRandomBuilder( int dimension ) {
        GameBuilder builder = Game.newBuilder(dimension);
        
        Iterable<Coordinate> iterable = Coordinate.iterateOverBoard(dimension);
        List<Coordinate> coordinates = Lists.newArrayList(iterable);
        Collections.shuffle(coordinates);
        for (int i = 0, max = dimension * dimension / 4; i < max; ++i) {
            Coordinate stone = coordinates.get(i);
            builder.addHandicapStone(stone);
        }
        
        return builder;
    }
    
    @Test
    public void gameMoveThrowsAnExceptionForOffBoardCoordinateArgumentAndDoesNotChangeState() {
        for (int dimension : VALID_DIMENSIONS) {
            if (dimension == 19) {
                continue;
            }
            
            Iterable<Coordinate> offBoardIterable = Coordinate.iterateOverBoard();
            Set<Coordinate> offBoardSet = Sets.newHashSet(offBoardIterable);
            if (dimension > 1) {
                Iterable<Coordinate> iterable = Coordinate.iterateOverBoard(dimension);
                List<Coordinate> coordinates = Lists.newArrayList(iterable);
                offBoardSet.removeAll(coordinates);
            }
            
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            Game expected = builder.build();
            
            for (Coordinate invalid : offBoardSet) {
                try {
                    Game wrong = game.move(invalid);
                    failForReturn("game.move", "IllegalArgumentException", wrong, invalid);
                } catch (IllegalArgumentException e) {
                    assertEquals(expected, game);
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable t) {
                    failForWrongThrowable("game.move", "IllegalArgumentException", t, invalid);
                }
            }
        }
    }
    
    @Test
    public void gameMoveThrowsAnExceptionForUnplayableCoordinateArgumentAndDoesNotChangeState() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            Game expected = builder.build();
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                Intersection state = game.get(coordinate);
                if (EMPTY != state) {
                    try {
                        Game wrong = game.move(coordinate);
                        failForReturn("game.move", "IllegalArgumentException", wrong, coordinate);
                    } catch (IllegalArgumentException e) {
                        assertEquals(expected, game);
                    } catch (AssertionError e) {
                        throw e;
                    } catch (Throwable t) {
                        failForWrongThrowable("game.move", "IllegalArgumentException", t, coordinate);
                    }
                }
            }
        }
    }
    
    @Test
    public void gameMoveThrowsAnExceptionForAnUnknownMoveClassAndDoesNotChangeState() {
        Move fake = new Move() {};
        
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            Game expected = builder.build();
            
            try {
                Game wrong = game.move(fake);
                failForReturn("game.move", "IllegalArgumentException", wrong, fake);
            } catch (IllegalArgumentException e) {
                assertEquals(expected, game);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("game.move", "IllegalArgumentException", t, fake);
            }
        }
    }
    
    @Test
    public void gameMoveAcceptsPassAndReturnsNewGameWithoutModifyingItself() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            Game original = builder.build();
            
            Game afterPass = game.move(PASS);
            
            assertNotNull(afterPass);
            assertNotEquals(game, afterPass);
            assertEquals(original, game);
            
            assertEquals(game.getCapturesFor(BLACK), afterPass.getCapturesFor(BLACK));
            assertEquals(game.getCapturesFor(WHITE), afterPass.getCapturesFor(WHITE));
            assertEquals(game.getCompensation(), afterPass.getCompensation(), 0.);
            assertNotEquals(game.getCurrentPlayer(), afterPass.getCurrentPlayer());
            assertEquals(game.getDimension(), afterPass.getDimension());
            assertEquals(game.getHandicap(), afterPass.getHandicap());
            assertEquals(game.getHandicapStonePlacements(), afterPass.getHandicapStonePlacements());
            assertEquals(game.getMovesPlayed() + 1, afterPass.getMovesPlayed());
            assertEquals(game.getOutcome(), afterPass.getOutcome());
            assertEquals(PASS, afterPass.getPreviousMove());
            assertEquals(game, afterPass.getPreviousState());
            assertTrue(afterPass.wouldPassEndGame());
        }
    }
    
    @Test
    public void twoPassesInARowPreparesAGameForScoring() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game start = builder.build();
            Game almostDone = start.move(PASS);
            Game readyForScoring = almostDone.move(PASS);
            assertEquals(COMPLETE_BUT_NOT_SCORED, readyForScoring.getOutcome());
        }
    }
    
    @Test
    public void gameMoveAcceptsPlayableCoordinateArgumentAndReturnsNewGameWithoutModifyingItself() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            Game original = builder.build();
            
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                Intersection state = game.get(coordinate);
                if (EMPTY == state) {
                    Game afterMove = game.move(coordinate);
                    
                    assertNotNull(afterMove);
                    assertNotEquals(game, afterMove);
                    assertEquals(original, game);
                    
                    assertEquals(game.getCompensation(), afterMove.getCompensation(), 0.);
                    assertNotEquals(game.getCurrentPlayer(), afterMove.getCurrentPlayer());
                    assertEquals(game.getDimension(), afterMove.getDimension());
                    assertEquals(game.getHandicap(), afterMove.getHandicap());
                    assertEquals(game.getHandicapStonePlacements(), afterMove.getHandicapStonePlacements());
                    assertEquals(game.getMovesPlayed() + 1, afterMove.getMovesPlayed());
                    assertEquals(game.getOutcome(), afterMove.getOutcome());
                    assertEquals(coordinate, afterMove.getPreviousMove());
                    assertEquals(game, afterMove.getPreviousState());
                    assertEquals(game.getCurrentPlayer(), afterMove.get(coordinate));
                }
            }
        }
    }
    
    @Test
    public void playingMovesForWhichThereAreNoCapturesOnlyAddsThatStoneToThePosition() {
        GameBuilder builder = Game.newBuilder();
        builder.setCompensation(5.5);
        Game game = builder.build();
        
        for (int i = 0; i < 51; ++i) {
            Move move = TRIPLE_KO_GAME[i];
            Game next = game.move(move);
            
            GameDelta delta = new GameDelta(game, next);
            assertEquals(0, delta.newCapturesByBlack);
            assertEquals(0, delta.newCapturesByWhite);
            assertEquals(1, delta.positionDifferences.size());
            
            BoardDifference difference = delta.positionDifferences.get(0);
            assertEquals(move, difference.coordinate);
            assertEquals(EMPTY, difference.previousValue);
            assertEquals(game.getCurrentPlayer(), difference.currentValue);
            
            game = next;
        }
    }
    
    @Test
    public void playingMovesCorrectlyPerformsCaptures() {
        GameBuilder builder = Game.newBuilder();
        builder.setCompensation(5.5);
        Game game = builder.build();
        
        for (int i = 0, captureIndex = 0; i < 153; ++i) {
            Move move = TRIPLE_KO_GAME[i];
            Game next = game.move(move);
            
            if (i == MOVES_WITH_CAPTURES[captureIndex]) {
                GameDelta delta = new GameDelta(game, next);
                if (game.getCurrentPlayer() == BLACK) {
                    assertEquals(NUMBER_OF_STONES_CAPTURED[captureIndex], delta.newCapturesByBlack);
                    assertEquals(0, delta.newCapturesByWhite);
                } else {
                    assertEquals(0, delta.newCapturesByBlack);
                    assertEquals(NUMBER_OF_STONES_CAPTURED[captureIndex], delta.newCapturesByWhite);
                }
                assertEquals(NUMBER_OF_STONES_CAPTURED[captureIndex] + 1, delta.positionDifferences.size());
                
                boolean foundMove = false;
                for (BoardDifference difference : delta.positionDifferences) {
                    if (difference.coordinate == move) {
                        foundMove = true;
                        assertEquals(EMPTY, difference.previousValue);
                        assertEquals(game.getCurrentPlayer(), difference.currentValue);
                    } else {
                        assertEquals(next.getCurrentPlayer(), difference.previousValue);
                        assertTrue(difference.currentValue.countsAsLiberty());
                    }
                }
                assertTrue("Could not find move " + (i + 1) + " in the resulting board.", foundMove);
                
                ++captureIndex;
            }
            
            game = next;
        }
    }
    
    @Test
    public void playingMovesCorrectlyDistinguishesBetweenSelfCapturesAndLegalMoves() {
        GameBuilder builder = Game.newBuilder();
        builder.setCompensation(5.5);
        Game game = builder.build();
        
        for (int i = 0, koIndex = 0, selfCaptureIndex = 0; i < 153; ++i) {
            Move move = TRIPLE_KO_GAME[i];
            Game next = game.move(move);
            
            boolean isMoveWithKo = i == MOVES_WITH_KOS[koIndex];
            boolean isMoveWithSelfCapture = i == MOVES_WITH_SELF_CAPTURES[selfCaptureIndex];
            
            for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
                Intersection value = next.get(coordinate);
                if (!value.countsAsLiberty()) {
                    continue;
                }
                
                if (isMoveWithKo) {
                    boolean isKo = false;
                    for (Coordinate ko : LOCATIONS_FOR_KOS[koIndex]) {
                        if (coordinate == ko) {
                            isKo = true;
                            break;
                        }
                    }
                    if (isKo) {
                        continue;
                    }
                }
                
                Intersection expected = EMPTY;
                if (isMoveWithSelfCapture) {
                    for (Coordinate selfCapture : LOCATIONS_FOR_SELF_CAPTURES[selfCaptureIndex]) {
                        if (coordinate == selfCapture) {
                            expected = TEMPORARILY_UNPLAYABLE;
                            break;
                        }
                    }
                }
                
                assertEquals(
                    "On move " + (i + 1) + ", " + coordinate + " should be " + expected + " but is " + value + ".",
                    expected,
                    value
                );
            }
            
            if (isMoveWithKo) {
                ++koIndex;
            }
            if (isMoveWithSelfCapture) {
                ++selfCaptureIndex;
            }
            
            game = next;
        }
    }
    
    @Test
    public void playingMovesCorrectlyDistinguishesBetweenKosAndLegalMoves() {
        GameBuilder builder = Game.newBuilder();
        builder.setCompensation(5.5);
        Game game = builder.build();
        
        for (int i = 0, koIndex = 0, selfCaptureIndex = 0; i < 153; ++i) {
            Move move = TRIPLE_KO_GAME[i];
            Game next = game.move(move);
            
            boolean isMoveWithKo = i == MOVES_WITH_KOS[koIndex];
            boolean isMoveWithSelfCapture = i == MOVES_WITH_SELF_CAPTURES[selfCaptureIndex];
            
            for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
                Intersection value = next.get(coordinate);
                if (!value.countsAsLiberty()) {
                    continue;
                }
                
                if (isMoveWithSelfCapture) {
                    boolean isSelfCapture = false;
                    for (Coordinate selfCapture : LOCATIONS_FOR_SELF_CAPTURES[selfCaptureIndex]) {
                        if (coordinate == selfCapture) {
                            isSelfCapture = true;
                            break;
                        }
                    }
                    if (isSelfCapture) {
                        continue;
                    }
                }
                
                Intersection expected = EMPTY;
                if (isMoveWithKo) {
                    for (Coordinate ko : LOCATIONS_FOR_KOS[koIndex]) {
                        if (coordinate == ko) {
                            expected = TEMPORARILY_UNPLAYABLE;
                            break;
                        }
                    }
                }
                
                assertEquals(
                    "On move " + (i + 1) + ", " + coordinate + " should be " + expected + " but is " + value + ".",
                    expected,
                    value
                );
            }
            
            if (isMoveWithKo) {
                ++koIndex;
            }
            if (isMoveWithSelfCapture) {
                ++selfCaptureIndex;
            }
            
            game = next;
        }
    }
    
    //==================================================================================================================
    // For convenience, Game should expose a pass() method that performs identically to move(PASS).  Thus, it has the
    // following performance:
    // 1) If called on a Game in the IN_PROGRESS state when the previous move was a PASS, it returns a Game with an
    //    identical position with the move counter incremented and the Outcome set to COMPLETE_BUT_NOT_SCORED.
    // 2) If called on a Game in the IN_PROGRESS state when the previous move was not a PASS, it returns a Game with an
    //    identical position with the TEMPORARILY_UNPLAYABLE locations updated for the new current player and the move
    //    counter incremented.
    // 3) Otherwise, pass() throws an IllegalStateException because the Game is over.
    //==================================================================================================================
    @Test
    public void passMethodIsIdenticalToPassingPassInstanceToMove() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            for (int i = 0; i < 2; ++i) {
                Game move = game.move(PASS);
                Game pass = game.pass();
                assertEquals(pass, move);
                game = move;
            }
        }
    }
    
    @Test
    public void twoPassesInARowProduceAGameThatIsCompleteButNotScored() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            Game passed = game.pass();
            Game complete = passed.pass();
            assertNotEquals(game, passed);
            assertNotEquals(passed, complete);
            assertNotEquals(game, complete);
            
            assertEquals(passed.getAllGroups(), complete.getAllGroups());
            assertEquals(passed.getCapturesFor(BLACK), complete.getCapturesFor(BLACK));
            assertEquals(passed.getCapturesFor(WHITE), complete.getCapturesFor(WHITE));
            assertEquals(expected, complete.);
            
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                assertEquals(passed.get(coordinate), complete.get(coordinate));
            }
        }
    }
    
    @Test
    public void aCompleteButNotScoredGameThrowsAnExceptionOnPassAndDoesNotModifyItself() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build().pass().pass();
            Game expected = builder.build().pass().pass();
            
            try {
                Game wrong = game.pass();
                failForReturn("game.pass", "IllegalStateException", wrong);
            } catch (IllegalStateException e) {
                assertEquals(expected, game);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("game.pass", "IllegalStateException", t);
            }
        }
    }
    
    @Test
    public void anInvalidatedGameThrowsAnExceptionOnPassAndDoesNotModifyItself() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build().invalidate();
            Game expected = builder.build().invalidate();
            
            try {
                Game wrong = game.pass();
                failForReturn("game.pass", "IllegalStateException", wrong);
            } catch (IllegalStateException e) {
                assertEquals(expected, game);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("game.pass", "IllegalStateException", t);
            }
        }
    }
    
    @Test
    public void aWonGameThrowsAnExceptionOnPassAndDoesNotModifyItself() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build().pass().pass().score();
            Game expected = builder.build().pass().pass().score();
            
            try {
                Game wrong = game.pass();
                failForReturn("game.pass", "IllegalStateException", wrong);
            } catch (IllegalStateException e) {
                assertEquals(expected, game);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("game.pass", "IllegalStateException", t);
            }
        }
    }
    
    @Test
    public void aDrawnGameThrowsAnExceptionOnPassAndDoesNotModifyItself() {
        for (int dimension : VALID_DIMENSIONS) {
            if (dimension == 1) {
                continue;
            }
            
            GameBuilder builder = Game.newBuilder(dimension);
            builder.setCompensation(0.);
            Game game = builder.build().move(C01_R01).move(C02_R01).pass().pass().score();
            Game expected = builder.build().move(C01_R01).move(C02_R01).pass().pass().score();
            
            try {
                Game wrong = game.pass();
                failForReturn("game.pass", "IllegalStateException", wrong);
            } catch (IllegalStateException e) {
                assertEquals(expected, game);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("game.pass", "IllegalStateException", t);
            }
        }
    }
    
    //==================================================================================================================
    // A Game is only over when both players agree about the life, death, and distribution of territory for the whole
    // board.  Sometimes, the two players may disagree when they start counting.  The resume() method allows the players
    // to resume a Game when such a disagreement occurs. It performs precisely as follows:
    // 1) If called on a Game in the COMPLETE_BUT_NOT_SCORED state, it returns the nearest Game in its history that is
    //    in the IN_PROGRESS state.
    // 2) Otherwise, it throws an IllegalStateException.
    //==================================================================================================================
    @Test
    public void aCompleteButNotScoredGameCanBeResumed() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            Game firstPass = game.pass();
            Game complete = firstPass.pass();
            Game resumed = complete.resume();
            assertEquals(game, resumed);
        }
    }
    
    @Test
    public void anInProgressGameThrowsAnExceptionForACallToResumeAndDoesNotChangeAnything() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build();
            Game expected = builder.build();
            try {
                Game wrong = game.resume();
                failForReturn("game.resume", "IllegalStateException", wrong);
            } catch (IllegalStateException e) {
                assertEquals(expected, game);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("game.resume", "IllegalStateException", t);
            }
        }
    }
    
    @Test
    public void anInvalidatedGameThrowsAnExceptionForACallToResumeAndDoesNotChangeAnything() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build().invalidate();
            Game expected = builder.build().invalidate();
            try {
                Game wrong = game.resume();
                failForReturn("game.resume", "IllegalStateException", wrong);
            } catch (IllegalStateException e) {
                assertEquals(expected, game);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("game.resume", "IllegalStateException", t);
            }
        }
    }
    
    @Test
    public void aWonGameThrowsAnExceptionForACallToResumeAndDoesNotChangeAnything() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build().pass().pass().score();
            Game expected = builder.build().pass().pass().score();
            try {
                Game wrong = game.resume();
                failForReturn("game.resume", "IllegalStateException", wrong);
            } catch (IllegalStateException e) {
                assertEquals(expected, game);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("game.resume", "IllegalStateException", t);
            }
        }
    }
    
    @Test
    public void aDrawnGameThrowsAnExceptionForACallToResumeAndDoesNotChangeAnything() {
        GameBuilder builder = Game.newBuilder();
        builder.setCompensation(0.);
        Game game = builder.build().move(Coordinate.C16_R04).move(Coordinate.C04_R04).pass().pass().score();
        Game expected = builder.build().move(Coordinate.C16_R04).move(Coordinate.C04_R04).pass().pass().score();
        try {
            Game wrong = game.resume();
            failForReturn("game.resume", "IllegalStateException", wrong);
        } catch (IllegalStateException e) {
            assertEquals(expected, game);
        } catch (AssertionError e) {
            throw e;
        } catch (Throwable t) {
            failForWrongThrowable("game.resume", "IllegalStateException", t);
        }
    }
    
    //==================================================================================================================
    // According to some rule sets, a referee may invalidate a match result, requiring that a new Game be played.  This
    // occurs if a hyper ko develops (even under Chinese rules!) or if a kibbitzer makes a comment such that the Outcome
    // of a Game would change.  While invalidation is unlikely to happen with this code base, the invalidate() method is
    // supplied so that this can occur.  The invalidate() method has the following performance:
    // 1) If called on a Game that is IN_PROGRESS, it creates a new Game that is identical to the called Game except
    //    that its Outcome is set to INVALIDATED.  The original Game is not modified.
    // 2) Otherwise, the method throws an IllegalStateException without modifying the Game.
    //==================================================================================================================
    @Test
    public void anInProgressGameCanBeInvalidatedWithoutChangingTheOriginalGame() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build().pass();
            Game expected = builder.build().pass();
            Game invalidated = game.invalidate();
            assertEquals(game, expected);
            assertNotEquals(game, invalidated);
            assertTrue(invalidated.isOver());
            assertFalse(invalidated.wouldPassEndGame());
            assertEquals(game.getCompensation(), invalidated.getCompensation(), 0.);
            assertEquals(game, invalidated.getPreviousState());
            assertEquals(0, invalidated.getCapturesFor(BLACK));
            assertEquals(0, invalidated.getCapturesFor(WHITE));
            assertEquals(dimension, invalidated.getDimension());
            assertEquals(game.getHandicap(), invalidated.getHandicap());
            assertEquals(game.getMovesPlayed(), invalidated.getMovesPlayed());
            assertEquals(game.getPreviousMove(), invalidated.getPreviousMove());
            assertEquals(INVALIDATED, invalidated.getOutcome());
            assertEquals(game.getHandicapStonePlacements(), invalidated.getHandicapStonePlacements());
            assertEquals(game.getGroupsOfStones(), invalidated.getGroupsOfStones());
            assertEquals(game.getAllGroups(), invalidated.getAllGroups());
            assertEquals(Collections.EMPTY_SET, invalidated.getLegalMoves());
            
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                assertEquals(game.get(coordinate), invalidated.get(coordinate));
            }
            
            try {
                Player wrong = invalidated.getCurrentPlayer();
                failForReturn("invalidated.getCurrentPlayer", "IllegalStateException", wrong);
            } catch (IllegalStateException e) {
                assertEquals(expected.invalidate(), invalidated);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("invalidated.getCurrentPlayer", "IllegalStateException", t);
            }
        }
    }
    
    @Test
    public void aCompleteButNotScoredGameThrowsAnExceptionForACallToInvalidateAndDoesNotChangeItself() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = createRandomBuilder(dimension);
            Game game = builder.build().pass().pass();
            Game expected = builder.build().pass().pass();
        }
    }
}
//*/
