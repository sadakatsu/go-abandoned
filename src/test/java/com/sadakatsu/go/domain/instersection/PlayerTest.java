package com.sadakatsu.go.domain.instersection;

import static com.sadakatsu.go.domain.intersection.Stone.*;

import org.junit.Assert;
import org.junit.Test;

public class PlayerTest {
    @Test
    public void theOppositeOfBlackIsWhite() {
        Assert.assertEquals(WHITE, BLACK.getOpposite());
    }
    
    @Test
    public void theOppositeOfWhiteIsBlack() {
        Assert.assertEquals(BLACK, WHITE.getOpposite());
    }
}
