package com.sadakatsu.go;

import static com.sadakatsu.go.domain.Coordinate.*;
import static com.sadakatsu.go.domain.Pass.PASS;

import java.util.HashSet;
import java.util.Set;

import com.sadakatsu.go.domain.Coordinate;
import com.sadakatsu.go.domain.Game.GameBuilder;
import com.sadakatsu.go.domain.Game;
import com.sadakatsu.go.domain.Group;
import com.sadakatsu.go.domain.Move;
import com.sadakatsu.go.domain.intersection.Stone;

public class PlayCompletedGame {
    private static final Move[] MOVES = {
//        Chen Yaoye 9p vs. Ke Jie 9p
//        C16_R04, C04_R16, C16_R17, C04_R04, C06_R17, C03_R14, C10_R16, C17_R14, C17_R16, C17_R11, C17_R09, C15_R11,
//        C17_R06, C14_R15, C13_R16, C06_R03, C11_R03, C04_R09, C08_R03, C09_R16, C10_R15, C10_R17, C09_R15, C07_R16,
//        C04_R17, C03_R17, C05_R16, C04_R15, C09_R17, C09_R18, C08_R17, C07_R17, C08_R18, C07_R18, C10_R18, C06_R18,
//        C03_R04, C03_R05, C02_R05, C03_R03, C02_R04, C03_R06, C02_R06, C03_R07, C03_R02, C02_R03, C04_R03, C04_R02,
//        C05_R03, C02_R02, C05_R04, C04_R05, C05_R02, C03_R01, C15_R09, C16_R15, C11_R06, C17_R03, C15_R02, C16_R02,
//        C18_R04, C09_R04, C09_R03, C15_R17, C15_R18, C14_R17, C12_R08, C11_R04, C10_R03, C07_R05, C10_R04, C06_R02,
//        C14_R18, C07_R12, C07_R14, C08_R14, C05_R09, C07_R09, C07_R11, C06_R11, C05_R05, C05_R06, C06_R06, C05_R07,
//        C04_R10, C03_R09, C07_R13, C08_R12, C08_R13, C06_R13, C06_R14, C05_R14, C05_R15, C06_R12, C06_R15, C05_R18,
//        C07_R04, C06_R04, C14_R13, C15_R13, C14_R12, C15_R12, C06_R08, C07_R07, C05_R13, C04_R14, C07_R06, C06_R07,
//        C06_R05, C08_R06, C08_R05, C05_R01, C07_R05, C18_R15, C18_R10, C17_R18, C18_R18, C13_R17, C17_R17, C18_R11,
//        C08_R08, C08_R07, C09_R11, C07_R08, C09_R12, C14_R11, C07_R01, C06_R01, C14_R14, C13_R15, C15_R14, C15_R15,
//        C12_R13, C16_R14, C05_R17, C04_R18, C08_R11, C07_R10, C16_R10, C13_R11, C12_R14, C13_R09, C13_R08, C18_R16,
//        C18_R17, C19_R11, C07_R02, C13_R18, C08_R16, C07_R15, C08_R15, C19_R17, C19_R18, C18_R19, C16_R19, C14_R19,
//        C17_R19, C14_R09, C14_R08, C11_R12, C12_R12, C11_R11, C11_R13, C18_R09, C18_R08, C19_R09, C19_R08, C19_R10,
//        C09_R06, C17_R08, C16_R08, C17_R10, C17_R07, C11_R17, C11_R18, C12_R16, C12_R18, C12_R15, C12_R11, C11_R10,
//        C12_R10, C11_R09, C10_R08, C09_R07, C10_R07, C12_R19, C12_R09, C09_R10, C11_R19, C13_R19, C13_R10, C14_R10,
//        C16_R11, C16_R12, C19_R16, C09_R08, C10_R09, C10_R10, C10_R12, C11_R08, C11_R07, C09_R09, C08_R10, C08_R09,
//        C16_R16, C15_R16, C15_R10, C11_R15, C13_R12, C19_R14, C11_R14, C04_R01, C07_R03, C07_R19, C13_R14, C17_R15,
//        C09_R19, C12_R17, C19_R17, C19_R15, C09_R13, C15_R19, C16_R18, C11_R16, C08_R19, C10_R11, C06_R16, PASS, PASS
        
//        Yang Dingxin 4p vs. Li He 5p
//        C16_R04, C04_R04, C16_R17, C04_R16, C06_R17, C03_R14, C16_R11, C14_R03, C17_R06, C16_R02, C09_R16, C14_R05,
//        C14_R16, C16_R07, C16_R06, C15_R07, C18_R08, C07_R03, C03_R09, C03_R11, C03_R04, C03_R03, C03_R05, C02_R03,
//        C05_R06, C05_R04, C04_R13, C03_R13, C08_R04, C06_R06, C06_R05, C05_R05, C06_R04, C06_R03, C05_R03, C04_R03,
//        C08_R03, C07_R06, C08_R05, C09_R07, C11_R04, C03_R07, C05_R07, C06_R08, C02_R07, C02_R06, C03_R06, C02_R08,
//        C03_R08, C04_R07, C05_R08, C06_R09, C04_R11, C06_R11, C04_R12, C03_R12, C04_R06, C01_R07, C03_R10, C02_R04,
//        C06_R12, C07_R12, C07_R13, C08_R12, C13_R04, C14_R04, C13_R03, C17_R03, C06_R14, C05_R17, C06_R16, C06_R18,
//        C07_R18, C17_R16, C17_R17, C17_R13, C17_R11, C18_R17, C18_R18, C18_R15, C19_R17, C14_R14, C16_R15, C15_R13,
//        C14_R12, C12_R14, C12_R16, C16_R09, C17_R09, C12_R12, C12_R11, C11_R11, C13_R13, C19_R16, C18_R16, C12_R13,
//        C13_R11, C18_R17, C11_R10, C10_R10, C18_R16, C12_R15, C11_R09, C11_R16, C12_R17, C10_R09, C10_R15, C18_R17,
//        C04_R15, C04_R14, C18_R16, C09_R14, C15_R06, C13_R08, C10_R08, C09_R08, C14_R06, C12_R07, C14_R02, C15_R02,
//        C13_R02, C13_R05, C13_R06, C12_R05, C07_R02, C08_R02, C12_R06, C10_R07, C09_R02, C11_R05, C05_R18, C04_R18,
//        C06_R19, C18_R17, C03_R16, C05_R13, C05_R12, C05_R14, C06_R13, C03_R17, C18_R16, C10_R04, C10_R03, C05_R15,
//        C13_R09, C18_R17, C05_R16, C04_R17, C18_R16, C08_R13, C06_R15, C18_R17, C11_R12, C10_R11, C18_R16, C05_R09,
//        C13_R14, C18_R17, C19_R18, C13_R15, C14_R15, C15_R12, C13_R12, C13_R07, C15_R11, C04_R10, C06_R02, C05_R02,
//        C05_R10, C04_R09, C11_R15, C17_R07, C18_R07, C18_R05, C11_R08, C11_R07, C18_R04, C17_R04, C17_R05, C18_R03,
//        C18_R06, C10_R14, C11_R14, C11_R13, C13_R16, C10_R13, C19_R05, C08_R15, C08_R16, C12_R04, C11_R03, C09_R15,
//        C10_R16, C05_R11, C14_R09, C15_R09, C08_R06, C08_R07, C10_R05, C18_R02, C12_R03, C06_R01, C08_R01, C07_R15,
//        C11_R06, C15_R03, C15_R01, C16_R01, C14_R01, C07_R16, C07_R17, C04_R19, C05_R19, C19_R03, C19_R04, C16_R10,
//        C17_R10, C15_R10, C14_R10, C12_R09, C12_R10, C12_R08, C07_R04, C05_R03, C10_R06, C09_R06, C09_R05, C15_R05,
//        C16_R05, C15_R08, C17_R08, C07_R14, C16_R03, C17_R01, C07_R01, C05_R01, C07_R05, C14_R07, C16_R08, C15_R04,
//        C14_R08, PASS, PASS

//        Kiyonari Testuya 9p vs. Furuya Yutaka 8p
        C17_R04, C15_R17, C04_R16, C04_R03, C15_R03, C17_R10, C03_R05, C04_R08, C05_R04, C04_R04, C04_R05, C05_R05,
        C04_R07, C06_R05, C05_R07, C07_R03, C03_R08, C17_R16, C06_R17, C10_R16, C16_R11, C16_R10, C15_R11, C15_R10,
        C14_R11, C18_R12, C14_R16, C15_R16, C14_R15, C13_R18, C16_R14, C17_R06, C03_R15, C13_R09, C11_R14, C12_R16,
        C09_R15, C03_R10, C05_R10, C03_R12, C02_R13, C05_R11, C06_R10, C02_R08, C03_R07, C02_R12, C02_R09, C06_R11,
        C07_R11, C06_R13, C03_R13, C02_R10, C08_R03, C08_R04, C04_R11, C04_R12, C04_R10, C03_R09, C05_R12, C04_R13,
        C06_R12, C01_R09, C09_R03, C07_R02, C15_R05, C15_R06, C14_R05, C09_R04, C11_R03, C18_R05, C10_R04, C03_R17,
        C04_R17, C02_R04, C09_R16, C09_R17, C08_R17, C09_R18, C08_R18, C12_R14, C12_R15, C11_R15, C13_R15, C10_R14,
        C11_R13, C10_R15, C17_R15, C18_R16, C18_R04, C03_R14, C02_R14, C04_R14, C02_R15, C08_R19, C07_R19, C09_R19,
        C07_R18, C09_R06, C10_R05, C09_R01, C10_R02, C19_R04, C19_R03, C19_R05, C18_R03, C14_R06, C13_R06, C13_R07,
        C12_R07, C12_R06, C13_R05, C13_R08, C16_R06, C16_R07, C16_R05, C18_R07, C10_R13, C09_R14, C12_R10, C13_R10,
        C13_R11, C11_R09, C11_R10, C11_R07, C10_R09, C10_R08, C09_R09, C12_R05, C12_R04, C08_R14, C09_R13, C08_R16,
        C03_R04, C03_R03, C02_R05, C02_R03, C07_R06, C07_R13, C08_R13, C18_R14, C07_R05, C07_R04, C08_R07, C04_R09,
        C06_R06, C06_R04, C17_R13, C18_R13, C17_R11, C18_R11, C14_R10, C14_R09, C14_R17, C14_R18, C07_R15, C07_R16,
        C06_R15, C08_R15, C06_R16, C05_R09, C06_R09, C09_R08, C08_R08, C05_R08, C06_R08, C17_R12, C16_R12, C15_R15,
        C15_R14, C17_R14, C16_R15, C16_R13, C15_R13, C02_R07, C02_R06, C05_R06, C04_R06, C01_R13, C04_R15, C07_R17,
        C06_R18, C12_R09, C11_R06, C12_R08, C16_R16, C16_R17, C18_R15, C19_R15, C11_R05, C07_R12, C08_R12, C12_R07,
        C01_R14, C01_R12, C03_R11, C02_R11, C13_R17, C12_R17, C08_R02, C08_R01, C10_R01, C05_R14, C05_R15, C01_R07,
        C01_R04, C01_R03, C01_R05, C17_R13,
        
        // These moves are not part of the actual game, but how I think it should play out.
        C09_R02, C07_R01, C09_R05, C09_R07, C10_R06, C13_R16, C14_R14, C17_R05, C01_R06, C07_R14, C08_R05, C08_R06,
        C10_R07, C05_R13, C06_R14,
        
        PASS, PASS
    };
    
    private static final Coordinate[] DEAD_GROUP_INDICATORS = {
//      Chen Yaoye 9p vs. Ke Jie 9p
//        C16_R02, C17_R03, C02_R04, C09_R04, C11_R04, C06_R08, C05_R09, C04_R10, C05_R13, C08_R14, C13_R16, C18_R19
        
//      Yang Dingxin 4p vs. Li He 5p
//        C03_R04, C03_R08, C03_R16, C04_R15, C05_R10, C10_R04, C11_R12, C11_R16, C14_R14, C15_R12, C17_R13, C17_R16,
//        C18_R15, C18_R17, C19_R16
        
//      Kiyonari Testuya 9p vs. Furuya Yutaka 8p
        C03_R17, C05_R04, C12_R14
    };
    
    public static void main( String[] args ) {
        GameBuilder builder = Game.newBuilder();
        
        Game game = builder.build();
        System.out.println(game);
        
        for (Move move : MOVES) {
            game = game.play(move);
            System.out.println(game);
        }
        
        long start = System.nanoTime();
        
        Set<Group> deadGroups = new HashSet<>();
        for (Group group : game.getGroupsOfStones()) {
            for (Coordinate coordinate : DEAD_GROUP_INDICATORS) {
                if (group.members.contains(coordinate)) {
                    deadGroups.add(group);
                    break;
                }
            }
        }
        
        game = game.score(deadGroups);
        
        long end = System.nanoTime();
        System.out.format("\nGame scored in %f ms\n\n", (end - start) / 1e6);
        
        System.out.println(game);
        
        Set<Group> groups = game.getAllGroups();
        for (Group group : groups) {
            System.out.println(group);
        }
        
        System.out.println();
        for (int row = 1; row <= 19; ++row) {
            for (int column = 1; column <= 19; ++column) {
                Coordinate coordinate = Coordinate.get(column, row);
                int score = 0;
                for (Group group : groups) {
                    if (group.members.contains(coordinate)) {
                        if (group.type == Stone.BLACK) {
                            score = 1;
                        } else if (group.type == Stone.WHITE) {
                            score = -1;
                        } else if (group.type.countsAsLiberty()) {
                            if (group.bordersBlack) {
                                ++score;
                            }
                            if (group.bordersWhite) {
                                --score;
                            }
                        } else {
                            score = Integer.MIN_VALUE;
                        }
                        break;
                    }
                }
                
                if (score == 1) {
                    System.out.print("+");
                } else if (score == -1) {
                    System.out.print("-");
                } else if (score == 0){
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
