package com.sadakatsu.go.domain.instersection;

import static com.sadakatsu.go.domain.intersection.Empty.EMPTY;
import static com.sadakatsu.go.domain.intersection.PermanentlyUnplayable.PERMANENTLY_UNPLAYABLE;
import static com.sadakatsu.go.domain.intersection.Stone.*;
import static com.sadakatsu.go.domain.intersection.TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sadakatsu.go.domain.intersection.Intersection;

public class IntersectionsTest {
    private static Intersection[] INTERSECTIONS = {
        EMPTY,
        BLACK,
        WHITE,
        TEMPORARILY_UNPLAYABLE,
        PERMANENTLY_UNPLAYABLE
    };
    
    @Test
    public void onlyEmptyIsPlayable() {
        for (Intersection intersection : INTERSECTIONS) {
            boolean actual = intersection.isPlayable();
            boolean expected = EMPTY.equals(intersection);
            if (actual != expected) {
                String message = String.format(
                    "%s.isPlayable() should have returned %s, but it returned %s.",
                    intersection,
                    expected,
                    actual
                );
                fail(message);
            }
        }
    }
    
    @Test
    public void onlyEmptyAndTemporarilyUnplayableCountAsLiberties() {
        for (Intersection intersection : INTERSECTIONS) {
            boolean actual = intersection.countsAsLiberty();
            boolean expected = EMPTY.equals(intersection) || TEMPORARILY_UNPLAYABLE.equals(intersection);
            if (actual != expected) {
                String message = String.format(
                    "%s.countsAsLiberty() should have returned %s, but it returned %s.",
                    intersection,
                    expected,
                    actual
                );
                fail(message);
            }
        }
    }
}
