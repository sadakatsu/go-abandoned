package com.sadakatsu.go.domain;

import static com.sadakatsu.go.domain.intersection.Empty.EMPTY;
import static com.sadakatsu.go.domain.intersection.PermanentlyUnplayable.PERMANENTLY_UNPLAYABLE;
import static com.sadakatsu.go.domain.intersection.Stone.BLACK;
import static com.sadakatsu.go.domain.intersection.Stone.WHITE;
import static com.sadakatsu.go.domain.intersection.TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE;
import static com.sadakatsu.util.TestHelper.*;
import static org.junit.Assert.*;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.sadakatsu.go.domain.intersection.Intersection;

public class GroupTest {
    private static final Intersection[] INTERSECTIONS = {
        EMPTY,
        BLACK,
        WHITE,
        TEMPORARILY_UNPLAYABLE,
        PERMANENTLY_UNPLAYABLE
    };
    
    @Test
    public void groupConstructorRejectsNullBoard() {
        for (Coordinate coordinate : Coordinate.values()) {
            try {
                Group wrong = new Group(null, coordinate);
                failForReturn("new Group()", "IllegalArgumentException", wrong, null, coordinate);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("new Group", "IllegalArgumentException", t, null, coordinate);
            }
        }
    }
    
    @Test
    public void groupConstructorRejectsNullCoordinate() {
        for (int dimension = 1; dimension <= 19; ++dimension) {
            Board board = new Board(dimension);
            try {
                Group wrong = new Group(board, null);
                failForReturn("new Group()", "IllegalArgumentException", wrong, board, null);
            } catch (IllegalArgumentException e) {
                // success
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable t) {
                failForWrongThrowable("new Group", "IllegalArgumentException", t, board, null);
            }
        }
    }
    
    @Test
    public void groupConstructorRejectsCoordinateThatIsOffBoard() {
        for (int dimension = 1; dimension <= 18; ++dimension) {
            Iterable<Coordinate> validCoordinatesIterable = Coordinate.iterateOverBoard(dimension);
            Set<Coordinate> validCoordinatesSet = Sets.newHashSet(validCoordinatesIterable);
            
            Iterable<Coordinate> allCoordinates = Coordinate.iterateOverBoard();
            Set<Coordinate> invalidCoordinates = Sets.newHashSet(allCoordinates);
            invalidCoordinates.removeAll(validCoordinatesSet);
            
            Board board = new Board(dimension);
            for (Coordinate coordinate : invalidCoordinates) {
                try {
                    Group wrong = new Group(board, coordinate);
                    failForReturn("new Group()", "IllegalArgumentException", wrong, board, coordinate);
                } catch (IllegalArgumentException e) {
                    // success
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable t) {
                    failForWrongThrowable("new Group", "IllegalArgumentException", t, board, coordinate);
                }
            }
        }
    }
    
    @Test
    public void allEmptyBoardsHaveOneEmptyGroupCoveringEntireBoard() {
        for (int dimension = 1; dimension <= 19; ++dimension) {
            Board board = new Board(dimension);
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                Group group = new Group(board, coordinate);
                assertNotNull(group);
                assertEquals(EMPTY, group.type);
                assertFalse(group.bordersBlack);
                assertFalse(group.bordersWhite);
                assertEquals(0, group.liberties);
                assertEquals(dimension * dimension, group.members.size());
                assertTrue(group.members.contains(coordinate));
            }
        }
    }
    
    @Test
    public void emptyIntersectionsAndUnplayableIntersectionsAreBothConsideredEmptyForGroups() {
        for (int dimension = 1; dimension <= 19; ++dimension) {
            Board board = new Board(dimension);
            for (int mutated = 0; mutated < dimension; ++mutated) {
                Coordinate toMutate = getRandomValidCoordinate(dimension);
                board.set(toMutate, TEMPORARILY_UNPLAYABLE);
            }
            
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                Group group = new Group(board, coordinate);
                assertNotNull(group);
                assertEquals(EMPTY, group.type);
                assertFalse(group.bordersBlack);
                assertFalse(group.bordersWhite);
                assertEquals(0, group.liberties);
                assertEquals(dimension * dimension, group.members.size());
                assertTrue(group.members.contains(coordinate));
            }
        }
    }
    
    private Coordinate getRandomValidCoordinate( int dimension ) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int column = random.nextInt(1, dimension + 1);
        int row = random.nextInt(1, dimension + 1);
        return Coordinate.get(column, row);
    }
    
    @Test
    public void groupsAreEqualIfTheyHaveIdenticalTypesLibertiesBorderStatesAndMembers() {
        Board board = buildRandomBoard();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Group a = new Group(board, coordinate);
            Group b = new Group(board, coordinate);
            
            assertEquals(a.bordersBlack, b.bordersBlack);
            assertEquals(a.bordersWhite, b.bordersWhite);
            assertEquals(a.liberties, b.liberties);
            assertEquals(a.type, b.type);
            assertEquals(a.members, b.members);
            assertEquals(a, b);
        }
    }
    
    @Test
    public void groupsAreNotEqualIfAnyOfTheirFieldsAreDifferent() {
        Board board = buildRandomBoard();
        board.set(Coordinate.C01_R01, BLACK);
        board.set(Coordinate.C19_R19, WHITE);
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Group a = new Group(board, coordinate);
            
            for (Coordinate neighbor : coordinate.getNeighbors()) {
                Intersection current = board.get(neighbor);
                if (a.type != current && !(a.type.countsAsLiberty() && current.countsAsLiberty())) {
                    Group b = new Group(board, neighbor);
                    assertNotEquals(a, b);
                }
            }
        }
    }
    
    private Board buildRandomBoard() {
        Board board = new Board();
        
        Random random = ThreadLocalRandom.current();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            int index = random.nextInt(INTERSECTIONS.length);
            Intersection value = INTERSECTIONS[index];
            board.set(coordinate, value);
        }
        
        return board;
    }
    
    @Test
    public void adjacentIntersectionsWithSameStateBelongToSameGroup() {
        Board board = buildRandomBoard();
        board.set(Coordinate.C01_R01, BLACK);
        board.set(Coordinate.C01_R02, BLACK);
        board.set(Coordinate.C02_R01, BLACK);
        board.set(Coordinate.C19_R19, WHITE);
        board.set(Coordinate.C19_R18, WHITE);
        board.set(Coordinate.C18_R19, WHITE);
        
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Group center = new Group(board, coordinate);
            Intersection centerType = board.get(coordinate);
            
            for (Coordinate neighbor : coordinate.getNeighbors()) {
                Group adjacent = new Group(board, neighbor);
                Intersection adjacentType = board.get(neighbor);
                
                if (centerType == adjacentType || centerType.countsAsLiberty() && adjacentType.countsAsLiberty()) {
                    assertEquals(center, adjacent);
                } else {
                    assertNotEquals(center, adjacent);
                }
            }
        }
    }
    
    @Test
    public void equalityCheckingMustConsiderType() {
        Board first = new Board();
        first.set(Coordinate.C01_R01, BLACK);
        Board second = new Board();
        second.set(Coordinate.C01_R01, WHITE);
        
        Group a = new Group(first, Coordinate.C01_R01);
        Group b = new Group(second, Coordinate.C01_R01);
        assertNotEquals(a, b);
    }
    
    @Test
    public void equalityCheckingMustConsiderMembers() {
        Board board = new Board();
        board.set(Coordinate.C01_R01, BLACK);
        board.set(Coordinate.C19_R19, BLACK);
        Group northEast = new Group(board, Coordinate.C01_R01);
        Group southWest = new Group(board, Coordinate.C19_R19);
        assertNotEquals(northEast, southWest);
    }
    
    @Test
    public void groupsCanOnlyEqualGroups() {
        Object[] notGroups = { null, "not a group", new Integer(17) };
        Board board = buildRandomBoard();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Group group = new Group(board, coordinate);
            for (Object notAGroup : notGroups) {
                assertNotEquals(group, notAGroup);
            }
        }
    }
    
    @Test
    public void aGroupAlwaysEqualsItself() {
        Board board = buildRandomBoard();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Group group = new Group(board, coordinate);
            assertEquals(group, group);
        }
    }
    
    @Test
    public void equalGroupsGenerateIdenticalHashCodes() {
        Board board = buildRandomBoard();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Group a = new Group(board, coordinate);
            Group b = new Group(board, coordinate);
            
            assertEquals(a.hashCode(), b.hashCode());
        }
    }
    
    @Test
    public void groupAlwaysReturnsSameHashCode() {
        Board board = buildRandomBoard();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Group a = new Group(board, coordinate);
            int first = a.hashCode();
            int second = a.hashCode();
            assertEquals(first, second);
        }
    }
    
    @Test
    public void toStringIsOverridden() {
        Board board = buildRandomBoard();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Group a = new Group(board, coordinate);
            String representation = a.toString();
            String original = getDefaulToString(a);
            assertNotEquals(original, representation);
        }
    }
    
    @Test
    public void toStringAlwaysReturnsSameString() {
        Board board = buildRandomBoard();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            Group a = new Group(board, coordinate);
            String representation = a.toString();
            String same = a.toString();
            assertTrue(same == representation);
        }
    }
}
