package com.sadakatsu.go.domain;

import static com.sadakatsu.go.domain.Direction.*;

import java.util.Iterator;

import com.sadakatsu.go.domain.exceptions.NoNeighborException;

/**
 * The Coordinate refers to an intersection on a Go board.  These are intended to use as a consistent look-up that can
 * be used throughout the domain that can prevent (or at least minimize) the need for communicating objects to calculate
 * coordinates and passing two arguments to each others' methods.
 * <p>
 * Each instance is named in a column-major matrix coordinate style using Black's perspective as the reference.  The
 * columns progress from Direction.WEST to Direction.EAST with increasing column values of 1 to 19.  Similarly, the rows
 * progress from Direction.NORTH to Direction.SOUTH with increasing row values from 1 to 19.  These column/row values
 * then capture this information with the format "C##_R##".  {@code C01_R01} refers to the northwest corner of the
 * board, {@code C10_R10} refers to the tengen, {@code C19_R19} refers to the southeast corner of the board, and
 * {@code C17_R04} refers to the common Black komoku first move.
 * <p>
 * Many static and instance methods in this class are overloaded to receive an argument called {@code maxComponent}.
 * Based on the reasonable assumption that a Go board will always be square but not necessarily the full 19x19, these
 * variants enable querying based upon other board sizes.  For example, to iterate over the Coordinates of a 9x9 board,
 * call {@code Coordinate.iterateOverBoard(9)}.  In every case, the method versions that lack the {@code maxComponent}
 * parameter defaults to a full 19x19 board, effectively using a {@code maxComponent} of 19.
 * 
 * @see com.sadakatsu.go.domain.Direction
 */
public enum Coordinate implements Move {
    // ENUM VALUES /////////////////////////////////////////////////////////////////////////////////////////////////////
    C01_R01 (1, 1),
    C01_R02 (1, 2),
    C01_R03 (1, 3),
    C01_R04 (1, 4),
    C01_R05 (1, 5),
    C01_R06 (1, 6),
    C01_R07 (1, 7),
    C01_R08 (1, 8),
    C01_R09 (1, 9),
    C01_R10 (1, 10),
    C01_R11 (1, 11),
    C01_R12 (1, 12),
    C01_R13 (1, 13),
    C01_R14 (1, 14),
    C01_R15 (1, 15),
    C01_R16 (1, 16),
    C01_R17 (1, 17),
    C01_R18 (1, 18),
    C01_R19 (1, 19),
    C02_R01 (2, 1),
    C02_R02 (2, 2),
    C02_R03 (2, 3),
    C02_R04 (2, 4),
    C02_R05 (2, 5),
    C02_R06 (2, 6),
    C02_R07 (2, 7),
    C02_R08 (2, 8),
    C02_R09 (2, 9),
    C02_R10 (2, 10),
    C02_R11 (2, 11),
    C02_R12 (2, 12),
    C02_R13 (2, 13),
    C02_R14 (2, 14),
    C02_R15 (2, 15),
    C02_R16 (2, 16),
    C02_R17 (2, 17),
    C02_R18 (2, 18),
    C02_R19 (2, 19),
    C03_R01 (3, 1),
    C03_R02 (3, 2),
    C03_R03 (3, 3),
    C03_R04 (3, 4),
    C03_R05 (3, 5),
    C03_R06 (3, 6),
    C03_R07 (3, 7),
    C03_R08 (3, 8),
    C03_R09 (3, 9),
    C03_R10 (3, 10),
    C03_R11 (3, 11),
    C03_R12 (3, 12),
    C03_R13 (3, 13),
    C03_R14 (3, 14),
    C03_R15 (3, 15),
    C03_R16 (3, 16),
    C03_R17 (3, 17),
    C03_R18 (3, 18),
    C03_R19 (3, 19),
    C04_R01 (4, 1),
    C04_R02 (4, 2),
    C04_R03 (4, 3),
    C04_R04 (4, 4),
    C04_R05 (4, 5),
    C04_R06 (4, 6),
    C04_R07 (4, 7),
    C04_R08 (4, 8),
    C04_R09 (4, 9),
    C04_R10 (4, 10),
    C04_R11 (4, 11),
    C04_R12 (4, 12),
    C04_R13 (4, 13),
    C04_R14 (4, 14),
    C04_R15 (4, 15),
    C04_R16 (4, 16),
    C04_R17 (4, 17),
    C04_R18 (4, 18),
    C04_R19 (4, 19),
    C05_R01 (5, 1),
    C05_R02 (5, 2),
    C05_R03 (5, 3),
    C05_R04 (5, 4),
    C05_R05 (5, 5),
    C05_R06 (5, 6),
    C05_R07 (5, 7),
    C05_R08 (5, 8),
    C05_R09 (5, 9),
    C05_R10 (5, 10),
    C05_R11 (5, 11),
    C05_R12 (5, 12),
    C05_R13 (5, 13),
    C05_R14 (5, 14),
    C05_R15 (5, 15),
    C05_R16 (5, 16),
    C05_R17 (5, 17),
    C05_R18 (5, 18),
    C05_R19 (5, 19),
    C06_R01 (6, 1),
    C06_R02 (6, 2),
    C06_R03 (6, 3),
    C06_R04 (6, 4),
    C06_R05 (6, 5),
    C06_R06 (6, 6),
    C06_R07 (6, 7),
    C06_R08 (6, 8),
    C06_R09 (6, 9),
    C06_R10 (6, 10),
    C06_R11 (6, 11),
    C06_R12 (6, 12),
    C06_R13 (6, 13),
    C06_R14 (6, 14),
    C06_R15 (6, 15),
    C06_R16 (6, 16),
    C06_R17 (6, 17),
    C06_R18 (6, 18),
    C06_R19 (6, 19),
    C07_R01 (7, 1),
    C07_R02 (7, 2),
    C07_R03 (7, 3),
    C07_R04 (7, 4),
    C07_R05 (7, 5),
    C07_R06 (7, 6),
    C07_R07 (7, 7),
    C07_R08 (7, 8),
    C07_R09 (7, 9),
    C07_R10 (7, 10),
    C07_R11 (7, 11),
    C07_R12 (7, 12),
    C07_R13 (7, 13),
    C07_R14 (7, 14),
    C07_R15 (7, 15),
    C07_R16 (7, 16),
    C07_R17 (7, 17),
    C07_R18 (7, 18),
    C07_R19 (7, 19),
    C08_R01 (8, 1),
    C08_R02 (8, 2),
    C08_R03 (8, 3),
    C08_R04 (8, 4),
    C08_R05 (8, 5),
    C08_R06 (8, 6),
    C08_R07 (8, 7),
    C08_R08 (8, 8),
    C08_R09 (8, 9),
    C08_R10 (8, 10),
    C08_R11 (8, 11),
    C08_R12 (8, 12),
    C08_R13 (8, 13),
    C08_R14 (8, 14),
    C08_R15 (8, 15),
    C08_R16 (8, 16),
    C08_R17 (8, 17),
    C08_R18 (8, 18),
    C08_R19 (8, 19),
    C09_R01 (9, 1),
    C09_R02 (9, 2),
    C09_R03 (9, 3),
    C09_R04 (9, 4),
    C09_R05 (9, 5),
    C09_R06 (9, 6),
    C09_R07 (9, 7),
    C09_R08 (9, 8),
    C09_R09 (9, 9),
    C09_R10 (9, 10),
    C09_R11 (9, 11),
    C09_R12 (9, 12),
    C09_R13 (9, 13),
    C09_R14 (9, 14),
    C09_R15 (9, 15),
    C09_R16 (9, 16),
    C09_R17 (9, 17),
    C09_R18 (9, 18),
    C09_R19 (9, 19),
    C10_R01 (10, 1),
    C10_R02 (10, 2),
    C10_R03 (10, 3),
    C10_R04 (10, 4),
    C10_R05 (10, 5),
    C10_R06 (10, 6),
    C10_R07 (10, 7),
    C10_R08 (10, 8),
    C10_R09 (10, 9),
    C10_R10 (10, 10),
    C10_R11 (10, 11),
    C10_R12 (10, 12),
    C10_R13 (10, 13),
    C10_R14 (10, 14),
    C10_R15 (10, 15),
    C10_R16 (10, 16),
    C10_R17 (10, 17),
    C10_R18 (10, 18),
    C10_R19 (10, 19),
    C11_R01 (11, 1),
    C11_R02 (11, 2),
    C11_R03 (11, 3),
    C11_R04 (11, 4),
    C11_R05 (11, 5),
    C11_R06 (11, 6),
    C11_R07 (11, 7),
    C11_R08 (11, 8),
    C11_R09 (11, 9),
    C11_R10 (11, 10),
    C11_R11 (11, 11),
    C11_R12 (11, 12),
    C11_R13 (11, 13),
    C11_R14 (11, 14),
    C11_R15 (11, 15),
    C11_R16 (11, 16),
    C11_R17 (11, 17),
    C11_R18 (11, 18),
    C11_R19 (11, 19),
    C12_R01 (12, 1),
    C12_R02 (12, 2),
    C12_R03 (12, 3),
    C12_R04 (12, 4),
    C12_R05 (12, 5),
    C12_R06 (12, 6),
    C12_R07 (12, 7),
    C12_R08 (12, 8),
    C12_R09 (12, 9),
    C12_R10 (12, 10),
    C12_R11 (12, 11),
    C12_R12 (12, 12),
    C12_R13 (12, 13),
    C12_R14 (12, 14),
    C12_R15 (12, 15),
    C12_R16 (12, 16),
    C12_R17 (12, 17),
    C12_R18 (12, 18),
    C12_R19 (12, 19),
    C13_R01 (13, 1),
    C13_R02 (13, 2),
    C13_R03 (13, 3),
    C13_R04 (13, 4),
    C13_R05 (13, 5),
    C13_R06 (13, 6),
    C13_R07 (13, 7),
    C13_R08 (13, 8),
    C13_R09 (13, 9),
    C13_R10 (13, 10),
    C13_R11 (13, 11),
    C13_R12 (13, 12),
    C13_R13 (13, 13),
    C13_R14 (13, 14),
    C13_R15 (13, 15),
    C13_R16 (13, 16),
    C13_R17 (13, 17),
    C13_R18 (13, 18),
    C13_R19 (13, 19),
    C14_R01 (14, 1),
    C14_R02 (14, 2),
    C14_R03 (14, 3),
    C14_R04 (14, 4),
    C14_R05 (14, 5),
    C14_R06 (14, 6),
    C14_R07 (14, 7),
    C14_R08 (14, 8),
    C14_R09 (14, 9),
    C14_R10 (14, 10),
    C14_R11 (14, 11),
    C14_R12 (14, 12),
    C14_R13 (14, 13),
    C14_R14 (14, 14),
    C14_R15 (14, 15),
    C14_R16 (14, 16),
    C14_R17 (14, 17),
    C14_R18 (14, 18),
    C14_R19 (14, 19),
    C15_R01 (15, 1),
    C15_R02 (15, 2),
    C15_R03 (15, 3),
    C15_R04 (15, 4),
    C15_R05 (15, 5),
    C15_R06 (15, 6),
    C15_R07 (15, 7),
    C15_R08 (15, 8),
    C15_R09 (15, 9),
    C15_R10 (15, 10),
    C15_R11 (15, 11),
    C15_R12 (15, 12),
    C15_R13 (15, 13),
    C15_R14 (15, 14),
    C15_R15 (15, 15),
    C15_R16 (15, 16),
    C15_R17 (15, 17),
    C15_R18 (15, 18),
    C15_R19 (15, 19),
    C16_R01 (16, 1),
    C16_R02 (16, 2),
    C16_R03 (16, 3),
    C16_R04 (16, 4),
    C16_R05 (16, 5),
    C16_R06 (16, 6),
    C16_R07 (16, 7),
    C16_R08 (16, 8),
    C16_R09 (16, 9),
    C16_R10 (16, 10),
    C16_R11 (16, 11),
    C16_R12 (16, 12),
    C16_R13 (16, 13),
    C16_R14 (16, 14),
    C16_R15 (16, 15),
    C16_R16 (16, 16),
    C16_R17 (16, 17),
    C16_R18 (16, 18),
    C16_R19 (16, 19),
    C17_R01 (17, 1),
    C17_R02 (17, 2),
    C17_R03 (17, 3),
    C17_R04 (17, 4),
    C17_R05 (17, 5),
    C17_R06 (17, 6),
    C17_R07 (17, 7),
    C17_R08 (17, 8),
    C17_R09 (17, 9),
    C17_R10 (17, 10),
    C17_R11 (17, 11),
    C17_R12 (17, 12),
    C17_R13 (17, 13),
    C17_R14 (17, 14),
    C17_R15 (17, 15),
    C17_R16 (17, 16),
    C17_R17 (17, 17),
    C17_R18 (17, 18),
    C17_R19 (17, 19),
    C18_R01 (18, 1),
    C18_R02 (18, 2),
    C18_R03 (18, 3),
    C18_R04 (18, 4),
    C18_R05 (18, 5),
    C18_R06 (18, 6),
    C18_R07 (18, 7),
    C18_R08 (18, 8),
    C18_R09 (18, 9),
    C18_R10 (18, 10),
    C18_R11 (18, 11),
    C18_R12 (18, 12),
    C18_R13 (18, 13),
    C18_R14 (18, 14),
    C18_R15 (18, 15),
    C18_R16 (18, 16),
    C18_R17 (18, 17),
    C18_R18 (18, 18),
    C18_R19 (18, 19),
    C19_R01 (19, 1),
    C19_R02 (19, 2),
    C19_R03 (19, 3),
    C19_R04 (19, 4),
    C19_R05 (19, 5),
    C19_R06 (19, 6),
    C19_R07 (19, 7),
    C19_R08 (19, 8),
    C19_R09 (19, 9),
    C19_R10 (19, 10),
    C19_R11 (19, 11),
    C19_R12 (19, 12),
    C19_R13 (19, 13),
    C19_R14 (19, 14),
    C19_R15 (19, 15),
    C19_R16 (19, 16),
    C19_R17 (19, 17),
    C19_R18 (19, 18),
    C19_R19 (19, 19);
    
    // NESTED TYPES ////////////////////////////////////////////////////////////////////////////////////////////////////
    // It was convenient to use an internal class to store the column/row pair.  Components serves this purpose.
    private static final class Components {
        int column;
        int row;
        
        Components( int column, int row ) {
            this.column = column;
            this.row = row;
        }
    }
    
    // This type allows for lazy iteration over a Coordinate's neighbors given the maximum component.
    private static final class NeighborIterator implements Iterable<Coordinate>, Iterator<Coordinate> {
        private static final Direction[] DIRECTIONS = Direction.values();
        
        private final Coordinate center;
        private final int maxComponent;
        
        private int directionIndex;
        
        NeighborIterator( Coordinate center, int maxComponent ) {
            validateCoordinate(center);
            validateMaxComponent(maxComponent);
            this.center = center;
            this.maxComponent = maxComponent;
            this.directionIndex = -1;
            findNext();
        }
        
        private void findNext() {
            do {
                ++directionIndex;
            } while (directionIndex < DIRECTIONS.length && !center.hasNeighborTo(getCurrentDirection(), maxComponent));
        }
        
        private Direction getCurrentDirection() {
            return DIRECTIONS[directionIndex];
        }
        
        @Override
        public boolean hasNext() {
            return directionIndex < DIRECTIONS.length;
        }

        @Override
        public Coordinate next() {
            Coordinate neighbor = null;
            
            try {
                neighbor = center.getNeighborTo(getCurrentDirection(), maxComponent);
                findNext();
            } catch (NoNeighborException e) {
                // findNext() and hasNext() should ensure that this never happens.
                throw new IllegalStateException(e);
            }
            
            return neighbor;
        }

        @Override
        public Iterator<Coordinate> iterator() {
            return this;
        }
    }
    
    // This type allows for lazy iteration over all the Coordinates of a board with a given maximum component.
    private static final class BoardIterator implements Iterable<Coordinate>, Iterator<Coordinate> {
        private final int maxComponent;
        
        private int column;
        private int row;
        
        BoardIterator( int maxComponent ) {
            validateMaxComponent(maxComponent);
            
            this.maxComponent = maxComponent;
            this.column = 1;
            this.row = 1;
        }
        
        @Override
        public boolean hasNext() {
            return row <= maxComponent;
        }

        @Override
        public Coordinate next() {
            Coordinate coordinate = getCoordinate(new Components(column, row));
            
            if (column < maxComponent) {
                ++column;
            } else {
                ++row;
                column = 1;
            }
            
            return coordinate;
        }

        @Override
        public Iterator<Coordinate> iterator() {
            return this;
        }
    }
    
    // STATIC FIELDS ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int MAX_COMPONENT = 19;
    private static final Coordinate[][] LAYOUT = new Coordinate[MAX_COMPONENT][MAX_COMPONENT];
    static {
        for (Coordinate coordinate : values()) {
            setLayout(coordinate);
        }
    }
    
    // STATIC METHODS //////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This convenience method allows for rapid lookup of a Coordinate given its column and row components.  It is most
     * likely useful as part of interpreting other Coordinate representations (perhaps Strings from SGF or GTP) into
     * these coordinates.
     * 
     * @param column The column component of the desired Coordinate.
     * @param row The row component of the desired Coordinate.
     * @return The Coordinate that matches the column/row pair of components.
     * @throws IllegalArgumentException if either {@code column} or {@code row} is less than 1 or greater than 19.
     */
    public static Coordinate get( int column, int row ) {
        if (!isComponentValid(column, MAX_COMPONENT) || !isComponentValid(row, MAX_COMPONENT)) {
            String message = String.format("Received an invalid column/row pair: %d - %d.", column, row);
            throw new IllegalArgumentException(message);
        }
        Components components = new Components(column, row);
        return getCoordinate(components);
    }
    
    private static boolean isComponentValid( int component, int maxComponent ) {
        return component >= 1 && component <= maxComponent;
    }
    
    /**
     * Returns an Iterable that produces a lazy Iterator for processing all of the Coordinates on a 19x19 board.
     * @return an Iterable for iterating over all the Coordinates of a 19x19 board.
     */
    public static Iterable<Coordinate> iterateOverBoard() {
        return iterateOverBoard(MAX_COMPONENT);
    }
    
    /**
     * Returns an Iterable that produces a lazy Iterator for only the subset of Coordinates that apply to a board with
     * a size that matches the passed maximum component.  This allows iteration over 9x9 and 13x13 boards as well as
     * custom sizes.
     * @param maxComponent The number of rows and columns on the desired board
     * @return an Iterable for iterating over all the Coordinates of a {@code maxComponent}x{@code maxComponent} board
     * @throws an IllegalArgumentException if {@code maxComponent} is less than 1 or greater than 19
     */
    public static Iterable<Coordinate> iterateOverBoard( int maxComponent ) {
        validateMaxComponent(maxComponent);
        return new BoardIterator(maxComponent);
    }
    
    private static void validateCoordinate( Coordinate coordinate ) {
        if (coordinate == null) {
            throw new IllegalArgumentException("Coordinate may not be null.");
        }
    }
    
    private static void validateMaxComponent( int maxComponent ) {
        if (maxComponent < 1 || maxComponent > MAX_COMPONENT) {
            String message = String.format(
                "Received invalid max component %d; should be between 1 and 19 inclusive.",
                maxComponent
            );
            throw new IllegalArgumentException(message);
        }
    }
    
    private static Coordinate getCoordinate ( Components components ) {
        return LAYOUT[components.row - 1][components.column - 1];
    }
    
    private static void setLayout( Coordinate coordinate ) {
        LAYOUT[coordinate.getRow() - 1][coordinate.getColumn() - 1] = coordinate;
    }
    
    // INSTANCE CODE ///////////////////////////////////////////////////////////////////////////////////////////////////
    private final Components components;
    
    private Coordinate( int column, int row ) {
        components = new Components(column, row);
    }
    
    /**
     * @return The index of the column of this Coordinate on the board.
     */
    public int getColumn() {
        return components.column;
    }
    
    /**
     * @return The index of the row of this Coordinate on the board.
     */
    public int getRow() {
        return components.row;
    }
    
    /**
     * Determines whether there is a Coordinate that is adjacent to this Coordinate in the passed direction.  Every
     * Coordinate that is on the second line or higher (meaning that neither of its components are 1 or 19) will return
     * {@code true} for every Direction since they have four neighbors.  A Coordinate that is on the side (meaning that
     * only one of its components is 1 or 19) will have three neighbors and will return {@code false} for the one
     * Direction that leads off the board.  A Coordinate in the corner (both components are 1 or 19) will have two
     * neighbors and two Directions that return false.
     * @param direction the Direction in which to step one over to check whether a Coordinate exists on the board
     * @return {@code true} iff there is a Coordinate on the board that is to the {@code direction} of this Coordinate
     * @throws IllegalArgumentException if {@code direction} is {@code null}
     */
    public boolean hasNeighborTo( Direction direction ) {
        return hasNeighborTo(direction, MAX_COMPONENT);
    }
    
    /**
     * Determines whether there is a Coordinate that is adjacent to this Coordinate in the passed direction for the
     * requested board size.  This method is very similar to {@link #hasNeighborTo(Direction)}, but it has the following
     * important differences:
     * <ul>
     * <li>{@link #hasNeighborTo(Direction)} only returns {@code false} in cases where no Coordinate instance exists in
     * the requested direction (since there is a 1:1 correspondence between the coordinates of a 19x19 board and the
     * Coordinate instances).  This method allows querying against other board sizes, so any Coordinate instance with
     * either a column or row component that is greater than {@code maxComponent} is actually off the board and is not a
     * neighbor.  For example, {@code C09_R09} is the southeast corner of a 9x9 board.  If {@code Direction.EAST} and
     * 9 were passed as the arguments to this method, it would return {@code false} because {@code C10_R09} is off the
     * edge of a 9x9 board.
     * <li>Since every Coordinate is a valid Coordinate on a 19x19 board, every Coordinate will have at least two
     * neighbors for {@link #hasNeighborTo(Direction)}.  This method might be called on a Coordinate where the passed
     * {@code maxComponent} means that this Coordinate is itself not on the board.  To prevent any potential confusion
     * with flood searching over a board (for example, when trying to determine groups), Coordinates that are not on the
     * requested board size will return {@code false} for every direction.  Thus, {@code
     * C10_R09.hasNeighborTo(Direction.WEST, 9)} will return {@code false} to prevent accidentally "entering" the board
     * from outside.
     * </ul>
     * @param direction the Direction in which to step one over to check whether a Coordinate exists on the board
     * @param maxComponent The number of rows and columns on the desired board
     * @return {@code true} iff this Coordinate is on the desired board and there is also another Coordinate on the
     * desired board that is to the {@code direction} of this Coordinate
     * @throws IllegalArgumentException if {@direction} is {@code null}, or {@code maxComponent} is less than 1 or
     * greater than 19
     */
    public boolean hasNeighborTo( Direction direction, int maxComponent ) {
        validateDirection(direction);
        validateMaxComponent(maxComponent);
        
        boolean result = false;
        
        if (areComponentsValid(this.components, maxComponent)) {
            Components neighborComponents = getComponentsFor(direction);
            result = areComponentsValid(neighborComponents, maxComponent);
        }
        
        return result;
    }
    
    private Components getComponentsFor( Direction direction ) {
        validateDirection(direction);
        
        int column = components.column;
        int row = components.row;
        
        if (direction == EAST) {
            ++column;
        } else if (direction == WEST) {
            --column;
        } else if (direction == NORTH) {
            --row;
        } else {
            ++row;
        }
        
        return new Components(column, row);
    }
    
    private void validateDirection( Direction direction ) {
        if (direction == null) {
            throw new IllegalArgumentException("The Direction may not be null.");
        }
    }
    
    private boolean areComponentsValid( Components components, int maxComponent ) {
        return isComponentValid(components.column, maxComponent) && isComponentValid(components.row, maxComponent);
    }
    
    /**
     * If this Coordinate has a neighboring Coordinate in the requested Direction on a 19x19 board, this method will
     * return it.
     * @param direction the Direction in which to step one over to retrieve a Coordinate
     * @return the found neighboring Coordinate
     * @throws IllegalArgumentException if {@code direction} is {@code null}
     * @throws NoNeighborException if {@link #hasNeighborTo(Direction)} would return {@code false}
     */
    public Coordinate getNeighborTo( Direction direction ) throws NoNeighborException {
        return getNeighborTo(direction, MAX_COMPONENT);
    }
    
    /**
     * If this Coordinate has a neighboring Coordinate in the requested Direction on a {@code
     * maxComponent}x{@code maxComponent} board, this method will return it.
     * @param direction the Direction in which to step one over to retrieve a Coordinate
     * @param maxComponent The number of rows and columns on the desired board
     * @return the found neighboring Coordinate
     * @throws IllegalArgumentException if {@direction} is {@code null}, or {@code maxComponent} is less than 1 or
     * greater than 19
     * @throws NoNeighborException if {@link #hasNeighborTo(Direction, int))} would return {@code false}
     */
    public Coordinate getNeighborTo( Direction direction, int maxComponent ) throws NoNeighborException {
        validateDirection(direction);
        validateMaxComponent(maxComponent);
        
        if (!areComponentsValid(this.components, maxComponent)) {
            throw new NoNeighborException(this, direction, maxComponent);
        }
        
        Components neighborComponents = getComponentsFor(direction);
        if (!areComponentsValid(neighborComponents, maxComponent)) {
            throw new NoNeighborException(this, direction, maxComponent);
        } else {
            return getCoordinate(neighborComponents);
        }
    }
    
    /**
     * Returns an Iterable that produces a lazy Iterator over all the Coordinates that are neighbors of this Coordinate
     * on a 19x19 board.
     * @return an Iterable for iterating over all the Coordinates that neighbor this Coordinate on a 19x19 board
     * @see #hasNeighborTo(Direction)
     */
    public Iterable<Coordinate> getNeighbors() {
        return getNeighbors(MAX_COMPONENT);
    }
    
    /**
     * Returns an Iterable that produces a lazy Iterator over all the Coordinates that are neighbors of this Coordinate
     * on a {@code maxComponent}x{@code maxComponent} board.  Note that if this Coordinate is not on the requested board
     * (either its column or its row is greater than {@code maxComponent}), the Iterator will immediately return {@code
     * false} on a call to {@code hasNext()}. 
     * @param maxComponent The number of rows and columns on the desired board
     * @return an Iterable for iterating over all the Coordinates that neighbor this Coordinate on a {@code
     * maxComponent}x{@code maxComponent} board
     * @throws IllegalArgumentException if {@code maxComponent} is less than 1 or greater than 19
     * @see #hasNeighborTo(Direction, int)
     */
    public Iterable<Coordinate> getNeighbors( int maxComponent ) {
        validateMaxComponent(maxComponent);
        return new NeighborIterator(this, maxComponent);
    }
}
