package com.sadakatsu.go;

import static com.sadakatsu.go.domain.Coordinate.*;

import com.sadakatsu.go.domain.Game.GameBuilder;
import com.sadakatsu.go.domain.Game;
import com.sadakatsu.go.domain.Move;

public class PlayInvalidatedGame {
    private static final Move[] moves = {
        C17_R04, C04_R04, C16_R17, C04_R17, C06_R03, C03_R06, C11_R03, C16_R14, C17_R12, C14_R16, C17_R16, C17_R15,
        C15_R15, C15_R16, C16_R16, C16_R15, C14_R15, C13_R16, C13_R15, C12_R16, C15_R13, C18_R12, C17_R13, C18_R16,
        C18_R17, C18_R15, C16_R18, C18_R13, C17_R10, C17_R11, C16_R11, C18_R11, C16_R10, C18_R10, C11_R14, C14_R18,
        C18_R09, C19_R18, C18_R18, C19_R17, C10_R16, C10_R17, C09_R17, C10_R18, C19_R10, C19_R11, C19_R13, C17_R14,
        C19_R14, C18_R08, C17_R09, C19_R09, C17_R07, C18_R07, C17_R06, C17_R03, C16_R03, C18_R04, C18_R06, C16_R04,
        C17_R05, C11_R04, C10_R03, C12_R03, C15_R03, C10_R04, C09_R04, C09_R05, C08_R04, C12_R04, C08_R05, C09_R06,
        C19_R16, C19_R15, C17_R19, C05_R03, C06_R02, C17_R02, C15_R19, C15_R18, C16_R02, C14_R05, C15_R05, C14_R04,
        C15_R04, C03_R15, C15_R14, C14_R19, C16_R13, C18_R14, C19_R08, C19_R07, C19_R06, C16_R19, C11_R06, C10_R06,
        C15_R19, C19_R16, C17_R08, C16_R19, C11_R08, C08_R06, C06_R05, C09_R09, C15_R19, C18_R19, C03_R03, C05_R02,
        C05_R04, C05_R05, C06_R04, C04_R03, C04_R05, C03_R05, C05_R06, C03_R04, C07_R09, C08_R08, C11_R10, C09_R16,
        C07_R11, C06_R07, C04_R07, C10_R10, C11_R11, C06_R10, C07_R10, C05_R08, C04_R14, C03_R14, C04_R12, C08_R16,
        C04_R10, C12_R06, C11_R07, C07_R08, C19_R08, C16_R19, C11_R05, C10_R05, C15_R19, C17_R18, C17_R17, C16_R19,
        C03_R17, C03_R16, C15_R19, C15_R17, C19_R10, C16_R19, C19_R19, C19_R09, C15_R19, C18_R19
    };
    public static void main( String[] args ) {
        GameBuilder builder = Game.newBuilder();
        builder.setCompensation(5.5);
        
        Game game = builder.build();
        System.out.println(game);
        
        for (Move move : moves) {
            game = game.play(move);
            System.out.println(game);
        }
    }

}
