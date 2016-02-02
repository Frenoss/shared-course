package loa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.Formatter;
import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;

import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Direction.*;

/** Represents the state of a game of Lines of Action.
 *  @author Lucy Chen
 */
class Board implements Iterable<Move> {

    /** Size of a board. */
    static final int M = 8;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row - 1][col - 1]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is MxM.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        clear();
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _moves.clear();
        for (int r = 1; r <= M; r += 1) {
            for (int c = 1; c <= M; c += 1) {
                set(c, r, contents[r - 1][c - 1]);
            }
        }
        _turn = side;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        initialize(board._board, board.turn());
        _moves.addAll(board._moves);
    }

    /** Return the contents of column C, row R, where 1 <= C,R <= 8,
     *  where column 0 corresponds to column 'a' in the standard
     *  notation. */
    Piece get(int c, int r) {
        return _board[r - 1][c - 1];
    }

    /** Return the contents of the square SQ.  SQ must be the
     *  standard printed designation of a square (having the form cr,
     *  where c is a letter from a-h and r is a digit from 1-8). */
    Piece get(String sq) {
        return get(col(sq), row(sq));
    }

    /** Return the column number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int col(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(0) - 'a' + 1;
    }

    /** Return the row number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int row(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(1) - '0';
    }

    /** Set the square at column C, row R to V, and make NEXT the next side
     *  to move, if it is not null. */
    void set(int c, int r, Piece v, Piece next) {
        _board[r - 1][c - 1] = v;
        if (next != null) {
            _turn = next;
        }
    }

    /** Set the square at column C, row R to V. */
    void set(int c, int r, Piece v) {
        set(c, r, v, null);
    }

    /** Assuming isLegal(MOVE), make MOVE. */
    void makeMove(Move move) {
        assert isLegal(move);
        _moves.add(move);
        Piece replaced = move.replacedPiece();
        int col0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        if (replaced != EMP) {
            set(c1, r1, EMP);
        }
        set(c1, r1, move.movedPiece());
        set(col0, r0, EMP);
        _turn = _turn.opposite();
    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Move move = _moves.remove(_moves.size() - 1);
        Piece replaced = move.replacedPiece();
        int col0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        Piece movedPiece = move.movedPiece();
        set(c1, r1, replaced);
        set(col0, r0, movedPiece);
        _turn = _turn.opposite();
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Test method - Return the Piece array representing the
     *  current board state. */
    Piece[][] boardState() {
        return _board;
    }

    /** Returns the winner, if any. */
    Piece winner() {
        return winner;
    }

    /** Return true iff MOVE is legal for the player currently on move. */
    boolean isLegal(Move move) {
        if (move == null) {
            return false;
        }

        int col0 = move.getCol0();
        int row0 = move.getRow0();
        int col1 = move.getCol1();
        int row1 = move.getRow1();

        if (move.movedPiece() != turn()) {
            return false;
        }
        if ((col0 != col1) && (row0 != row1)
            && (Math.abs(col1 - col0) != Math.abs(row1 - row0))) {
            return false;
        }
        if (move.length() != pieceCountAlong(move)) {
            return false;
        }
        if (blocked(move)) {
            return false;
        }
        return true;
    }

    /** Return a sequence of all legal moves from this position. */
    Iterator<Move> legalMoves() {
        return new MoveIterator();
    }

    @Override
    public Iterator<Move> iterator() {
        return legalMoves();
    }

    /** Return true if there is at least one legal move for the player
     *  on move. */
    public boolean isLegalMove() {
        return iterator().hasNext();
    }

    /** Return true iff either player has all his pieces continguous. */
    boolean gameOver() {
        if (piecesContiguous(turn().opposite())) {
            winner = turn().opposite();
            return true;
        }
        else if (piecesContiguous(turn())) {
            winner = turn();
            return true;
        }
        return false;
    }

    /** Return true iff SIDE's pieces are continguous. */
    boolean piecesContiguous(Piece side) {
        boolean[][] trueMatrix = new boolean[8][8];
        for (int i = 0; i < M; i++) {
            for (int k = 0; k < M; k++) {
                trueMatrix[i][k] = false;
            }
        }
        ArrayList<ArrayList<Integer>> pieceLoc
            = new ArrayList<ArrayList<Integer>>();
        outerloop:
        for (int c = 1; c <= M; c ++) {
            for (int r = 1; r <= M; r++) {
                if (get(c, r) == side) {
                    markMatrix(trueMatrix, c, r);
                    ArrayList<Integer> firstPiece
                        = new ArrayList<>(Arrays.asList(r, c));
                    pieceLoc.add(firstPiece);
                    break outerloop;
                }
            }
        }
        while (!pieceLoc.isEmpty()) {
            pieceLoc = checkPce(pieceLoc, trueMatrix, side);
        }
        for (int c = 1; c <= M; c++) {
            for (int r = 1; r <= M; r++) {
                if ((get(c, r) == side) && (!trueMatrix[r - 1][c - 1])) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Takes in a list PLOCS containing the coordinates of all relevant
     *  pieces and the boolean array MATRIX and checks each set of coordinates,
     *  marking them true in MATRIX if appropriate. */
    ArrayList<ArrayList<Integer>> checkPce(ArrayList<ArrayList<Integer>> pLocs,
        boolean[][] matrix, Piece side) {
        ArrayList<ArrayList<Integer>> newLocs
            = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<Integer> coords : pLocs) {
            int r = coords.get(0);
            int c = coords.get(1);
            Direction d = NOWHERE;
            while (d.succ() != null) {
                d = d.succ();
                int newC = c + d.dc;
                int newR = r + d.dr;
                if (Move.inBounds(newC, newR)) {
                    if ((get(newC, newR) == side)
                        && (!matrix[newR - 1][newC - 1])) {
                        markMatrix(matrix, newC, newR);
                        ArrayList<Integer> markedPiece
                            = new ArrayList<>(Arrays.asList(newR, newC));
                        newLocs.add(markedPiece);
                    }
                }
            }
        }
        return newLocs;
    }

    /** Helper method that marks the location at MATRIX[C-1][R-1]
     *  with the value true. */
    private void markMatrix(boolean[][] matrix, int c, int r) {
        matrix[r - 1][c - 1] = true;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        if (this.turn() != b.turn()) {
            return false;
        }
        for (int c = 1; c <= M; c++) {
            for (int r = 1; r <= M; r++) {
                if (b.get(c, r) != this.get(c, r)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return 0; // FIXME
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = M; r >= 1; r -= 1) {
            out.format("    ");
            for (int c = 1; c <= M; c += 1) {
                out.format("%s ", get(c, r).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return the number of pieces in the line of action indicated by MOVE.
     *  Assumes that MOVE is in a compass direction. */
    private int pieceCountAlong(Move move) {
        int col0 = move.getCol0();
        int row0 = move.getRow0();
        int col1 = move.getCol1();
        int row1 = move.getRow1();

        Direction d = NOWHERE;
        int deltaC = delta(col0, col1);
        int deltaR = delta(row0, row1);

        while (d.succ() != null) {
            d = d.succ();
            if ((d.dc == deltaC) && (d.dr == deltaR)) {
                break;
            }
        }
        return pieceCountAlong(col0, row0, d);
    }

    /** Return the number of pieces in the line of action in direction DIR and
     *  containing the square at column C and row R. */
    private int pieceCountAlong(int c, int r, Direction dir) {
        int numPieces = 0;
        for (int col = c, row = r; Move.inBounds(col, row);
            col += dir.dc, row += dir.dr) {
            if (get(col, row) != EMP) {
                numPieces++;
            }
        }
        for (int col = c, row = r; Move.inBounds(col, row);
            col -= dir.dc, row -= dir.dr) {
            if (get(col, row) != EMP) {
                numPieces++;
            }
        }
        if (get(c, r) != EMP) {
            numPieces--;
        }
        return numPieces;
    }

    /** Return true iff MOVE is blocked by an opposing piece or by a
     *  friendly piece on the target square. */
    private boolean blocked(Move move) {
        int col0 = move.getCol0();
        int row0 = move.getRow0();
        int col1 = move.getCol1();
        int row1 = move.getRow1();

        int deltaC = delta(col0, col1);
        int deltaR = delta(row0, row1);

        for (int i = col0 + deltaC, k = row0 + deltaR; !(i == col1 && k == row1);
            i += deltaC, k += deltaR) {
            if (get(i, k) == move.movedPiece().opposite()) {
                return true;
            }
        }
        if (move.replacedPiece() == move.movedPiece()) {
            return true;
        }
        return false;
    }

    /** Returns 1 if END > START, 0 if END == START, and
     *  -1 if END < START. */
    private int delta(int start, int end) {
        if (end > start) {
            return 1;
        }
        if (end == start) {
            return 0;
        } else {
            return -1;
        }
    }

    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** Data structure that stores the state of the board. */
    private Piece[][] _board = new Piece[8][8];

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();

    /** Current side on move. */
    private Piece _turn;

    /** Winner of the game, if any. */
    private Piece winner = EMP;

    /** An iterator returning the legal moves from the current board. */
    private class MoveIterator implements Iterator<Move> {
        
        /** Current piece under consideration. */
        private int _c, _r;
        /** Next direction of current piece to return. */
        private Direction _dir;
        /** Next move. */
        private Move _move;

        /** A new move iterator for turn(). */
        MoveIterator() {
            _c = 1; _r = 1; _dir = NOWHERE;
            incr();
        }

        @Override
        public boolean hasNext() {
            return _move != null;
        }

        @Override
        public Move next() {
            if (_move == null) {
                throw new NoSuchElementException("no legal move");
            }

            Move move = _move;
            incr();
            return move;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Advance to the next legal move. */
        private void incr() {
            while (true) {
                while ((get(_c, _r) != turn()) || (_dir.succ() == null)) {
                    if (_c < M) {
                        _c++;
                    } 
                    else if (_r < M) {
                        _r++;
                        _c = 1;
                    } else {
                        _move = null;
                        return;
                    }
                }
                if (_dir.succ() == null) {
                    _dir = NOWHERE;
                }
                while (_dir.succ() != null) {
                    _dir = _dir.succ();
                    int d = pieceCountAlong(_c, _r, _dir);
                    Move newMove = Move.create(_c, _r, d, _dir, Board.this);
                    if (isLegal(newMove)) {
                        _move = newMove;
                        return;
                    }
                }
            }
        }
    }

}
