package com.sadakatsu.go.domain;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.sadakatsu.go.domain.exceptions.NoNeighborException;

public class CoordinateTest {
    private static final Coordinate[] COORDINATES = Coordinate.values();
    private static final Direction[] DIRECTIONS = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    
    @Test
    public void thereAre361Coordinates() {
        if (COORDINATES.length != 361) {
            failFormat("There are %d Coordinates.", COORDINATES.length);
        }
    }
    
    private void failFormat( String format, Object...args) {
        String message = String.format(format, args);
        fail(message);
    }
    
    @Test
    public void eachCoordinateHasComponentsBetween1And19() {
        for (Coordinate coordinate : COORDINATES) {
            int column = coordinate.getColumn();
            int row = coordinate.getRow();
            if (column < 1 || column > 19 || row < 1 || row > 19) {
                failFormat("%s has the invalid coordinate pair %d/%d.", coordinate, column, row);
            }
        }
    }
    
    @Test
    public void eachCoordinateHasAUniqueComponents() {
        for (int i = 0; i < COORDINATES.length - 1; ++i) {
            Coordinate first = COORDINATES[i];
            int column = first.getColumn();
            int row = first.getRow();
            for (int j = i + 1; j < COORDINATES.length; ++j) {
                Coordinate second = COORDINATES[j];
                if (column == second.getColumn() && row == second.getRow()) {
                    failFormat(
                        "Two different Coordinates have the same column/row pair of %d/%d: %s and %s.",
                        column,
                        row,
                        first,
                        second
                    );
                }
            }
        }
    }
    
    @Test
    public void iterateOverBoardWithoutArgumentIteratesOverFullBoard() {
        Set<Coordinate> coordinates = new HashSet<>();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            if (coordinates.contains(coordinate)) {
                failFormat("Received a Coordinate more than once from iterateOverBoard(): %s.", coordinate);
            }
            coordinates.add(coordinate);
        }
        if (361 != coordinates.size()) {
            failFormat("iterateOverBoard() returned %d Coordinates.", coordinates.size());
        }
    }
    
    @Test
    public void iterateOverBoardWithArgumentIteratesWithinSpecifiedBound() {
        for (int maxComponent = 1; maxComponent <= 19; ++maxComponent) {
            int expectedCount = maxComponent * maxComponent;
            Set<Coordinate> coordinates = new HashSet<>();
            for (Coordinate coordinate : Coordinate.iterateOverBoard(maxComponent)) {
                if (coordinates.contains(coordinate)) {
                    failFormat(
                        "Received a Coordinate more than once from iterateOverBoard(%d): %s.",
                        maxComponent,
                        coordinate
                    );
                } else if (coordinate.getColumn() > maxComponent || coordinate.getRow() > maxComponent) {
                    failFormat(
                        "iterateOverBoard(%d) returned a Coordinate that it should not: %s.",
                        maxComponent,
                        coordinate
                    );
                }
                
                coordinates.add(coordinate);
            }
            if (expectedCount != coordinates.size()) {
                failFormat("iterateOverBoard(%d) returned %d Coordinates.", expectedCount, coordinates.size()); 
            }
        }
    }
    
    @Test
    public void hasNeighborToThrowsExceptionForNullDirection() {
        for (Coordinate coordinate : COORDINATES) {
            try {
                boolean hasNeighbor = coordinate.hasNeighborTo(null);
                failFormat(
                    "%s.hasNeighborTo(null) should have thrown an IllegalArgumentException, but instead it returned %s.",
                    coordinate,
                    hasNeighbor
                );
            } catch (IllegalArgumentException e) {
                // success
            } catch (Exception e) {
                failFormat(
                    "%s.hasNeighborTo(null) should have thrown an IllegalArgumentException, but instead it threw  %s.",
                    coordinate,
                    e.getClass().getSimpleName()
                );
            }
        }
    }
    
    @Test
    public void hasNeighborToWithMaxComponentThrowsExceptionForNullDirection() {
        for (int maxComponent = 1; maxComponent <= 19; ++maxComponent) {
            for (Coordinate coordinate : COORDINATES) {
                try {
                    boolean hasNeighbor = coordinate.hasNeighborTo(null, maxComponent);
                    failFormat(
                        "%s.hasNeighborTo(null, %d) should have thrown an IllegalArgumentException, but instead it returned %s.",
                        coordinate,
                        maxComponent,
                        hasNeighbor
                    );
                } catch (IllegalArgumentException e) {
                    // success
                } catch (Exception e) {
                    failFormat(
                        "%s.hasNeighborTo(null, %d) should have thrown an IllegalArgumentException, but instead it threw  %s.",
                        coordinate,
                        maxComponent,
                        e.getClass().getSimpleName()
                    );
                }
            }
        }
    }
    
    @Test
    public void hasNeighborToChecksNeighborsForFullBoard() {
        for (Coordinate coordinate : COORDINATES) {
            boolean[] expectations = getExpectedHasNeighborToResults(coordinate, 19);
            for (int i = 0; i < 4; ++i) {
                Direction direction = DIRECTIONS[i];
                boolean expected = expectations[i];
                
                boolean actual = coordinate.hasNeighborTo(direction);
                if (actual != expected) {
                    failFormat(
                        "Coordinate %s reported that it %s a neighbor to the %s when it %s.",
                        coordinate,
                        actual ? "has" : "does not have",
                        direction,
                        expected ? "does" : "does not"
                    );
                }
            }
        }
    }
    
    private boolean[] getExpectedHasNeighborToResults( Coordinate coordinate, int maxComponent ) {
        int column = coordinate.getColumn();
        int row = coordinate.getRow();
        
        boolean[] expectations;
        if (column > maxComponent || row > maxComponent) {
            expectations = new boolean[] { false, false, false, false };
        } else {
            expectations = new boolean[] { row > 1, column < maxComponent, row < maxComponent, column > 1 };
        }
        return expectations;
    }
    
    @Test
    public void hasNeighborToWithMaxComponentThrowsExceptionForInvalidMaxComponent() {
        for (Coordinate coordinate : COORDINATES) {
            for (Direction direction : DIRECTIONS) {
                for (
                    int maxComponent = -10;
                    maxComponent <= 30;
                    maxComponent = (maxComponent == 0 ? 20 : maxComponent + 1)
                ) {
                    try {
                        boolean hasNeighbor = coordinate.hasNeighborTo(direction, maxComponent);
                        failFormat(
                            "%s.hasNeighborTo(%s, %d) should have thrown an IllegalArgumentException, but instead it returned %s.",
                            coordinate,
                            direction,
                            maxComponent,
                            hasNeighbor
                        );
                    } catch (IllegalArgumentException e) {
                        // success
                    } catch (Exception e) {
                        failFormat(
                            "%s.hasNeighborTo(%s, %d) should have thrown an IllegalArgumentException, but instead it threw  %s.",
                            coordinate,
                            direction,
                            maxComponent,
                            e.getClass().getSimpleName()
                        );
                    }
                }
            }
        }
    }
    
    @Test
    public void hasNeighborToWithMaxComponentChecksWhetherNeighborCoordinateIsOnBoard() {
        for (int maxComponent = 1; maxComponent <= 19; ++maxComponent) {
            for (Coordinate coordinate : Coordinate.iterateOverBoard(maxComponent)) {
                boolean[] expectations = getExpectedHasNeighborToResults(coordinate, maxComponent);
                for (int i = 0; i < 4; ++i) {
                    Direction direction = DIRECTIONS[i];
                    boolean expected = expectations[i];
                    
                    boolean actual = coordinate.hasNeighborTo(direction, maxComponent);
                    if (actual != expected) {
                        failFormat(
                            "Coordinate %s reported that it %s a neighbor to the %s for max component %d when it %s.",
                            coordinate,
                            actual ? "has" : "does not have",
                            direction,
                            maxComponent,
                            expected ? "does" : "does not"
                        );
                    }
                }
            }
        }
    }
    
    @Test
    public void hasNeighborToWithMaxComponentIsAlwaysFalseWhenQueriedCoordinateIsNotOnBoard() {
        for (int maxComponent = 1; maxComponent < 19; ++maxComponent) {
            Set<Coordinate> excluded = Sets.newHashSet(Coordinate.iterateOverBoard(maxComponent));
            Set<Coordinate> remainder = Sets.newHashSet(COORDINATES);
            remainder.removeAll(excluded);
            
            for (Coordinate coordinate : remainder) {
                for (Direction direction : DIRECTIONS) {
                    boolean result = coordinate.hasNeighborTo(direction, maxComponent);
                    if (result) {
                        failFormat(
                            "Coordinate %s is not on a board with a max component of %d, yet it reported that it has a neighbor to the %s.",
                            coordinate,
                            maxComponent,
                            direction
                        );
                    }
                }
            }
        }
    }
    
    // TODO: Write tests for getNeighborTo(Direction) and getNeighborTo(Direction, int) to test for thrown Exceptions
    // for invalid arguments.
    @Test
    public void getNeighborToThrowsExceptionForNullDirection() {
        for (Coordinate coordinate : COORDINATES) {
            try {
                Coordinate wrong = coordinate.getNeighborTo(null);
                failFormat(
                    "%s.getNeighborTo(null) should throw an IllegalArgumentException, but instead it returned %s.",
                    coordinate,
                    wrong
                );
            } catch (IllegalArgumentException e) {
                // success
            } catch (Exception e) {
                failFormat(
                    "%s.getNeighborTo(null) should throw an IllegalArgumentException, but instead it threw %s.",
                    coordinate,
                    e.getClass().getSimpleName()
                );
            }
        }
    }
    
    @Test
    public void getNeighborToWithMaxComponentThrowsExceptionForNullDirection() {
        for (int maxComponent = 1; maxComponent <= 10; ++maxComponent) {
            for (Coordinate coordinate : COORDINATES) {
                try {
                    Coordinate wrong = coordinate.getNeighborTo(null, maxComponent);
                    failFormat(
                        "%s.getNeighborTo(null, %d) should throw an IllegalArgumentException, but instead it returned %s.",
                        coordinate,
                        maxComponent,
                        wrong
                    );
                } catch (IllegalArgumentException e) {
                    // success
                } catch (Exception e) {
                    failFormat(
                        "%s.getNeighborTo(null, %d) should throw an IllegalArgumentException, but instead it threw %s.",
                        coordinate,
                        maxComponent,
                        e.getClass().getSimpleName()
                    );
                }
            }
        }
    }
    
    @Test
    public void getNeighborToWithMaxComponentThrowsExceptionForInvalidMaxComponent() {
        for (int maxComponent = -10; maxComponent <= 30; maxComponent += (maxComponent == 0 ? 20 : 1)) {
            for (Coordinate coordinate : COORDINATES) {
                for (Direction direction : DIRECTIONS) {
                    try {
                        Coordinate wrong = coordinate.getNeighborTo(direction, maxComponent);
                        failFormat(
                            "%s.getNeighborTo(%s, %d) should throw an IllegalArgumentException, but instead it returned %s.",
                            coordinate,
                            direction,
                            maxComponent,
                            wrong
                        );
                    } catch (IllegalArgumentException e) {
                        // success
                    } catch (Exception e) {
                        failFormat(
                            "%s.getNeighborTo(%s, %d) should throw an IllegalArgumentException, but instead it threw %s.",
                            coordinate,
                            direction,
                            maxComponent,
                            e.getClass().getSimpleName()
                        );
                    }
                }
            }
        }
    }
    
    // Okay, I admit, this test probably does WAY too much.  However, I found it easier to conceptualize the related
    // behaviors of getNeighborTo(Direction) and getNeighborTo(Direction, int) together.  I might be able to rethink
    // this into multiple tests later, but for now this should cover everything.
    //
    // This method tests the following:
    // - If either hasNeighborTo(Direction) or hasNeighborTo(Direction, int) would return false, then a call to
    //   getNeighborTo(Direction) or getNeighborTo(Direction, int) with the same arguments should throw
    //   NoNeighborException.
    // - No arguments to either getNeighborTo(Direction) and getNeighborTo(Direction, int) ever result in a return of
    //   null.
    // - If a call to hasNeighborTo(Direction) or hasNeighborTo(Direction, int) returns true, then a call to
    //   getNeighborTo(Direction) or getNeighborTo(Direction, int) with the same arguments should return a Coordinate
    //   that is properly displaced from the starting coordinate.
    @Test
    public void testGetNeighborToComprehensively() {
        for (
            Integer maxComponent = null;
            maxComponent == null || maxComponent <= 19;
            maxComponent = (maxComponent == null ? 1 : maxComponent + 1)
        ) {
            for (Coordinate coordinate : COORDINATES) {
                boolean[] expectations = getExpectedHasNeighborToResults(
                    coordinate,
                    maxComponent == null ? 19 : maxComponent
                );
                for (int i = 0; i < 4; ++i) {
                    Direction direction = DIRECTIONS[i];
                    boolean expected = expectations[i];
                    
                    if (expected) {
                        try {
                            Coordinate neighbor = (
                                maxComponent == null ?
                                coordinate.getNeighborTo(direction) :
                                coordinate.getNeighborTo(direction, maxComponent)
                            );
                            testIsRightNeighbor(coordinate, direction, neighbor, null);
                        } catch (Exception e) {
                            failFormat(
                                "%s.getNeighborTo(%s%s) should have returned a Coordinate, but instead it threw %s.",
                                coordinate,
                                direction,
                                getMaxComponentVariableString(maxComponent),
                                e.getClass().getSimpleName()
                            );
                        }
                    } else {
                        try {
                            Coordinate wrong = (
                                maxComponent == null ?
                                coordinate.getNeighborTo(direction) :
                                coordinate.getNeighborTo(direction, maxComponent)
                            );
                            failFormat(
                                "%s.getNeighborTo(%s%s) should have thrown a NoNeighborException, but instead it returned %s.",
                                coordinate,
                                direction,
                                getMaxComponentVariableString(maxComponent),
                                wrong
                            );
                        } catch (NoNeighborException e) {
                            // success
                        } catch (Exception e) {
                            failFormat(
                                "%s.getNeighborTo(%s%s) should have thrown a NoNeighborException, but instead it threw %s. ",
                                coordinate,
                                direction,
                                getMaxComponentVariableString(maxComponent),
                                e.getClass().getSimpleName()
                            );
                        }
                    }
                }
            }
        }
    }
    
    private String getMaxComponentVariableString( Integer maxComponent ) {
        return maxComponent != null ? ", " + maxComponent : "";
    }
    
    private void testIsRightNeighbor(
        Coordinate coordinate,
        Direction direction,
        Coordinate neighbor,
        Integer maxComponent
    ) {
        if (neighbor == null) {
            failFormat(
                "%s.getNeighborTo(%s%s) returned null.",
                coordinate,
                direction,
                getMaxComponentVariableString(maxComponent)
            );
        }
        
        int columnDelta = neighbor.getColumn() - coordinate.getColumn();
        int rowDelta = neighbor.getRow() - coordinate.getRow();
        
        boolean isCorrect = false;
        switch (direction) {
            case EAST:
                isCorrect = columnDelta == 1 && rowDelta == 0;
                break;
            
            case NORTH:
                isCorrect = columnDelta == 0 && rowDelta == -1;
                break;
            
            case SOUTH:
                isCorrect = columnDelta == 0 && rowDelta == 1;
                break;
            
            case WEST:
                isCorrect = columnDelta == -1 && rowDelta == 0;
                break;
        }
        if (!isCorrect) {
            failFormat(
                "%s.getNeighborTo(%s%s) returned %s.",
                coordinate,
                direction,
                getMaxComponentVariableString(maxComponent),
                neighbor
            );
        }
    }
    
    @Test
    public void getNeighborsReturnsExpectedNeighborsOnFullSizeBoard() {
        for (Coordinate coordinate : COORDINATES) {
            Set<Coordinate> expected = null;
            try {
                expected = getExpectedNeighbors(coordinate, 19);
            } catch (NoNeighborException e) {
                failFormat(
                    "Could not test %s.getNeighbors() because of a NoNeighborException while generating the expected set: %s\n%s",
                    coordinate,
                    e.getMessage(),
                    e.getStackTrace()
                );
            }
            
            try {
                Set<Coordinate> actual = Sets.newHashSet(coordinate.getNeighbors());
                if (!expected.equals(actual)) {
                    failFormat(
                        "%s.getNeighbors() was expected to return the Coordinates in %s but instead returned the Coordinates %s.",
                        coordinate,
                        expected,
                        actual
                    );
                }
            } catch (Exception e) {
                failFormat(
                    "%s.getNeighbors() should return an Iterable<Coordinate>, but instead it threw %s: %s\n%s.",
                    coordinate,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())
                );
            }
        }
    }
    
    private Set<Coordinate> getExpectedNeighbors( Coordinate coordinate, int maxComponent ) throws NoNeighborException {
        Set<Coordinate> expectedNeighbors = new HashSet<>(4);
        
        boolean[] expectations = getExpectedHasNeighborToResults(coordinate, 19);
        for (int i = 0; i < 4; ++i) {
            if (expectations[i]) {
                Coordinate neighbor = coordinate.getNeighborTo(DIRECTIONS[i]);
                expectedNeighbors.add(neighbor);
            }
        }
        
        return expectedNeighbors;
    }
    
    @Test
    public void getNeighborsReturnsWithMaxComponentThrowsExceptionForInvalidMaxComponent() {
        for (
            int maxComponent = -10;
            maxComponent <= 30;
            maxComponent += (maxComponent == 0 ? 20 : 1)
        ) {
            for (Coordinate coordinate : COORDINATES) {
                try {
                    Iterable<Coordinate> wrong = coordinate.getNeighbors(maxComponent);
                    failFormat(
                        "%s.getNeighbors(%d) should throw an IllegalArgumentException, but instead it returned %s.",
                        coordinate,
                        wrong == null ? wrong : Sets.newHashSet(wrong)
                    );
                } catch (IllegalArgumentException e) {
                    // success
                } catch (Exception e) {
                    failFormat(
                        "%s.getNeighbors(%d) should throw an IllegalArgumentException, but instead it threw %s: %s\n%s.",
                        coordinate,
                        maxComponent,
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        Arrays.toString(e.getStackTrace())
                    );
                }
            }
        }
    }
    
    @Test
    public void getNeighborsWithMaxComponentReturnsExpectedNeighborsForBoardSize() {
        for (int maxComponent = 1; maxComponent <= 19; ++maxComponent) {
            for (Coordinate coordinate : COORDINATES) {
                Set<Coordinate> expected = null;
                try {
                    expected = getExpectedNeighbors(coordinate, maxComponent);
                } catch (NoNeighborException e) {
                    failFormat(
                        "Could not test %s.getNeighbors(%d) because of a NoNeighborException while generating the expected set: %s\n%s",
                        coordinate,
                        maxComponent,
                        e.getMessage(),
                        e.getStackTrace()
                    );
                }
                
                try {
                    Set<Coordinate> actual = Sets.newHashSet(coordinate.getNeighbors());
                    if (!expected.equals(actual)) {
                        failFormat(
                            "%s.getNeighbors(%d) was expected to return the Coordinates in %s but instead returned the Coordinates %s.",
                            coordinate,
                            maxComponent,
                            expected,
                            actual
                        );
                    }
                } catch (Exception e) {
                    failFormat(
                        "%s.getNeighbors(%d) should return an Iterable<Coordinate>, but instead it threw %s: %s\n%s.",
                        coordinate,
                        maxComponent,
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        Arrays.toString(e.getStackTrace())
                    );
                }
            }
        }
    }
    
    @Test
    public void getReturnsCoordinateThatMatchesArguments() {
        for (Coordinate expected : COORDINATES) {
            int column = expected.getColumn();
            int row = expected.getRow();
            try {
            Coordinate actual = Coordinate.get(column, row);
                if (expected != actual) {
                    failFormat(
                        "Coordinate.get(%d, %d) should have returned %s but it returned %s.",
                        column,
                        row,
                        expected,
                        actual
                    );
                }
            } catch (Exception e) {
                failFormat(
                    "Coordinate.get(%d, %d) should have returned %s, but instead it threw %s: %s\n%s",
                    column,
                    row,
                    expected,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())
                );
            }
        }
    }
    
    @Test
    public void getThrowsExceptionForAnyInvalidComponents() {
        int[] invalid = { -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 };
        int[] valid = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
        
        for (int column : invalid) {
            for (int row : valid) {
                testGetThrowsException(column, row);
            }
        }
        
        for (int column : valid) {
            for (int row : invalid) {
                testGetThrowsException(column, row);
            }
        }
        
        for (int column : invalid) {
            for (int row : invalid) {
                testGetThrowsException(column, row);
            }
        }
    }
    
    private void testGetThrowsException( int column, int row ) {
        try {
            Coordinate wrong = Coordinate.get(column, row);
            failFormat(
                "Coordinate.get(%d, %d) should have thrown an IllegalArgumentException, but instead it returned %s.",
                column,
                row,
                wrong
            );
        } catch (IllegalArgumentException e) {
            // success
        } catch (Exception e) {
            failFormat(
                "Coordinate.get(%d, %d) should have thrown an IllegalArgumentException, but instead it threw %s: %s\n%s",
                column,
                row,
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
            );
        }
    }
    
    @Test
    public void valuesAreNamedUsingFormatThatPresentsTheirColumnAndRow() {
        for (int column = 1; column <= 19; ++column) {
            for (int row = 1; row <= 19; ++row) {
                Coordinate expected = Coordinate.get(column, row);
                String name = String.format("C%02d_R%02d", column, row);
                Coordinate actual = Coordinate.valueOf(name);
                if (expected != actual) {
                    failFormat(
                        "Coordinate.valueOf(\"%s\") should have returned %s, but instead it returned %s.",
                        name,
                        expected,
                        actual
                    );
                }
            }
        }
    }
}
