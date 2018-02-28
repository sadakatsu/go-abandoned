package com.sadakatsu.go.domain;

import static com.sadakatsu.go.domain.Pass.PASS;
import static com.sadakatsu.go.domain.intersection.Empty.EMPTY;
import static com.sadakatsu.go.domain.intersection.Stone.BLACK;
import static com.sadakatsu.go.domain.intersection.Stone.WHITE;
import static com.sadakatsu.go.domain.intersection.TemporarilyUnplayable.TEMPORARILY_UNPLAYABLE;
import static com.sadakatsu.go.domain.outcome.CompleteButNotScored.COMPLETE_BUT_NOT_SCORED;
import static com.sadakatsu.go.domain.outcome.InProgress.IN_PROGRESS;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;
import com.sadakatsu.go.domain.intersection.Intersection;
import com.sadakatsu.go.domain.intersection.Player;
import com.sadakatsu.go.domain.outcome.Invalidated;
import com.sadakatsu.go.domain.outcome.Outcome;
import com.sadakatsu.go.domain.outcome.Outcomes;

// NOTE: This should probably become an interface later on so that different rule sets can be supported.  For this
// initial development effort with the emphasis on the Chinese rule set, a single class is being used.
public class Game {
    public static class GameBuilder implements Builder<Game> {
        private final int dimension;
        private final int maxHandicapStones;
        
        private double compensation;
        private Set<Coordinate> handicapStones;
        
        private GameBuilder( int dimension) {
            this.compensation = 7.5;
            this.dimension = dimension;
            this.handicapStones = new HashSet<>();
            this.maxHandicapStones = dimension * dimension - 1;
        }
        
        public double getCompensation() {
            return compensation;
        }
        
        public GameBuilder setCompensation( double compensation ) {
            if (
                !Double.isFinite(compensation) ||
                (double) (int) (compensation * 4.) != compensation * 4.
            ) {
                throw new IllegalArgumentException("Compensation should be a finite number evenly divisible by 0.25.");
            }
            this.compensation = compensation;
            return this;
        }
        
        public int getDimension() {
            return dimension;
        }
        
        public int countHandicapStones() {
            return handicapStones.size();
        }
        
        public Set<Coordinate> getHandicapStones() {
            return new HashSet<>(handicapStones);
        }
        
        public GameBuilder addHandicapStone( Coordinate stone ) {
            if (!handicapStones.contains(stone)) {
                validateHandicapStone(stone);
                
                if (handicapStones.size() + 1 > maxHandicapStones) {
                    throw new IllegalStateException(
                        "A GameBuilder of dimension " +
                        dimension +
                        " can only have " +
                        maxHandicapStones +
                        " handicap stones.  This add() would put the collection over the limit."
                    );
                }
                
                handicapStones.add(stone);
            }
            return this;
        }
        
        private void validateHandicapStone( Coordinate coordinate ) {
            if (
                coordinate == null ||
                coordinate.getColumn() > dimension ||
                coordinate.getRow() > dimension 
            ) {
                throw new IllegalArgumentException(
                    "Any handicap stone Coordinate must be a non-null Coordinate whose column and row are both less " +
                    "than or equal to the GameBuilder's dimension.  Received " + coordinate
                );
            } else if (dimension == 1) {
                throw new IllegalStateException("A 1x1 GameBuilder can never have any handicap stones.");
            }
        }
        
        public GameBuilder removeHandicapStone( Coordinate stone ) {
            validateHandicapStone(stone);
            handicapStones.remove(stone);
            return this;
        }
        
        public GameBuilder removeAllHandicapStones() {
            handicapStones.clear();
            return this;
        }
        
        public GameBuilder setHandicapStones( Set<Coordinate> stones ) {
            validateHandicapStones(stones);
            handicapStones.clear();
            handicapStones.addAll(stones);
            return this;
        }
        
        private void validateHandicapStones( Set<Coordinate> stones ) {
            if (stones == null) {
                throw new IllegalArgumentException("The passed Coordinate Set may not be null.");
            }
            
            stones.forEach(this::validateHandicapStone);
            
            if (stones.size() > maxHandicapStones) {
                throw new IllegalStateException(
                    "A GameBuilder of dimension " +
                    dimension +
                    " can only have " +
                    maxHandicapStones +
                    " handicap stones.  The passed Set has " +
                    stones.size() +
                    " Coordinates."
                );
            }
        }
        
        @Override
        public Game build() {
            return new Game(dimension, compensation, handicapStones);
        }
        
        @Override
        public boolean equals( Object other ) {
            boolean result = this == other;
            if (!result && other != null && GameBuilder.class.equals(other.getClass())) {
                GameBuilder that = (GameBuilder) other;
                result =
                    this.dimension == that.dimension &&
                    this.compensation == that.compensation &&
                    this.handicapStones.equals(that.handicapStones);
            }
            return result;
        }
        
        @Override
        public int hashCode() {
            HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(compensation);
            builder.append(dimension);
            builder.append(handicapStones);
            return builder.toHashCode();
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
    
    private static class SuperKoComparison {
        private SuperKoComparisonNode[] nodes;
        
        public SuperKoComparison( SuperKoComparison previous, Board board ) {
            if (previous != null) {
                nodes = Arrays.copyOf(previous.nodes, previous.nodes.length);
            } else {
                nodes = new SuperKoComparisonNode[board.getDimension() * board.getDimension()];
            }
            int stonesOnBoard = board.countNonEmptyIntersections();
            nodes[stonesOnBoard] = new SuperKoComparisonNode(board, nodes[stonesOnBoard]);
        }
        
        public boolean positionHasBeenPlayedBefore( Board board ) {
            boolean same = false;
            
            int stoneCount = board.countNonEmptyIntersections();
            for (SuperKoComparisonNode node = nodes[stoneCount]; !same && node != null; node = node.next) {
                if (board.isSamePositionAs(node.board)) {
                    same = true;
                }
            }
            
            return same;
        }
    }
    
    private static class SuperKoComparisonNode {
        Board board;
        SuperKoComparisonNode next;
        
        SuperKoComparisonNode( Board board, SuperKoComparisonNode next ) {
            this.board = board;
            this.next = next;
        }
    }
    
    public static GameBuilder newBuilder() {
        return new GameBuilder(19);
    }
    
    public static GameBuilder newBuilder( int dimension ) {
        if (dimension < 1 || dimension > 19) {
            throw new IllegalArgumentException("A game's dimension must be in the range [1, 19].");
        }
        return new GameBuilder(dimension);
    }
    
    private final Board board;
    private final double compensation;
    private final Game previousState;
    private final int capturesByBlack;
    private final int capturesByWhite;
    private final int dimension;
    private final int movesPlayed;
    private final Move previousMove;
    private final Outcome outcome;
    private final Player currentPlayer;
    private final Set<Coordinate> handicapStones;
    
    private final SuperKoComparison positionCache;
    
    private Integer hashCode;
    private String representation;
    
    private Game( int dimension, double compensation, Set<Coordinate> handicapStones ) {
        this.capturesByBlack = 0;
        this.capturesByWhite = 0;
        this.currentPlayer = handicapStones.size() == 0 ? BLACK : WHITE;
        this.compensation = compensation;
        this.dimension = dimension;
        this.handicapStones = Collections.unmodifiableSet(handicapStones);
        this.movesPlayed = 0;
        this.outcome = IN_PROGRESS;
        this.previousMove = null;
        this.previousState = null;
        
        this.board = new Board(dimension);
        for (Coordinate coordinate : handicapStones) {
            board.set(coordinate, BLACK);
        }
        processBoard();
        
        this.positionCache = new SuperKoComparison(null, this.board);
    }
    
    private void processBoard() {
        if (dimension == 1) {
            board.set(Coordinate.C01_R01, TEMPORARILY_UNPLAYABLE);
        } else if (handicapStones.size() < dimension * dimension - 1) {
            Set<Coordinate> selfCaptures = new HashSet<>();
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                if (board.get(coordinate).countsAsLiberty() && isSelfCapture(coordinate)) {
                    selfCaptures.add(coordinate);
                }
            }
            
            for (Coordinate coordinate : selfCaptures) {
                board.set(coordinate, TEMPORARILY_UNPLAYABLE);
            }
        }
    }
    
    private boolean isSelfCapture( Coordinate coordinate ) {
        boolean isSelfCapture = true;
        for (Coordinate neighbor : coordinate.getNeighbors(dimension)) {
            Intersection value = board.get(neighbor);
            if (value.countsAsLiberty()) {
                isSelfCapture = false;
                break;
            }
        }
        return isSelfCapture;
    }
    
    private Game( Game previousState, Move previousMove, int additionalCaptures, Board board, Outcome outcome ) {
        // Some constant fields are copied directly from the previous state.
        this.compensation = previousState.compensation;
        this.dimension = previousState.dimension;
        this.handicapStones = previousState.handicapStones;
        
        // The rest are based on the arguments.
        this.board = board;
        
        if (additionalCaptures > 0) {
            if (BLACK == previousState.currentPlayer) {
                this.capturesByBlack = previousState.capturesByBlack + additionalCaptures;
                this.capturesByWhite = previousState.capturesByWhite;
            } else {
                this.capturesByBlack = previousState.capturesByBlack;
                this.capturesByWhite = previousState.capturesByWhite + additionalCaptures;
            }
        } else {
            this.capturesByBlack = previousState.capturesByBlack;
            this.capturesByWhite = previousState.capturesByWhite;
        }
        
        if (!outcome.isOver()) {
            this.currentPlayer = previousState.currentPlayer.getOpposite();
        } else {
            this.currentPlayer = null;
        }
        
        this.movesPlayed = previousState.movesPlayed + 1;
        this.outcome = outcome;
        this.previousMove = previousMove;
        this.previousState = previousState;
        
        this.positionCache = new SuperKoComparison(previousState.positionCache, this.board);
    }
    
    private Game(
        Game previousState,
        Board finalBoard,
        int deadBlackStones,
        int deadWhiteStones,
        Outcome finalOutcome
    ) {
        this.board = finalBoard;
        this.capturesByBlack = previousState.capturesByBlack + deadWhiteStones;
        this.capturesByWhite = previousState.capturesByWhite + deadBlackStones;
        this.compensation = previousState.compensation;
        this.currentPlayer = previousState.currentPlayer;
        this.dimension = previousState.dimension;
        this.handicapStones = previousState.handicapStones;
        this.movesPlayed = previousState.movesPlayed;
        this.outcome = finalOutcome;
        this.positionCache = previousState.positionCache;
        this.previousMove = previousState.previousMove;
        this.previousState = previousState;
    }
    
    public boolean isOver() {
        return outcome.isOver();
    }
    
    public boolean wouldPassEndGame() {
        return outcome == IN_PROGRESS && PASS == previousMove;
    }

    public double getCompensation() {
        return compensation;
    }
    
    public Game getPreviousState() {
        if (previousState == null) {
            throw new IllegalStateException("This Game is the initial state; there is no previous Game.");
        }
        
        return previousState;
    }
    
    public int getCapturesFor( Player player ) {
        int value = 0;
        
        if (BLACK == player) {
            value = capturesByBlack;
        } else if (WHITE == player) {
            value = capturesByWhite;
        } else {
            throw new IllegalArgumentException("The passed Player must be either Stone.BLACK or Stone.WHITE.");
        }
        
        return value;
    }
    
    public int getDimension() {
        return dimension;
    }
    
    public int getHandicap() {
        return handicapStones.size();
    }
    
    public int getMovesPlayed() {
        return movesPlayed;
    }
    
    public Intersection get( Coordinate coordinate ) {
        return board.get(coordinate);
    }
    
    public Move getPreviousMove() {
        if (previousMove == null) {
            throw new IllegalStateException("This Game is the initial state; there is no previous Move.");
        }
        return previousMove;
    }
    
    public Outcome getOutcome() {
        return outcome;
    }
    
    public Player getCurrentPlayer() {
        if (IN_PROGRESS != outcome) {
            throw new IllegalStateException("Only a Game that is IN_PROGRESS has a current player.");
        }
        return currentPlayer;
    }
    
    public Set<Coordinate> getHandicapStonePlacements() {
        return new HashSet<>(handicapStones);
    }
    
    public Set<Group> getGroupsOfStones() {
        return getGroupsFor(board, false);
    }
    
    private Set<Group> getGroupsFor( Board board, boolean includeAllGroups ) {
        Set<Coordinate> grouped = new HashSet<>();
        Set<Group> groups = new HashSet<>();
        for (Coordinate coordinate : Coordinate.iterateOverBoard(board.getDimension())) {
            if (!grouped.contains(coordinate)) {
                Intersection value = board.get(coordinate);
                if (includeAllGroups || value == BLACK || value == WHITE) {
                    Group group = new Group(board, coordinate);
                    for (Coordinate member : group.members) {
                        grouped.add(member);
                    }
                    groups.add(group);
                }
            }
        }
        return groups;
    }
    
    public Set<Group> getAllGroups() {
        return getGroupsFor(board, true);
    }
    
    public Set<Move> getLegalMoves() {
        Set<Move> moves = new HashSet<>();
        if (outcome == IN_PROGRESS) {
            for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
                if (EMPTY == board.get(coordinate)) {
                    moves.add(coordinate);
                }
            }
            moves.add(PASS);
        }
        return moves;
    }
    
    public Game play( Move move ) {
        if (outcome != IN_PROGRESS) {
            throw new IllegalStateException("This Game is over; no further moves may be made (including passes).");
        }
        
        validateMove(move);
        
        Game next = null;
        if (PASS == move) {
            next = pass();
        } else {
            next = performMove((Coordinate) move);
        }
        return next;
    }
    
    private void validateMove( Move move ) {
        boolean valid = true;
        
        if (move == null) {
            valid = false;
        } else {
            Class<?> moveClass = move.getClass();
            if (!Pass.class.equals(moveClass)) {
                if (!Coordinate.class.equals(moveClass)) {
                    valid = false;
                } else {
                    Coordinate coordinate = (Coordinate) move;
                    if (coordinate.getColumn() > dimension || coordinate.getRow() > dimension) {
                        valid = false;
                    } else {
                        valid = EMPTY == board.get(coordinate);
                    }
                }
            }
        }
        
        if (!valid) {
            throw new IllegalArgumentException(
                "Game.move() accepts only Pass.PASS or a Coordinate that is both on the board and is currently marked" +
                " as EMPTY."
            );
        }
    }
    
    public Game pass() {
        if (outcome != IN_PROGRESS) {
            throw new IllegalStateException("This Game is over; no further moves may be made (including passes).");
        }
        
        Game next = null;
        if (PASS != previousMove) {
            next = passButContinueGame();
        } else {
            next = passAndEndGame();
        }
        return next;
    }
    
    private Game passButContinueGame() {
        Board nextBoard = prepareBoardForNextPlayer(board);
        return new Game(this, PASS, 0, nextBoard, IN_PROGRESS);
    }
    
    private Board prepareBoardForNextPlayer( Board board ) {
        Board nextBoard = new Board(board);
        Player nextPlayer = currentPlayer.getOpposite();
        
        for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
            Intersection value = nextBoard.get(coordinate);
            if (value.countsAsLiberty()) {
                boolean isPlayable = true;
                
                Board scratchPad = new Board(board);
                scratchPad.set(coordinate, (Intersection) nextPlayer);
                int captures = removeCaptures(scratchPad, coordinate, nextPlayer);
                if (captures == 0) {
                    Group group = new Group(scratchPad, coordinate);
                    if (group.liberties == 0) {
                        isPlayable = false; // self-capture
                    }
                }
                
                if (isPlayable) {
                    if (positionCache.positionHasBeenPlayedBefore(scratchPad)) {
                        isPlayable = false;
                    }
                }
                
                if (isPlayable) {
                    nextBoard.set(coordinate, EMPTY);
                } else {
                    nextBoard.set(coordinate, TEMPORARILY_UNPLAYABLE);
                }
            }
        }
        
        return nextBoard;
    }
    
    private int removeCaptures( Board board, Coordinate around, Player playedBy ) {
        int captures = 0;
        
        Intersection opposite = (Intersection) playedBy.getOpposite();
        for (Coordinate neighbor : around.getNeighbors(board.getDimension())) {
            Intersection value = board.get(neighbor);
            if (value == opposite) {
                Group group = new Group(board, neighbor);
                if (group.liberties == 0) {
                    captures += group.members.size();
                    for (Coordinate captured : group.members) {
                        board.set(captured, EMPTY);
                    }
                }
            }
        }
        
        return captures;
    }
    
    private Game passAndEndGame() {
        return new Game(this, PASS, 0, board, COMPLETE_BUT_NOT_SCORED);
    }
    
    private Game performMove( Coordinate move ) {
        Board nextBoard = new Board(board);
        nextBoard.set(move, (Intersection) currentPlayer);
        int additionalCaptures = removeCaptures(nextBoard, move, currentPlayer);
        nextBoard = prepareBoardForNextPlayer(nextBoard);
        return new Game(this, move, additionalCaptures, nextBoard, IN_PROGRESS);
    }
    
    public Game score() {
        return score(null);
    }
    
    public Game score( Set<Group> deadGroups ) {
        if (outcome != COMPLETE_BUT_NOT_SCORED) {
            throw new IllegalStateException("score() may only be called on games that are completed but not scored.");
        }
        validateDeadGroups(deadGroups);
        
        int deadBlackStones = 0;
        int deadWhiteStones = 0;
        Board clean = new Board(board);
        if (deadGroups != null && deadGroups.size() > 0) {
            for (Group group : deadGroups) {
                int count = group.members.size();
                if (group.type == BLACK) {
                    deadBlackStones += count;
                } else {
                    deadWhiteStones += count;
                }
                for (Coordinate coordinate : group.members) {
                    clean.set(coordinate, EMPTY);
                }
            }
        }
        
        // This step is not strictly necessary, but it should simplify presentation of the final scored position.
        for (Coordinate coordinate : Coordinate.iterateOverBoard(dimension)) {
            if (TEMPORARILY_UNPLAYABLE == clean.get(coordinate)) {
                clean.set(coordinate, EMPTY);
            }
        }
        
        double adjustment = (compensation + handicapStones.size()) / 2.;
        double blackPointsOnBoard = 0.;
        double whitePointsOnBoard = 0.;
        
        Set<Group> groups = getGroupsFor(clean, true);
        for (Group group : groups) {
            boolean blackScores = group.type == BLACK || group.type.countsAsLiberty() && group.bordersBlack;
            boolean whiteScores = group.type == WHITE || group.type.countsAsLiberty() && group.bordersWhite;
            if (blackScores || whiteScores) {
                double points = group.members.size();
                
                if (blackScores && whiteScores) {
                    points /= 2.;
                }
                
                if (blackScores) {
                    blackPointsOnBoard += points;
                }
                
                if (whiteScores) {
                    whitePointsOnBoard += points;
                }
            }
        }
        
        Outcome finalOutcome = Outcomes.getFinalScore(blackPointsOnBoard, -adjustment, whitePointsOnBoard, adjustment);
        return new Game(this, clean, deadBlackStones, deadWhiteStones, finalOutcome);
    }
    
    private void validateDeadGroups( Set<Group> groups ) {
        if (groups != null) {
            Set<Group> actualGroups = getAllGroups();
            for (Group candidate : groups) {
                if (!actualGroups.contains(candidate)) {
                    throw new IllegalArgumentException(
                        "Received a Group that does not exist on this Game's Board: " + candidate
                    );
                } else if (BLACK != candidate.type && WHITE != candidate.type ) {
                    throw new IllegalArgumentException(
                        "Only BLACK or WHITE groups can be marked as dead; received " + candidate
                    );
                }
            }
        }
    }
    
    public Game invalidate() {
        if (IN_PROGRESS != outcome) {
            throw new IllegalStateException("Only a Game IN_PROGRESS can be invalided.");
        }
        
        return new Game(this, board, 0, 0, Invalidated.INVALIDATED);
    }
    
    public Game resume() {
        if (COMPLETE_BUT_NOT_SCORED != outcome) {
            throw new IllegalStateException("Only a Game COMPLETE_BUT_NOT_SCORED can be resumed.");
        }
        
        return previousState.previousState;
    }
    
    @Override
    public boolean equals( Object other ) {
        boolean result = this == other;
        if (!result && other != null && Game.class.equals(other.getClass())) {
            Game that = (Game) other;
            result =
                capturesByBlack == that.capturesByBlack &&
                capturesByWhite == that.capturesByWhite &&
                compensation == that.compensation &&
                currentPlayer == that.currentPlayer &&
                dimension == that.dimension &&
                movesPlayed == that.movesPlayed &&
                previousMove == that.previousMove &&
                handicapStones.equals(that.handicapStones) &&
                outcome.equals(that.outcome) &&
                board.equals(that.board) &&
                Objects.equal(previousState, that.previousState);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        if (hashCode == null) {
            HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(previousState); // this could take a while...
            builder.append(previousMove);
            builder.append(capturesByBlack);
            builder.append(capturesByWhite);
            builder.append(compensation);
            builder.append(currentPlayer);
            builder.append(dimension);
            builder.append(movesPlayed);
            builder.append(outcome);
            builder.append(handicapStones);
            builder.append(board);
            hashCode = builder.toHashCode();
        }
        
        return hashCode;
    }
    
    @Override
    public String toString() {
        if (representation == null) {
            StringBuilder builder = new StringBuilder();
            builder.append(dimension);
            builder.append("x");
            builder.append(dimension);
            builder.append(" Game ");
            builder.append(outcome);
            builder.append(", ");
            builder.append(movesPlayed);
            builder.append(" Moves");
            
            if (!outcome.isOver()) {
                builder.append(", ");
                builder.append(currentPlayer);
                builder.append(" to Play");
            }
            
            builder.append("\nCompensation ");
            builder.append(compensation);
            builder.append(", Handicap ");
            builder.append(handicapStones.size());
            if (handicapStones.size() > 0) {
                builder.append(" ");
                builder.append(handicapStones);
            }
            builder.append("\nBlack Captures ");
            builder.append(capturesByBlack);
            builder.append(", White Captures ");
            builder.append(capturesByWhite);
            if (previousMove != null) {
                builder.append("\nPrevious Move @ ");
                builder.append(previousMove);
                builder.append(", Previous State Hash: ");
                builder.append(previousState.hashCode());
            }
            builder.append("\n");
            builder.append(board);
            
            representation = builder.toString();
        }
        
        return representation;
    }
}
