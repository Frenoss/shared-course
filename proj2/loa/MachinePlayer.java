package loa;

import java.util.Iterator;
import java.util.ArrayList;
import java.lang.Integer;
import java.util.Random;

/** An automated Player.
 *  @author Lucy Chen */
class MachinePlayer extends Player {

    /** Number of moves ahead the AI will look when making a move. */
    private static final int MAX_DEPTH = 2;

    /** Initial value of a move. */
    private static final int INIT_VALUE = 0;

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    @Override
    Move makeMove() {
        Move move = findBestMove(side(), getBoard(), MAX_DEPTH, INIT_VALUE);
        return move;
    }

    /** Finds the best move for a given board state. */
    private Move findBestMove(Piece s, Board board, int depth, double cutoff) {
        if (depth == 0) {
            return guessBestMove(s, board, cutoff);
        }

        Move bestSoFar = Move.create(0, 0, 0, 0, board);
        for (Move m : getBoard()) {
            Board next = new Board();
            next.copyFrom(board);
            next.makeMove(m);
            Move response = findBestMove(s.opposite(), next, depth - 1,
                -(bestSoFar.value()));
            if (-(response.value()) > bestSoFar.value()) {
                m.changeValue(-(response.value()));
                bestSoFar = m;
                if (m.value() >= cutoff) {
                    break;
                }
            }
        }
        return bestSoFar;
    }

    private Move guessBestMove(Piece side, Board board, double cutoff) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (Move m : getBoard()) {
            Board next = new Board();
            next.copyFrom(board);
            next.makeMove(m);
            if (next.gameOver()) {
                if (next.winner() == side) {
                    m.changeValue(Integer.MIN_VALUE);
                } else {
                    m.changeValue(Integer.MAX_VALUE);
                }
            }
            if (m.value() >= cutoff) {
                moves.add(m);
                break;
            }
        }
        return moves.get(rand.nextInt(moves.size()));
    }

    private ArrayList<Move> createMoveList(Board board) {
        Iterator<Move> iter = board.iterator();
        ArrayList<Move> moveList = new ArrayList<Move>();
        while (iter.hasNext()) {
            moveList.add(iter.next());
        }
        return moveList;
    }

    private Random rand = new Random();

}
