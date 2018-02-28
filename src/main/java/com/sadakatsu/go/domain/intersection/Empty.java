package com.sadakatsu.go.domain.intersection;

public enum Empty implements Intersection {
    EMPTY;

    @Override
    public boolean countsAsLiberty() {
        return true;
    }

    @Override
    public boolean isPlayable() {
        return true;
    }
}
