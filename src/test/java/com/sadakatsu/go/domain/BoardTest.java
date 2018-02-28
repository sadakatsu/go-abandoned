package com.sadakatsu.go.domain;

import static com.sadakatsu.go.domain.Coordinate.*;
import static com.sadakatsu.go.domain.Pass.PASS;
import static com.sadakatsu.go.domain.intersection.Empty.EMPTY;
import static com.sadakatsu.go.domain.intersection.PermanentlyUnplayable.PERMANENTLY_UNPLAYABLE;
import static com.sadakatsu.go.domain.intersection.Stone.*;
import static com.sadakatsu.go.domain.intersection.TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE;
import static com.sadakatsu.util.TestHelper.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sadakatsu.go.domain.intersection.Intersection;
import com.sadakatsu.go.domain.intersection.TemporarilyUnplayable;

public class BoardTest {
    private static final int[] INVALID_SIZES = { -4, -3, -2, -1, 0, 20, 21, 22, 23, 24 };
    private static final int[] VALID_SIZES = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
    private static final Intersection[] INTERSECTIONS = {
        EMPTY,
        BLACK,
        WHITE,
        TEMPORARILY_UNPLAYABLE,
        PERMANENTLY_UNPLAYABLE
    };
    private static final Intersection[] INVALID_INTERSECTIONS = {
        null,
        new Intersection() {
            @Override
            public boolean isPlayable() {
                return false;
            }
            
            @Override
            public boolean countsAsLiberty() {
                return false;
            }
        }
    };
    
    @Test
    public void defaultConstructorReturnsSizeNineteenBoard() {
        Board board = new Board();
        if (board.getDimension() != 19) {
            String message = String.format(
                "The default constructor should instantiate a 19x19 board, but getDimension() returned %d.",
                board.getDimension()
            );
            fail(message);
        }
    }
    
    @Test
    public void defaultConstructorReturnsEmptyBoard() {
        Board board = new Board();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Intersection value = board.get(coordinate);
            if (!EMPTY.equals(value)) {
                String message = String.format(
                    "The default constructor should instantiate an empty board, but get(%s) returned %s.",
                    coordinate,
                    value
                );
                fail(message);
            }
        }
    }
    
    @Test
    public void attemptingToInstantiateABoardWithAnInvalidDimensionThrowsAnException() {
        for (int invalidSize : INVALID_SIZES) {
            try {
                Board wrong = new Board(invalidSize);
                String message = String.format(
                    "new Board(%d) should have thrown an IllegalArgumentException, but instead it returned %s.",
                    invalidSize,
                    wrong
                );
                fail(message);
            } catch (IllegalArgumentException e) {
                // success
            } catch (Exception e) {
                String message = String.format(
                    "new Board(%d) should have thrown an IllegalArgumentException, but instead it threw %s: %s\n%s",
                    invalidSize,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())
                );
                fail(message);
            }
        }
    }
    
    @Test
    public void canInstantiateABoardOfVariousSizes() {
        for (int dimension : VALID_SIZES) {
            Board board = new Board(dimension);
            if (board.getDimension() != dimension) {
                String message = String.format(
                    "new Board(%1$d) should instantiate a %1$dx%1$d board, but getDimension() returned %d.",
                    dimension,
                    board.getDimension()
                );
                fail(message);
            }
        }
    }
    
    @Test
    public void newlyInstantiatedBoardsWithCustomSizesAreEmpty() {
        for (int dimension : VALID_SIZES) {
            Board board = new Board(dimension);
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                Intersection value = board.get(coordinate);
                if (!EMPTY.equals(value)) {
                    String message = String.format(
                        "new Board(%d) should instantiate an empty board, but get(%s) returned %s.",
                        dimension,
                        coordinate,
                        value
                    );
                    fail(message);
                }
            }
        }
    }
    
    @Test
    public void getThrowsExceptionWhenPassedInvalidCoordinateForBoard() {
        for (int dimension : VALID_SIZES) {
            Set<Coordinate> invalids = getInvalidCoordinatesFor(dimension);
            Board board = new Board(dimension);
            for (Coordinate invalid : invalids) {
                try {
                    Intersection wrong = board.get(invalid);
                    String message = String.format(
                        "A board of dimension %d should throw an IllegalArgumentException when get(%s) is called, but instead it returned %s.",
                        dimension,
                        invalid,
                        wrong
                    );
                    fail(message);
                } catch (IllegalArgumentException e) {
                    // success
                } catch (Exception e) {
                    String message = String.format(
                        "A board of dimension %d should throw an IllegalArgumentException when get(%s) is called, but instead it threw %s: %s\n%s.",
                        dimension,
                        invalid,
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        Arrays.toString(e.getStackTrace())
                    );
                    fail(message);
                }
            }
        }
    }
    
    private Set<Coordinate> getInvalidCoordinatesFor( int dimension ) {
        Set<Coordinate> invalid = null;
        
        Iterable<Coordinate> all = Coordinate.iterateOverBoard();
        invalid = Sets.newHashSet(all);
        
        Iterable<Coordinate> valid = Coordinate.iterateOverBoard(dimension);
        Set<Coordinate> toRemove = Sets.newHashSet(valid);
        invalid.removeAll(toRemove);
        
        invalid.add(null);
        
        return invalid;
    }
    
    @Test
    public void setThrowsExceptionAndDoesNotChangeBoardWhenPassedInvalidCoordinateForBoard() {
        for (int dimension : VALID_SIZES) {
            Board expected = new Board(dimension);
            for (Coordinate invalid : getInvalidCoordinatesFor(dimension)) {
                for (Intersection intersection : INTERSECTIONS) {
                    Board victim = new Board(dimension);
                    try {
                        victim.set(invalid, intersection);
                        String message = String.format(
                            "Calling board.set(%s, %s) on a board with dimension %d should throw an IllegalArgumentException, but instead it returned and resulted in:\n%s",
                            invalid,
                            intersection,
                            dimension,
                            victim
                        );
                        fail(message);
                    } catch (IllegalArgumentException e) {
                        if (!expected.equals(victim)) {
                            String message = String.format(
                                "Calling board.set(%s, %s) on a board with dimenion %d threw an IllegalArgumentException, but it modified the Board anyway:\n%s",
                                invalid,
                                intersection,
                                dimension,
                                victim
                            );
                            fail(message);
                        }
                    } catch (Exception e) {
                        StringBuilder builder = new StringBuilder("Calling board.set(");
                        builder.append(invalid);
                        builder.append(", ");
                        builder.append(intersection);
                        builder.append(
                            ") should throw an IllegalArgumentException without changing the board.  However, it threw "
                        );
                        builder.append(e.getClass().getSimpleName());
                        if (!expected.equals(victim)) {
                            builder.append(" and modified the Board to be \n");
                            builder.append(victim);
                        } else {
                            builder.append(".\n");
                        }
                        builder.append("MESSAGE: ");
                        builder.append(e.getMessage());
                        builder.append("\nTRACE: ");
                        builder.append(Arrays.toString(e.getStackTrace()));
                        String message = builder.toString();
                        fail(message);
                    }
                }
            }
        }
    }
    
    @Test
    public void setThrowsExceptionAndDoesNotChangeBoardWhenPassedInvalidIntersection() {
        for (int dimension : VALID_SIZES) {
            Board expected = new Board(dimension);
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                for (Intersection invalid : INVALID_INTERSECTIONS) {
                    Board victim = new Board(dimension);
                    try {
                        victim.set(coordinate, invalid);
                        String message = String.format(
                            "Calling board.set(%s, %s) on a board with dimension %d should throw an IllegalArgumentException, but instead it returned and resulted in:\n%s",
                            coordinate,
                            invalid,
                            dimension,
                            victim
                        );
                        fail(message);
                    } catch (IllegalArgumentException e) {
                        if (!expected.equals(victim)) {
                            if (!expected.equals(victim)) {
                                String message = String.format(
                                    "Calling board.set(%s, %s) on a board with dimenion %d threw an IllegalArgumentException, but it modified the Board anyway:\n%s",
                                    coordinate,
                                    invalid,
                                    dimension,
                                    victim
                                );
                                fail(message);
                            }
                        }
                    } catch (Exception e) {
                        StringBuilder builder = new StringBuilder("Calling board.set(");
                        builder.append(coordinate);
                        builder.append(", ");
                        builder.append(invalid);
                        builder.append(
                            ") should throw an IllegalArgumentException without changing the board.  However, it threw "
                        );
                        builder.append(e.getClass().getSimpleName());
                        if (!expected.equals(victim)) {
                            builder.append(" and modified the Board to be \n");
                            builder.append(victim);
                        } else {
                            builder.append(".\n");
                        }
                        builder.append("MESSAGE: ");
                        builder.append(e.getMessage());
                        builder.append("\nTRACE: ");
                        builder.append(Arrays.toString(e.getStackTrace()));
                        String message = builder.toString();
                        fail(message);
                    }
                }
            }
        }
    }
    
    @Test
    public void setModifiesBoardStateAccordingToArguments() {
        for (int dimension : VALID_SIZES) {
            List<Coordinate> validCoordinates = Lists.newArrayList(Coordinate.iterateOverBoard(dimension));
            
            Board previous = new Board(dimension);
            Board current = new Board(dimension);
            Coordinate coordinate = null;
            Intersection intersection = null;
            for (int tries = 0; tries < dimension * 2; ++tries) {
                if (coordinate != null) {
                    previous.set(coordinate, intersection);
                    if (!previous.equals(current)) {
                        String message = String.format(
                            "An identical set operation on identical Boards resulted in different Board states.\nMove:%s @ %s\nFirst Board:\n%sSecondBoard:\n%s",
                            intersection,
                            coordinate,
                            previous,
                            current
                        );
                        fail(message);
                    }
                }
                
                do {
                    coordinate = getRandomCoordinate(validCoordinates);
                    intersection = getRandomIntersection();
                } while (current.get(coordinate) == intersection);
                
                current.set(coordinate, intersection);
                if (previous.equals(current)) {
                    String message = String.format(
                        "Calling set(%s, %s) on one of two identical boards did not result in their being different:\nFirst Board:\n%sSecondBoard:\n%s",
                        coordinate,
                        intersection,
                        current,
                        previous
                    );
                    fail(message);
                }
            }
        }
    }
    
    private Coordinate getRandomCoordinate( List<Coordinate> coordinates ) {
        Random random = ThreadLocalRandom.current();
        int length = coordinates.size();
        int index = random.nextInt(length);
        return coordinates.get(index);
    }
    
    private Intersection getRandomIntersection() {
        Random random = ThreadLocalRandom.current();
        int length = INTERSECTIONS.length;
        int index = random.nextInt(length);
        return INTERSECTIONS[index];
    }
    
    @Test
    public void aBoardIsEqualToItself() {
        for (int dimension : VALID_SIZES) {
            Board board = buildRandomBoard(dimension);
            assertTrue(board.equals(board));
        }
    }
    
    private Board buildRandomBoard( int dimension ) {
        Board board = new Board(dimension);
        List<Coordinate> validCoordinates = Lists.newArrayList(Coordinate.iterateOverBoard(dimension));
        
        Coordinate coordinate = null;
        Intersection intersection = null;
        for (int tries = 0; tries < dimension * 2; ++tries) {
            do {
                coordinate = getRandomCoordinate(validCoordinates);
                intersection = getRandomIntersection();
            } while (board.get(coordinate) == intersection);
            
            board.set(coordinate, intersection);
        }
        
        return board;
    }
    
    @Test
    public void aBoardIsNeverEqualToNull() {
        for (int dimension : VALID_SIZES) {
            Board board = buildRandomBoard(dimension);
            assertFalse(board.equals(null));
        }
    }
    
    @Test
    public void aBoardIsOnlyEqualToBoards() {
        for (int dimension : VALID_SIZES) {
            Board board = buildRandomBoard(dimension);
            assertFalse(board.equals(board.toString()));
        }
    }
    
    @Test
    public void boardsMustBeTheSameSizeToBeEqual() {
        for (int i = 1; i <= 18; ++i) {
            Board first = new Board(i);
            for (int j = i + 1; j <= 19; ++j) {
                Board second = new Board(j);
                assertFalse(first.equals(second) || second.equals(first));
            }
        }
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void copyConstructorThrowsExceptionForNullSource() {
        new Board(null);
    }
    
    @Test
    public void copyConstructorGeneratesValidCopy() {
        for (int dimension : VALID_SIZES) {
            Board original = buildRandomBoard(dimension);
            Board copy = new Board(original);
            assertEquals(dimension, copy.getDimension());
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                assertEquals(original.get(coordinate), copy.get(coordinate));
            }
        }
    }
    
    @Test
    public void isSamePositionAsReturnsTrueWhenAllIntersectionsBetweenTwoBoardsHaveTheSameColor() {
        for (int dimension : VALID_SIZES) {
            Board first = new Board(dimension);
            Board second = new Board(dimension);
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                Intersection value = getRandomIntersection();
                first.set(coordinate, value);
                if (value == EMPTY) {
                    second.set(coordinate, TEMPORARILY_UNPLAYABLE);
                } else if (value == TEMPORARILY_UNPLAYABLE) {
                    second.set(coordinate, EMPTY);
                } else {
                    second.set(coordinate, value);
                }
            }
            assertTrue(first.isSamePositionAs(second));
            assertTrue(second.isSamePositionAs(first));
        }
    }
    
    @Test
    public void isSamePositionAsReturnsFalseWhenAnyIntersectionsBetweenTwoBoardHaveADifferentColor() {
        for (int dimension : VALID_SIZES) {
            Board board = new Board(dimension);
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                Intersection value = getRandomIntersection();
                board.set(coordinate, value);
            }
            
            Board different = new Board(board);
            Coordinate toMutate = getRandomCoordinate(dimension);
            Intersection toChange = board.get(toMutate);
            Intersection newValue;
            do {
                newValue = getRandomIntersection();
            } while (toChange.countsAsLiberty() && newValue.countsAsLiberty() || toChange == newValue);
            different.set(toMutate, newValue);
            
            assertFalse(board.isSamePositionAs(different));
            assertFalse(different.isSamePositionAs(board));
        }
    }
    
    @Test
    public void twoBoardsOfDifferentSizesNeverHaveTheSamePosition() {
        for (int dimension : VALID_SIZES) {
            if (dimension == 1) {
                continue;
            }
            
            Board smaller = new Board(dimension - 1);
            Board board = new Board(dimension);
            assertFalse(smaller.isSamePositionAs(board));
            assertFalse(board.isSamePositionAs(smaller));
        }
    }
    
    @Test
    public void isSamePositionAsThrowsAnExceptionForNullArgument() {
        for (int dimension : VALID_SIZES) {
            Board board = new Board(dimension);
            try {
                boolean wrong = board.isSamePositionAs(null);
                failForReturn("board.isSamePositionAs", "IllegalArgumentException", wrong, (Object) null); 
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("board.isSamePositionAs", "IllegalArgumentException", t, (Object) null);
            }
        }
    }
    
    private Coordinate getRandomCoordinate( int dimension ) {
        Random random = ThreadLocalRandom.current();
        int column = random.nextInt(dimension) + 1;
        int row = random.nextInt(dimension) + 1;
        return Coordinate.get(column, row);
    }
    
    // TODO: Test that the isSamePositionAs() can detect different positions, that null causes an Exception, and that
    // boards of different sizes are never the same.
    
    @Test
    public void twoIdenticalBoardAreEqual() {
        for (int dimension : VALID_SIZES) {
            Board original = buildRandomBoard(dimension);
            Board copy = new Board(original);
            assertTrue(original.equals(copy));
            assertTrue(copy.equals(original));
        }
    }
    
    @Test
    public void equalBoardsGenerateIdenticalHashCodes() {
        for (int dimension : VALID_SIZES) {
            Board original = buildRandomBoard(dimension);
            Board copy = new Board(original);
            assertEquals(original.hashCode(), copy.hashCode());
        }
    }
    
    // I do not want to write comprehensive unit tests for hashCode() or toString().  hashCode() is almost completely
    // tested by the current tests; all that is missing is testing a branch for illegal intersection values that should
    // never get set (as verified by the above tests).  toString() is a convenient string representation to aid
    // debugging as opposed to a maintained interface method. 
    
    // Testing my SituationHashes in my goai project revealed that there appear to be situations in which two
    // substantially different boards evaluate as having the same position, resulting in incorrect ko evaluation.  This
    // test evaluates one such position discovered in those tests to determine whether this bug has been fixed.
    @Test
    public void doDifferentBoardsGenerateDifferentPositions() {
        Board first = new Board(6);
        Board second = new Board(6);
        
        Coordinate[] SHARED_BLACK = {
            C05_R01,
            C01_R02,
            C02_R03,
            C06_R03,
            C01_R05,
            C03_R05,
            C04_R05,
            C05_R05,
            C04_R06
        };
        Coordinate[] SHARED_WHITE = {
            C02_R01,
            C03_R01,
            C06_R01,
            C02_R02,
            C04_R02,
            C05_R03,
            C02_R04,
            C04_R04,
            C02_R05,
            C06_R06
        };
        
        for (Coordinate coordinate : SHARED_BLACK) {
            first.set(coordinate, BLACK);
            second.set(coordinate, BLACK);
        }
        for (Coordinate coordinate : SHARED_WHITE) {
            first.set(coordinate, WHITE);
            second.set(coordinate, WHITE);
        }
        
        first.set(C02_R06, BLACK);
        first.set(C03_R03, WHITE);
        
        second.set(C04_R03, BLACK);
        second.set(C01_R06, WHITE);
        
        assertFalse(first.isSamePositionAs(second));
    }
    
    // 2017-07-31: Testing SituationHash found a sequence that generated boards with identical meanings but had
    // different legal moves.  To my surprise, a legal move was marked as temporarily unplayable on one of the
    // boards.  This situation has been captured as a unit test to try to fix this.
    @Test
    public void legalMovesShouldNotBeFlaggedAsTemporarilyUnplayable() {
        Move[] moves = {
            C03_R01, C04_R02,
            C01_R05, C02_R01,
            C05_R03, C02_R03,
            PASS, C03_R04,
            C02_R04, C05_R04,
            C05_R05, C02_R05,
            C05_R02, C03_R02,
            C01_R03, C01_R01,
            C02_R02, C03_R03,
            C05_R01, C01_R02
        };
        
        Game game = Game.newBuilder(5).build();
        for (Move move : moves) {
            game = game.play(move);
        }
        assertNotEquals(TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE, game.get(C03_R05));
    }
}
