package com.sadakatsu.go.domain.outcome;

import java.util.concurrent.ThreadLocalRandom;

public class OutcomeTestHelper {
    static int MINIMUM = -361;
    static int MAXIMUM = 361;
    static int MINIMUM_STEP = MINIMUM * 4;
    static int MAXIMUM_STEP = MAXIMUM * 4;
    
    static double generateRandomInvalidScore() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        double invalid = 0;
        while (invalid * 4 == (double) (int) (invalid * 4)) {
            invalid = random.nextInt(MINIMUM, MAXIMUM) + random.nextDouble();
        }
        return invalid;
    }
    
    static double generateRandomValidScore() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double initial = random.nextInt(MINIMUM_STEP, MAXIMUM_STEP + 1);
        return initial * 0.25;
    }
    
    static double generateRandomScoreGreaterThanExcluding( double bound, double exclude ) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        int boundStep = (int) (bound * 4);
        int excludeStep = (int) (exclude * 4);
        
        int step;
        do {
            step = random.nextInt(boundStep + 1, MAXIMUM_STEP + 2);
        } while (step == excludeStep);
        
        return step * 0.25;
    }
    
    static double generateRandomScoreLessThanExcluding( double bound, double exclude ) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        int boundStep = (int) (bound * 4);
        int excludeStep = (int) (exclude * 4);
        
        int step;
        do {
            step = random.nextInt(MINIMUM_STEP - 1, boundStep);
        } while (step == excludeStep);
        
        return step * 0.25;
    }
    
    static double generateDifferentValidScore( double exclude ) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        int excludeStep = (int) (exclude * 4);
        
        int step;
        do {
            step = random.nextInt(MINIMUM_STEP, MAXIMUM_STEP + 1);
        } while (step == excludeStep);
        
        return step * 0.25;
    }
    
    private OutcomeTestHelper() {}
}
