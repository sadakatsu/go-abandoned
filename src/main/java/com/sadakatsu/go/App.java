package com.sadakatsu.go;

import static com.sadakatsu.go.domain.intersection.Empty.EMPTY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sadakatsu.go.domain.Coordinate;
import com.sadakatsu.go.domain.Game;
import com.sadakatsu.go.domain.Move;
import com.sadakatsu.go.domain.Game.GameBuilder;

public class App {
    public static void main( String[] args ) {
        GameBuilder builder = Game.newBuilder();
        Game game = builder.build();
        
        System.out.println(game);
        
        long start, end;
        while (!game.wouldPassEndGame()) {
            List<Move> legalMoves = getLegalMoves(game);
            Collections.shuffle(legalMoves);
            
            start = System.nanoTime();
            if (legalMoves.size() == 0) {
                game = game.pass();
            } else {
                game = game.play(legalMoves.get(0));
            }
            end = System.nanoTime();
            
            System.out.println(game);
            System.out.format("%d: Move took %f ms.\n", game.getMovesPlayed(), (end - start) / 1e6);
        }
        
        game = game.pass();
        System.out.println(game);
    }
    
    private static List<Move> getLegalMoves( Game game ) {
        List<Move> moves = new ArrayList<>();
        for (Coordinate coordinate : Coordinate.iterateOverBoard()) {
            if (EMPTY == game.get(coordinate)) {
                moves.add(coordinate);
            }
        }
        return moves;
    }
}
