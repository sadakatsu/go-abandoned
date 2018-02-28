package com.sadakatsu.go.domain.intersection;

public enum PermanentlyUnplayable implements Intersection {
    PERMANENTLY_UNPLAYABLE;

    @Override
    public boolean countsAsLiberty() {
        return false;
    }

    @Override
    public boolean isPlayable() {
        return false;
    }
}
