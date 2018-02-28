package com.sadakatsu.go.domain.intersection;

public enum TemporarilyUnplayable implements Intersection {
    TEMPORARILY_UNPLAYABLE;

    @Override
    public boolean countsAsLiberty() {
        return true;
    }

    @Override
    public boolean isPlayable() {
        return false;
    }

}
