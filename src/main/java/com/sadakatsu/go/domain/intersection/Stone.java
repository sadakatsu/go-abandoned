package com.sadakatsu.go.domain.intersection;

public enum Stone implements Intersection, Player {
    BLACK,
    WHITE;

    @Override
    public boolean countsAsLiberty() {
        return false;
    }

    @Override
    public boolean isPlayable() {
        return false;
    }
    
    @Override
    public Player getOpposite() {
        return (this.equals(BLACK) ? WHITE : BLACK);
    }
}
