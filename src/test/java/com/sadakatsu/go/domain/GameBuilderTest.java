package com.sadakatsu.go.domain;

import static org.junit.Assert.*;
import static com.sadakatsu.util.TestHelper.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sadakatsu.go.domain.Game.GameBuilder;
import com.sadakatsu.go.domain.intersection.Empty;
import com.sadakatsu.go.domain.intersection.Intersection;
import com.sadakatsu.go.domain.intersection.Player;
import com.sadakatsu.go.domain.intersection.Stone;
import com.sadakatsu.go.domain.intersection.TemporarilyUnplayable;
import com.sadakatsu.go.domain.outcome.InProgress;

public class GameBuilderTest {
    private static final int[] INVALID_DIMENSIONS = { -5, -4, -3, -2, -1, 0, 20, 21, 22, 23, 24, 25 };
    private static final int[] VALID_DIMENSIONS = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
    
    //==================================================================================================================
    // Following my preferred pattern, builders should not expose their constructors.  Builders should be provided
    // through factory methods exposed by the built class.
    //==================================================================================================================
    @Test
    public void gameBuilderProvidesNoPublicConstructors() throws Exception {
        Constructor<?>[] constructors = GameBuilder.class.getConstructors();
        assertEquals(0, constructors.length);
    }
    
    //==================================================================================================================
    // I wrestled with the idea of allowing GameBuilders to provide a mutable board dimension.  The problem with this is
    // that a GameBuilder also needs to manage the placement of handicap stones.  If a GameBuilder's dimension is
    // changed, the positions of the old handicap stones may become invalid, or there might be too many stones to fit on
    // the board.  The only reasonable options to solve this are (1) to remove all stones that are invalid for the new
    // board size, (2) remove all handicap stones on a resize, or (3) to make the dimension be permanently set by the
    // constructor.  Options 1 and 2 have the smells of having multiple side effects for a setter, having complicated
    // side effects that will be difficult to remember, and removing one of the benefits of a builder (that of being
    // able to call the setters in whatever order is convenient for the user), so I chose Option 3.  The GameBuilder
    // factory methods set the board's dimension through the hidden constructor, and the user has to live with it after
    // that.
    //==================================================================================================================
    
    // Let the niladic factory method return a builder for a standard-sized board as a convenience.
    @Test
    public void gameClassNewBuilderWithNoArgumentsReturns19x19Builder() {
        GameBuilder builder = Game.newBuilder();
        assertNotNull(builder);
        assertEquals(19, builder.getDimension());
    }
    
    // If the user supplies a dimension argument to the factory method, it should successfully use the values 1 through
    // 19.
    @Test
    public void gameClassNewBuilderWithDimensionArgumentReturnsBuilderUsingValidArgumentAsDimension() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = Game.newBuilder(dimension);
            assertNotNull(builder);
            assertEquals(dimension, builder.getDimension());
        }
    }
    
    // The factory method should throw an IllegalArgumentException if the user provides it a dimension less than 1 or
    // greater than 19.
    @Test
    public void gameClassNewBuilderWithDimensionArgumentErrorsOnDimensionLessThan1OrGreaterThan19() {
        for (int dimension : INVALID_DIMENSIONS) {
            try {
                GameBuilder builder = Game.newBuilder(dimension);
                failForReturn("Game.newBuilder", "IllegalArgumentException", builder, dimension);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                // already failed
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("Game.newBuilder", "IllegalArgumentException", t, dimension);
            }
        }
    }
    
    //==================================================================================================================
    // The standard compensation given to White is 7.5 points.  This value is used by a GameBuilder unless the user
    // changes it, and then the GameBuilder uses the last value the user set.  Negative values are acceptable so that
    // reverse compensation can work. If the passed value is not divisible by 0.25, an IllegalArgumentException should
    // be thrown without changing the GameBuilder at all.
    //==================================================================================================================
    @Test
    public void newBuilderWithNoArgumentsStartsWithCompensationOfSevenPointFive() {
        GameBuilder builder = Game.newBuilder();
        assertEquals(7.5, builder.getCompensation(), 0.); // Small numbers divisible by 0.25 are stored exactly. 
    }
    
    @Test
    public void newBuilderWithValidDimensionStartsWithCompensationOfSevenPointFive() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = Game.newBuilder(dimension);
            assertEquals(7.5, builder.getCompensation(), 0.);
        }
    }
    
    @Test
    public void setCompensationAcceptsAnyFiniteCompensationEvenlyDivisibleByZeroPointTwoFive() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = Game.newBuilder(dimension);
            for (double compensation = -100.75; compensation <= 100.75; compensation += 0.25) {
                builder.setCompensation(compensation);
                assertEquals(compensation, builder.getCompensation(), 0.);
            }
        }
    }
    
    @Test
    public void setCompensationThrowsAnExceptionWithoutChangingAnythingForNonFiniteArguments() {
        double[] illegalCompensations = { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN };
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            for (double illegal : illegalCompensations) {
                try {
                    builder.setCompensation(illegal);
                    failForReturn("builder.setCompensation", "IllegalArgumentException", builder, illegal);
                } catch (IllegalArgumentException e) {
                    assertEquals(expected, builder);
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable t) {
                    failForWrongThrowable("builder.setCompensation", "IllegalArgumentException", t, illegal);
                }
            }
        }
    }
    
    @Test
    public void setCompensationThrowsAnExceptionWithoutChangingAnythingForFiniteArgumentsThatAreNotDivisibleByZeroPointTwoFive() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            
            for (int i = 0; i < 100; ++i) {
                double illegal;
                do {
                    illegal = ThreadLocalRandom.current().nextDouble();
                } while (isDivisibleByOneQuarter(illegal));
                try {
                    builder.setCompensation(illegal);
                    failForReturn("builder.setCompensation", "IllegalArgumentException", builder, illegal);
                } catch (IllegalArgumentException e) {
                    assertEquals(expected, builder);
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable t) {
                    failForWrongThrowable("builder.setCompensation", "IllegalArgumentException", t, illegal);
                }
            }
        }
    }
    
    private boolean isDivisibleByOneQuarter( double value ) {
        return (double) (int) (value * 4) == value * 4;
    }
    
    //==================================================================================================================
    // GameBuilders should always start with no handicap stones.  This should be proven both by checking the count of
    // handicap stones in the builder and by receiving an empty set back from querying for handicap stones.
    //==================================================================================================================
    @Test
    public void gameBuilderFactoryWithNoArgumentsStartsWithNoHandicapStones() {
        GameBuilder builder = Game.newBuilder();
        assertEquals(0, builder.countHandicapStones());
        assertEquals(Collections.EMPTY_SET, builder.getHandicapStones());
    }
    
    @Test
    public void gameBuilderFactoryWithValidDimensionArgumentStartsWithNoHandicapStones() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = Game.newBuilder(dimension);
            assertEquals(0, builder.countHandicapStones());
            assertEquals(Collections.EMPTY_SET, builder.getHandicapStones());
        }
    }
    
    //==================================================================================================================
    // GameBuilders must not return a Set of handicap stone placements that modifies its internal state.
    //==================================================================================================================
    @Test
    public void gameBuilderGetHandicapStonesReturnsCopyOfInternalSet() {
        GameBuilder builder = Game.newBuilder();
        Set<Coordinate> stones = builder.getHandicapStones();
        stones.add(null);
        assertNotEquals(builder.getHandicapStones(), stones);
    }
    
    //==================================================================================================================
    // GameBuilders should allow users to compose the handicap stone state since this will allow a program to pick
    // placements either by AI or by UI events.  This method should have the following effects:
    // 1) If the user passes null or a Coordinate outside of the GameBuilder's board dimensions, it throws an
    //    IllegalArgumentException.
    // 2) If the passed Coordinate is valid, but adding it would fill the whole board with stones, it throws an
    //    IllegalStateException.
    // 3) If the passed Coordinate is valid, but it has already been added to the GameBuilder, then nothing changes.
    // 4) Otherwise, the Coordinate is added to the set of handicap stones.  This should result in an incrementing of
    //    the GameBuilder's count of handicap stones and its addition to the set it would have previously returned.  No
    //    stones from previous calls should be added.
    //==================================================================================================================
    @Test
    public void gameBuilderCanAddOneHandicapStoneAtATime() {
        for (int dimension : VALID_DIMENSIONS) {
            if (dimension == 1) {
                continue; // because a 1x1 board cannot accept handicap stones due to self-capture
            }
            
            GameBuilder builder = Game.newBuilder(dimension);
            int expectedStones = 0;
            for (Coordinate stone : Coordinate.iterateOverBoard(dimension)) {
                Set<Coordinate> previousSet = builder.getHandicapStones();
                
                if (expectedStones == dimension * dimension - 1) {
                    break; // we don't want to trigger the error condition in this test
                }
                int count = builder.addHandicapStone(stone).countHandicapStones();
                ++expectedStones;
                Set<Coordinate> currentSet = builder.getHandicapStones();
                assertEquals(expectedStones, count);
                assertTrue(currentSet.contains(stone));
                
                currentSet.retainAll(previousSet);
                assertEquals(previousSet, currentSet);
            }
        }
    }
    
    @Test
    public void gameBuilderAddSingleStoneThrowsExceptionForIllegalCoordinatesAndDoesNotChangeItsState() {
        for (int dimension : VALID_DIMENSIONS) {
            List<Coordinate> invalidCoordinates = getInvalidCoordinates(dimension);
            
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            for (Coordinate invalid : invalidCoordinates) {
                try {
                    builder.addHandicapStone(invalid);
                    failForReturn(
                        "builder.addHandicapStone",
                        "IllegalArgumentException or IllegalStateException",
                        builder,
                        invalid
                    );
                } catch (IllegalArgumentException | IllegalStateException e) {
                    assertEquals(expected, builder);
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable t) {
                    failForWrongThrowable(
                        "builder.addHandicapStone",
                        "IllegalArgumentException or IllegalStateException",
                        t,
                        invalid
                    );
                }
            }
        }
    }
    
    private List<Coordinate> getInvalidCoordinates( int dimension ) {
        List<Coordinate> invalidCoordinates = new ArrayList<>();
        
        if (dimension < 19) {
            invalidCoordinates = getAllCoordinates();
            if (dimension > 1) {
                List<Coordinate> validCoordinates = getValidCoordinates(dimension);
                invalidCoordinates.removeAll(validCoordinates);
            }
        }
        
        invalidCoordinates.add(null);
        Collections.shuffle(invalidCoordinates);
        
        return invalidCoordinates;
    }
    
    private List<Coordinate> getAllCoordinates() {
        return Lists.newArrayList(Coordinate.iterateOverBoard());
    }
    
    private List<Coordinate> getValidCoordinates( int dimension ) {
        return Lists.newArrayList(Coordinate.iterateOverBoard(dimension));
    }
    
    @Test
    public void gameBuilderAddSingleStoneThrowsExceptionIfUserTriesToFillBoardWithHandicapStones() {
        for (int dimension : VALID_DIMENSIONS) {
            int maxNumberOfStones = dimension * dimension - 1;
            
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            
            Set<Coordinate> coordinateSet = Sets.newHashSet(Coordinate.iterateOverBoard(dimension));
            List<Coordinate> coordinateList = new ArrayList<>(coordinateSet);
            Collections.shuffle(coordinateList);
            for (int i = 0; i < maxNumberOfStones; ++i) {
                Coordinate coordinate = coordinateList.get(i);
                builder.addHandicapStone(coordinate);
                expected.addHandicapStone(coordinate);
            }
            
            Coordinate tooMany = coordinateList.get(maxNumberOfStones);
            try {
                builder.addHandicapStone(tooMany);
                failForReturn("builder.addHandicapStone", "IllegalStateException", builder, tooMany);
            } catch (IllegalStateException e) {
                assertEquals(expected, builder);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("builder.addHandicapStone", "IllegalStateException", t, tooMany);
            }
        }
    }
    
    @Test
    public void gameBuilderAddSingleStoneWithCoordinateAlreadyInCollectionEffectsNoChange() {
        for (int dimension : VALID_DIMENSIONS) {
            if (dimension == 1) {
                continue;
            }
            
            int maxNumberOfStones = dimension * dimension - 1;
            
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            
            Set<Coordinate> coordinateSet = Sets.newHashSet(Coordinate.iterateOverBoard(dimension));
            List<Coordinate> coordinateList = new ArrayList<>(coordinateSet);
            Collections.shuffle(coordinateList);
            coordinateList.remove(maxNumberOfStones - 1);
            for (Coordinate coordinate : coordinateList) {
                builder.addHandicapStone(coordinate);
                expected.addHandicapStone(coordinate);
            }
            
            for (Coordinate coordinate : coordinateList) {
                builder.addHandicapStone(coordinate);
                assertEquals(expected, builder);
            }
        }
    }
    
    //==================================================================================================================
    // Since UI setup is one of the focuses of this API, addHandicapStone() needs to be paired with
    // removeHandicapStone().  This method should have the following effects:
    // 1) If the user passes null or a Coordinate outside of the GameBuilder's board dimensions, it throws an
    //    IllegalArgumentException.
    // 2) If the passed Coordinate is valid but not in the GameBuilder, then nothing changes.
    // 3) Otherwise, it removes the Coordinate.  The count of handicap stones decreases by one, and it will no longer be
    //    in the set of stones it returns.  No other handicap stones should be removed.
    //==================================================================================================================
    @Test
    public void gameBuilderCanRemoveOneStoneAtATime() {
        for (int dimension : VALID_DIMENSIONS) {
            if (dimension == 1) {
                continue;
            }
            
            Iterable<Coordinate> coordinateIterable = Coordinate.iterateOverBoard(dimension);
            List<Coordinate> coordinateList = Lists.newArrayList(coordinateIterable);
            Collections.shuffle(coordinateList);
            coordinateList.remove(coordinateList.size() - 1);
            
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            for (Coordinate coordinate : coordinateList) {
                builder.addHandicapStone(coordinate);
                expected.addHandicapStone(coordinate);
            }
            
            Collections.shuffle(coordinateList);
            for (Coordinate coordinate : coordinateList) {
                builder.removeHandicapStone(coordinate);
                Set<Coordinate> afterRemove = builder.getHandicapStones();
                
                Set<Coordinate> disjoint = expected.getHandicapStones();
                disjoint.removeAll(afterRemove);
                
                assertNotEquals(expected, builder);
                assertEquals(expected.countHandicapStones() - 1, builder.countHandicapStones());
                assertEquals(1, disjoint.size());
                assertTrue(disjoint.contains(coordinate));
                
                expected.removeHandicapStone(coordinate);
            }
        }
    }
    
    @Test
    public void attemptingToRemoveHandicapStonesNotInGameBuilderChangesNothing() {
        for (int dimension : VALID_DIMENSIONS) {
            if (dimension == 1) {
                continue;
            }
            
            Iterable<Coordinate> coordinateIterable = Coordinate.iterateOverBoard(dimension);
            List<Coordinate> coordinateList = Lists.newArrayList(coordinateIterable);
            Collections.shuffle(coordinateList);
            coordinateList.remove(coordinateList.size() - 1);
            
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            for (Coordinate coordinate : coordinateList) {
                builder.addHandicapStone(coordinate);
                expected.addHandicapStone(coordinate);
            }
            
            Collections.shuffle(coordinateList);
            for (Coordinate coordinate : coordinateList) {
                expected.removeHandicapStone(coordinate);
                builder.removeHandicapStone(coordinate);
                
                // This tests both whether removing a Coordinate not in the set or removing from an empty set causes an
                // error.
                builder.removeHandicapStone(coordinate);
                assertEquals(expected, builder);
            }
        }
    }
    
    @Test
    public void attemptingToRemoveHandicapStoneWithInvalidCoordinateThrowsExceptionWithoutChangingAnything() {
        for (int dimension : VALID_DIMENSIONS) {
            List<Coordinate> invalidCoordinates = getInvalidCoordinates(dimension);
            
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            if (dimension > 1) {
                builder.addHandicapStone(Coordinate.C01_R01);
                expected.addHandicapStone(Coordinate.C01_R01);
            }
            
            for (Coordinate invalid : invalidCoordinates) {
                try {
                    builder.removeHandicapStone(invalid);
                    failForReturn("builder.removeHandicapStone", "IllegalArgumentException", builder, invalid);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    assertEquals(expected, builder);
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable t) {
                    failForWrongThrowable("builder.removeHandicapStone", "IllegalArgumentException", t, invalid);
                }
            }
        }
    }
    
    //==================================================================================================================
    // Removing stones one at a time, let alone needing to know where they are to do so, can be cumbersome.  It is
    // convenient to allow users to remove all the handicap stones at once.  This method should always work, since it
    // requires no arguments and has the same expected end state after every call.
    //==================================================================================================================
    @Test
    public void gameBuilderCanRemoveAllHandicapStonesAtOnce() {
        for (int dimension : VALID_DIMENSIONS) {
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            
            if (dimension > 1) {
                List<Coordinate> stones = getValidCoordinates(dimension);
                Collections.shuffle(stones);
                stones.remove(stones.size() - 1);
                for (Coordinate stone : stones) {
                    builder.addHandicapStone(stone);
                }
            }
            
            builder.removeAllHandicapStones();
            assertEquals(expected, builder);
        }
    }
    
    //==================================================================================================================
    // As the previous tests show, adding handicap stones one at a time can be cumbersome.  After being indecisive for a
    // while, I decided that it is acceptable to pair removeAllHandicapStones() with a setHandicapStones() method.  To
    // make this method as consistent as possible with the other methods, it has the following requirements:
    // 0) I chose to use Sets for the arguments, even though Collections should work, to prevent the situation where a
    //    user is surprised by the number of handicap stones in the GameBuilder should the Collection contain
    //    duplicates.  Unfortunately, this thinking precludes a variadic override, even though I love those.
    // 1) If the passed set of Coordinates is null or contains any Coordinates that are outside of the GameBuilder's
    //    dimensions, this method throws an IllegalArgumentException without changing the GameBuilder's state.
    // 2) If the passed set of Coordinates is too large for the GameBuilder's dimension, this method throws an
    //    IllegalStateException without changing the GameBuilder's state.
    // 3) Otherwise, this method overrides the internal Collection of handicap stones to contain those specified in the
    //    passed set.  This is a copy so modifying the argument will not also modify the GameBuilder's internal state.
    //    The number and placements of the handicap stones will match those in the argument after this call.
    //==================================================================================================================
    @Test
    public void gameBuilderCanSetHandicapStonesToValidSet() {
        for (int dimension : VALID_DIMENSIONS) {
            if (dimension == 1) {
                continue;
            }
            
            List<Coordinate> stoneList = getValidCoordinates(dimension);
            stoneList.remove(0);
            Set<Coordinate> stoneSet = new HashSet<>(stoneList);
            
            GameBuilder builder = Game.newBuilder(dimension);
            builder.setHandicapStones(stoneSet);
            assertEquals(stoneSet, builder.getHandicapStones());
            stoneSet.add(null);
            assertNotEquals(stoneSet, builder.getHandicapStones());
        }
    }
    
    @Test
    public void gameBuilderSetHandicapStonesThrowsExceptionWhenNullPassedAndDoesNotChangeState() {
        for (int dimension : VALID_DIMENSIONS) {
            if (dimension == 1) {
                continue;
            }
            
            GameBuilder builder = Game.newBuilder(dimension);
            GameBuilder expected = Game.newBuilder(dimension);
            try {
                builder.setHandicapStones(null);
                failForReturn("builder.setHandicapStones", "IllegalArgumentException", builder, (Object) null);
            } catch (IllegalArgumentException e) {
                assertEquals(expected, builder);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("builder.setHandicapStones", "IllegalArgumentException", t, (Object) null);
            }
        }
    }
    
    @Test
    public void gameBuilderSetHandicapStonesThrowsExceptionWhenAnyInvalidCoordinatesFoundAndDoesNotChangeState() {
        for (int dimension : VALID_DIMENSIONS) {
            List<Coordinate> stoneList = getValidCoordinates(dimension);
            stoneList.remove(0);
            
            Set<Coordinate> validSet = getValidSubset(stoneList);
            
            List<Coordinate> invalid = getInvalidCoordinates(dimension);
            invalid.add(null);
            Collections.shuffle(invalid);
            
            GameBuilder builder = Game.newBuilder(dimension);
            builder.setHandicapStones(validSet);
            GameBuilder expected = Game.newBuilder(dimension);
            expected.setHandicapStones(validSet);
            
            for (Coordinate current : invalid) {
                stoneList.add(current);
                Set<Coordinate> problem = new HashSet<>(stoneList);
                
                try {
                    builder.setHandicapStones(problem);
                    failForReturn(
                        "builder.setHandicapStones",
                        "IllegalArgumentException or IllegalStateException",
                        builder,
                        problem
                    );
                } catch (IllegalArgumentException | IllegalStateException e) {
                    assertEquals(expected, builder);
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable t) {
                    failForWrongThrowable(
                        "builder.setHandicapStones",
                        "IllegalArgumentException or IllegalStateException",
                        t,
                        problem
                    );
                }
                
                stoneList.remove(current);
            }
        }
    }
    
    private Set<Coordinate> getValidSubset( List<Coordinate> validCoordinates ) {
        Set<Coordinate> subset = new HashSet<>();
        for (int i = 0, take = validCoordinates.size() / 2; i < take; ++i) {
            Coordinate stone = validCoordinates.get(i);
            subset.add(stone);
        }
        return subset;
    }
    
    @Test
    public void gameBuilderSetHandicapStonesThrowsExceptionForTooManyStonesAndDoesNotChangeState() {
        for (int dimension : VALID_DIMENSIONS) {
            List<Coordinate> stoneList = getValidCoordinates(dimension);
            Set<Coordinate> subset = getValidSubset(stoneList);
            Set<Coordinate> tooMany = new HashSet<>(stoneList);
            
            GameBuilder builder = Game.newBuilder(dimension);
            builder.setHandicapStones(subset);
            GameBuilder expected = Game.newBuilder(dimension);
            expected.setHandicapStones(subset);
            
            try {
                builder.setHandicapStones(tooMany);
                failForReturn(
                    "builder.setHandicapStones",
                    "IllegalStateException",
                    builder,
                    tooMany
                );
            } catch (IllegalStateException e) {
                assertEquals(expected, builder);
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable(
                    "builder.setHandicapStones",
                    "IllegalStateException",
                    t,
                    tooMany
                );
            }
        }
    }
    
    //==================================================================================================================
    // The point of GameBuilder is to build a Game.  build() should always return a Game since all the setters ensure
    // that the GameBuilder never contains an erroneous state.  The Game should be created with the appropriate Board
    // dimensions, compensation, handicap stone placement, and player to move based upon the GameBuilder settings (note
    // that the player to move should be BLACK if there are no handicap stones and WHITE otherwise).  All the other
    // Game getters should indicate that no game play has occurred yet.
    //==================================================================================================================
    @Test
    public void gameBuilderBuildsGamesCorrectly() {
        for (GameBuilder builder : new GameBuilderConfigurations()) {
            int dimension = builder.getDimension();
            Set<Coordinate> handicapStones = builder.getHandicapStones();
            
            Game game = builder.build();
            
            // Some starting values are always the same.
            assertNotNull(game);
            assertEquals(0, game.getCapturesFor(Stone.BLACK));
            assertEquals(0, game.getCapturesFor(Stone.WHITE));
            assertEquals(0, game.getMovesPlayed());
            assertEquals(InProgress.IN_PROGRESS, game.getOutcome());
            
            // Others are based upon the builder's values.
            assertEquals(builder.getDimension(), game.getDimension());
            assertEquals(builder.getCompensation(), game.getCompensation(), 0.);
            assertEquals(handicapStones, game.getHandicapStonePlacements());
            
            Player expected = handicapStones.size() == 0 ? Stone.BLACK : Stone.WHITE;
            assertEquals(expected, game.getCurrentPlayer());
            
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                Intersection state = game.get(coordinate);
                if (handicapStones.contains(coordinate)) {
                    assertEquals(Stone.BLACK, state);
                } else {
                    assertTrue(
                        "Any Coordinate on a starting Game that does not contain a Black stone should be empty or " +
                        "temporarily unplayable, but instead it is " + state,
                        state == Empty.EMPTY || state == TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE
                    );
                }
            }
        }
    }
    
    //==================================================================================================================
    // GameBuilder is a mutable object and is not conceived for any situation in which comparison between two instances
    // will be useful.  Furthermore, it should never be used as a key in a collection.  However, it was expedient for
    // the unit tests above to be able to compare GameBuilders, so both equals() and hashCode() were overridden.  The
    // following tests are to ensure that this overriding behaves correctly.
    //==================================================================================================================
    private static class GameBuilderConfigurations implements Iterable<GameBuilder>, Iterator<GameBuilder> {
        private static double MAX_COMPENSATION = 0.5;
        
        private double compensation;
        private int dimension;
        private int stones;
        private GameBuilder builder;
        private List<Coordinate> validCoordinates;
        
        GameBuilderConfigurations() {
            builder = Game.newBuilder(1);
            compensation = -MAX_COMPENSATION;
            dimension = 1;
            stones = 0;
            validCoordinates = getValidCoordinatesForCurrentDimension();
        }
        
        private List<Coordinate> getValidCoordinatesForCurrentDimension() {
            Iterable<Coordinate> iterable = Coordinate.iterateOverBoard(dimension);
            List<Coordinate> list = Lists.newArrayList(iterable);
            Collections.shuffle(list);
            return list;
        }
        
        @Override
        public boolean hasNext() {
            return dimension < 20;
        }

        @Override
        public GameBuilder next() {
            if (compensation + 0.25 <= MAX_COMPENSATION) {
                compensation += 0.25;
            } else {
                if (stones + 1 < validCoordinates.size() / 4) {
                    Coordinate next = validCoordinates.get(stones);
                    builder.addHandicapStone(next);
                    ++stones;
                } else {
                    ++dimension;
                    if (hasNext()) {
                        builder = Game.newBuilder(dimension);
                        stones = 0;
                        validCoordinates = getValidCoordinatesForCurrentDimension();
                    }
                }
                
                compensation = -MAX_COMPENSATION;
            }
            
            builder.setCompensation(compensation);
            return builder;
        }

        @Override
        public Iterator<GameBuilder> iterator() {
            return this;
        }
    }
    
    @Test
    public void aGameBuilderIsAlwaysEqualToItself() {
        for (GameBuilder builder : new GameBuilderConfigurations()) {
            assertEquals(builder, builder);
        }
    }
    
    @Test
    public void aGameBuilderIsNeverEqualToNull() {
        for (GameBuilder builder : new GameBuilderConfigurations()) {
            assertNotEquals(null, builder);
        }
    }
    
    @Test
    public void aGameBuilderCannotBeEqualToAnyOtherClass() {
        for (GameBuilder builder : new GameBuilderConfigurations()) {
            assertNotEquals(builder.hashCode(), builder);
        }
    }
    
    @Test
    public void gameBuildersMustHaveTheSameDimensionCompensationAndHandicapStonesToBeEqual() {
        for (GameBuilder first : new GameBuilderConfigurations()) {
            for (GameBuilder second : new GameBuilderConfigurations()) {
                boolean shouldBeEqual =
                    first.getDimension() == second.getDimension() &&
                    first.getCompensation() == second.getCompensation() &&
                    Objects.equals(first.getHandicapStones(), second.getHandicapStones());
                if (shouldBeEqual) {
                    assertEquals(first, second);
                } else {
                    assertNotEquals(first, second);
                }
            }
        }
    }
    
    @Test
    public void gameBuilderHashCodesAreEqualForEqualGameBuilders() {
        for (GameBuilder builder : new GameBuilderConfigurations()) {
            GameBuilder copy = Game.newBuilder(builder.getDimension());
            copy.setCompensation(builder.getCompensation());
            copy.setHandicapStones(builder.getHandicapStones());
            assertEquals(builder.hashCode(), copy.hashCode());
        }
    }
    
    //==================================================================================================================
    // A good toString() method will help with logging and debugging, so ensure that all the pertinent fields are
    // recorded in that String.
    //==================================================================================================================
    @Test
    public void gameBuilderToStringPresentsAllSetValues() {
        for (GameBuilder builder : new GameBuilderConfigurations()) {
            String representation = builder.toString();
            assertTrue(representation.contains(Integer.toString(builder.getDimension())));
            assertTrue(representation.contains(Double.toString(builder.getCompensation())));
            for (Coordinate coordinate : builder.getHandicapStones()) {
                assertTrue(representation.contains(coordinate.toString()));
            }
        }
    }
}
