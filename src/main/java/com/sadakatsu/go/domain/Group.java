package com.sadakatsu.go.domain;

import static com.sadakatsu.go.domain.intersection.Empty.EMPTY;
import static com.sadakatsu.go.domain.intersection.PermanentlyUnplayable.PERMANENTLY_UNPLAYABLE;
import static com.sadakatsu.go.domain.intersection.Stone.BLACK;
import static com.sadakatsu.go.domain.intersection.Stone.WHITE;
import static com.sadakatsu.go.domain.intersection.TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.sadakatsu.go.domain.intersection.Intersection;

public class Group {
    public final boolean bordersBlack;
    public final boolean bordersWhite;
    public final int liberties;
    public final Intersection type;
    public final Set<Coordinate> members;
    
    private Integer hashCode;
    private String representation;
    
    public Group( Board board, Coordinate start ) {
        validateBoard(board);
        boolean bordersBlack = false;
        boolean bordersWhite = false;
        int liberties = 0;
        Intersection type = board.get(start);
        Set<Coordinate> group = new HashSet<>();
        
        Queue<Coordinate> toVisit = new LinkedList<>();
        Set<Coordinate> queued = new HashSet<>();
        toVisit.add(start);
        queued.add(start);
        while (!toVisit.isEmpty()) {
            Coordinate current = toVisit.remove();
            Intersection currentValue = board.get(current);
            if (type == currentValue || type.countsAsLiberty() && currentValue.countsAsLiberty()) {
                group.add(current);
                for (Coordinate neighbor : current.getNeighbors(board.getDimension())) {
                    if (!queued.contains(neighbor)) {
                        toVisit.add(neighbor);
                        queued.add(neighbor);
                    }
                }
            } else if (currentValue.countsAsLiberty()) {
                ++liberties;
            } else if (BLACK == currentValue) {
                bordersBlack = true;
            } else if (WHITE == currentValue) {
                bordersWhite = true;
            }
        }
        
        this.type = type == TEMPORARILY_UNPLAYABLE ? EMPTY : type;
        this.bordersBlack = bordersBlack;
        this.bordersWhite = bordersWhite;
        this.liberties = liberties;
        this.members = Collections.unmodifiableSet(group);
    }
    
    private void validateBoard( Board board ) {
        if (board == null) {
            throw new IllegalArgumentException("The passed Board may not be null.");
        }
    }
    
    @Override
    public boolean equals( Object other ) {
        boolean result = this == other;
        if (!result && other != null && Group.class.equals(other.getClass())) {
            Group that = (Group) other;
            result =
                this.bordersBlack == that.bordersBlack &&
                this.bordersWhite == that.bordersWhite &&
                this.liberties == that.liberties &&
                this.type == that.type &&
                members.equals(that.members);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        if (hashCode == null) {
            HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(bordersBlack);
            builder.append(bordersWhite);
            builder.append(liberties);
            builder.append(type);
            builder.append(members);
            hashCode = builder.toHashCode();
        }
        
        return hashCode;
    }
    
    @Override
    public String toString() {
        if (representation == null) {
            StringBuilder builder = new StringBuilder("Group{ ");
            builder.append(members.size());
            builder.append(" ");
            builder.append(type);
            if (type == EMPTY) {
                if (bordersBlack) {
                    builder.append(" bordering BLACK");
                    if (bordersWhite) {
                        builder.append(" and WHITE");
                    }
                } else if (bordersWhite) {
                    builder.append(" bordering WHITE");
                }
            } else if (type != PERMANENTLY_UNPLAYABLE) {
                builder.append(" with ");
                builder.append(liberties);
                builder.append(" liberties");
            }
            builder.append(", ");
            builder.append(members);
            builder.append(" }");
            representation = builder.toString();
        }
        return representation;
    }
}
