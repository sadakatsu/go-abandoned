package com.sadakatsu.go.domain;

import static com.sadakatsu.go.domain.intersection.Empty.EMPTY;
import static com.sadakatsu.go.domain.intersection.PermanentlyUnplayable.PERMANENTLY_UNPLAYABLE;
import static com.sadakatsu.go.domain.intersection.Stone.*;
import static com.sadakatsu.go.domain.intersection.TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.sadakatsu.go.domain.intersection.Intersection;

public class Board {
	public static final class SituationHash implements Comparable<SituationHash> {
		private final int dimension;
		private final int hashCode;
		private final long[] seed;
		
		private SituationHash( int dimension, long[] seed, long currentPlayerHash, long playerAfterPassHash ) {
			this.dimension = dimension;
			this.seed = Arrays.copyOf(seed, seed.length);
			this.seed[0] += playerAfterPassHash << SITUATION_BITS_PER_INTERSECTION + currentPlayerHash;
			this.hashCode = this.seed.hashCode();
		}
		
		@Override
		public boolean equals( Object other ) {
			boolean result = this == other;
			if (!result && other != null && SituationHash.class.equals(other.getClass())) {
				SituationHash that = (SituationHash) other;
				result = this.dimension == that.dimension && Arrays.equals(this.seed, that.seed);
			}
			return result;
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		@Override
		public String toString() {
			List<String> hexes = new ArrayList<>();
			for (long bucket : seed) {
				String hex = Long.toHexString(bucket);
				hexes.add(hex);
			}
			String concatenated = StringUtils.join(hexes, ":");
			return String.format("SituationHash{ dimension = %d, seed = %s }", concatenated);
		}

		@Override
		public int compareTo( SituationHash that ) {
			int result;
			
			if (that != null) {
				result = Integer.compare(that.dimension, this.dimension);
				for (int i = seed.length - 1; result == 0 && i >= 0; --i) {
					result = Long.compare(this.seed[i], that.seed[i]);
				}
			} else {
				result = -1;
			}
			
			return result;
		}
	}
	
    private static final int POSITION_BITS_PER_INTERSECTION = 2;
    private static final int SITUATION_BITS_PER_INTERSECTION = 3;
    
    // Originally, I had used Long.SIZE / BITS_PER_INTERSECTION, but allowing the sign bit to be used for comparing
    // positions caused a problem where different positions were evaluated as equal.  Removing the sign bit appears to
    // have fixed this issue.
    private static final int POSITION_INTERSECTIONS_PER_LONG = (Long.SIZE - 1) / POSITION_BITS_PER_INTERSECTION;
    private static final int SITUATION_INTERSECTIONS_PER_LONG = (Long.SIZE - 1) / SITUATION_BITS_PER_INTERSECTION;
    
    private static final int MAX_DIMENSION = 19;
    private static final Intersection[] RECOGNIZED_INTERSECTION_VALUES = {
        EMPTY,
        BLACK,
        WHITE,
        TEMPORARILY_UNPLAYABLE,
        PERMANENTLY_UNPLAYABLE
    };
    
    private final int dimension;
    private final long[] position;
    private final Intersection[] intersections;
    
    private int nonEmptyIntersections;
    
    public Board() {
        this(MAX_DIMENSION);
    }
    
    public Board( int dimension ) {
        if (dimension < 1 || dimension > MAX_DIMENSION) {
            String message = String.format(
                "Received the illegal dimension %d.  Expected 1 <= dimension <= 19.",
                dimension
            );
            throw new IllegalArgumentException(message);
        }
        
        int intersections = dimension * dimension;
        
        this.dimension = dimension;
        this.intersections = new Intersection[intersections];
        for (int i = 0; i < this.intersections.length; ++i) {
            this.intersections[i] = EMPTY;
        }
        
        int bitMaskLength = getBitMaskLength(intersections);
        this.position = new long[bitMaskLength];
        this.nonEmptyIntersections = 0;
    }
    
    private int getBitMaskLength( int intersections ) {
        int length = intersections / POSITION_INTERSECTIONS_PER_LONG;
        if (intersections % POSITION_INTERSECTIONS_PER_LONG > 0) {
            ++length;
        }
        return length;
    }
    
    public Board( Board source ) {
        if (source == null) {
            throw new IllegalArgumentException("The source cannot be null.");
        }
        
        this.dimension = source.dimension;
        this.intersections = Arrays.copyOf(source.intersections, source.intersections.length);
        this.position = Arrays.copyOf(source.position, source.position.length);
        this.nonEmptyIntersections = source.nonEmptyIntersections;
    }
    
    public Intersection get( Coordinate coordinate ) {
        validateCoordinate(coordinate);
        int index = convertCoordinateToIndex(coordinate);
        return intersections[index];
    }
    
    private void validateCoordinate( Coordinate coordinate ) {
        if (coordinate == null || coordinate.getColumn() > dimension || coordinate.getRow() > dimension) {
            String message = String.format(
                "This board has dimension %d, but the passed Coordinate is %s.",
                dimension,
                coordinate
            );
            throw new IllegalArgumentException(message);
        }
    }
    
    private int convertCoordinateToIndex( Coordinate coordinate ) {
        return (coordinate.getRow() - 1) * dimension + coordinate.getColumn() - 1;
    }
    
    public void set( Coordinate coordinate, Intersection intersection ) {
        validateCoordinate(coordinate);
        validateIntersection(intersection);
        int index = convertCoordinateToIndex(coordinate);
        
        int positionIndex = index / POSITION_INTERSECTIONS_PER_LONG;
        int offset = (index % POSITION_INTERSECTIONS_PER_LONG) * POSITION_BITS_PER_INTERSECTION;
        Intersection previousValue = intersections[index];
        
        if (!previousValue.countsAsLiberty()) {
            position[positionIndex] -= (long) getIntersectionHash(previousValue) << offset;
            --nonEmptyIntersections;
        }
        if (!intersection.countsAsLiberty()) {
            position[positionIndex] += (long) getIntersectionHash(intersection) << offset;
            ++nonEmptyIntersections;
        }
        
        intersections[index] = intersection;
    }
    
    private int getIntersectionHash( Intersection intersection ) {
        int hash = 0;
        if (intersection == BLACK) {
            hash = 1;
        } else if (intersection == WHITE) {
            hash = 2;
        } else if (intersection == PERMANENTLY_UNPLAYABLE) {
            hash = 3;
        }
        return hash;
    }
    
    private void validateIntersection( Intersection intersection ) {
        if (!ArrayUtils.contains(RECOGNIZED_INTERSECTION_VALUES, intersection)) {
            String message = String.format(
                "Received an unrecoginzed Intersection %s.  It should be one of Empty.EMPTY, Stone.BLACK, " +
                "Stone.WHITE, TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE, or PermanentlyUnplayable." +
                "PERMANENTLY_UNPLAYABLE.",
                intersection
            );
            throw new IllegalArgumentException(message);
        }
    }
    
    public int getDimension() {
        return dimension;
    }
    
    public boolean isSamePositionAs( Board that ) {
        if (that == null) {
            throw new IllegalArgumentException("isSamePositionAs() requires a non-null Board argument.");
        }
        return this.dimension == that.dimension && Arrays.equals(this.position, that.position);
    }
    
    public int countNonEmptyIntersections() {
        return nonEmptyIntersections;
    }
    
    @Override
    public boolean equals( Object other ) {
        boolean result = this == other;
        if (!result && other != null && Board.class.equals(other.getClass())) {
            Board that = (Board) other;
            result = this.dimension == that.dimension && Arrays.equals(this.intersections, that.intersections);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = intersections.length - 1; i >= 0; --i) {
            Intersection value = intersections[i];
            
            int contribution = -1;
            if (EMPTY.equals(value)) {
                contribution = 0;
            } else if (BLACK.equals(value)) {
                contribution = 1;
            } else if (WHITE.equals(value)) {
                contribution = 2;
            } else if (TEMPORARILY_UNPLAYABLE.equals(value)) {
                contribution = 3;
            } else if (PERMANENTLY_UNPLAYABLE.equals(value)) {
                contribution = 4;
            } else {
                errorOverIllegalIntersectionValue(i, value);
            }
            
            hash = hash * 5 + contribution;
        }
        return hash;
    }
    
    private void errorOverIllegalIntersectionValue( int index, Intersection value ) {
        String message = String.format(
            "%s somehow has an unpermitted Intersection value at index %d: %s.",
            this,
            index,
            value
        );
        throw new IllegalStateException(message);
    }
    
    @Override
    public String toString() {
        String representation = null;
        
        if (dimension == 1 && EMPTY.equals(intersections[0])) {
            representation = "∙";
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < intersections.length; ++i) {
                Intersection value = intersections[i];
                if (EMPTY.equals(value)) {
                    if (i == 0) {
                        builder.append("┌");
                    } else if (i == dimension - 1) {
                        builder.append("┐");
                    } else if (i == dimension * (dimension - 1)) {
                        builder.append("└");
                    } else if (i == intersections.length - 1) {
                        builder.append("┘");
                    } else if (i < dimension) {
                        builder.append("┬");
                    } else if (i >= dimension * (dimension - 1)) {
                        builder.append("┴");
                    } else if (i % dimension == 0) {
                        builder.append("├");
                    } else if (i % dimension == dimension - 1) {
                        builder.append("┤");
                    } else {
                        builder.append("┼");
                    }
                } else if (BLACK.equals(value)) {
                    builder.append("●");
                } else if (WHITE.equals(value)) {
                    builder.append("○");
                } else if (TEMPORARILY_UNPLAYABLE.equals(value)) {
                    // builder.append("劫"); // this character is not rendered as fixed-width
                    // builder.append("◊");
                    builder.append("∙");
                } else if (PERMANENTLY_UNPLAYABLE.equals(value)) {
                    builder.append("X");
                } else {
                    errorOverIllegalIntersectionValue(i, value);
                }
                
                if ((i + 1) % dimension == 0) {
                    builder.append("\n");
                }
            }
            representation = builder.toString();
        }
        
        return representation;
    }
}
