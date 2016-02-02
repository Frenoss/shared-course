package loa;

import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import static loa.Piece.*;

/** The suite of all JUnit tests for the loa package.
 *  @author Lucy Chen
 */
public class UnitTest {

    private static final Piece[][] SOME_PIECES = {
        { EMP, BP,  WP,  BP,  WP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, BP,  EMP, EMP, WP  },
        { EMP, BP,  EMP, EMP, EMP, WP,  EMP, BP  },
        { WP,  EMP, BP,  EMP, EMP, EMP, EMP, WP  },
        { BP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { WP,  EMP, EMP, EMP, WP,  EMP, EMP, WP  },
        { EMP, BP,  BP,  EMP, BP,  BP,  WP,  EMP }
    };

    private static final Piece[][] BLACK_WIN = {
        { EMP, EMP, EMP, BP,  EMP, EMP, EMP, EMP },
        { EMP, WP,  WP,  BP,  EMP, EMP, EMP, EMP },
        { EMP, EMP, BP,  BP,  WP,  WP,  EMP, WP  },
        { EMP, WP,  BP,  WP,  WP,  EMP, EMP, EMP },
        { EMP, BP,  WP,  BP,  BP,  BP,  EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
    };

    private static final Piece[][] NO_WIN = {
        { EMP, EMP, EMP, BP,  EMP, EMP, EMP, EMP },
        { EMP, WP,  WP,  BP,  EMP, EMP, EMP, EMP },
        { EMP, EMP, BP,  BP,  WP,  WP,  EMP, WP  },
        { EMP, WP,  BP,  WP,  WP,  EMP, EMP, EMP },
        { EMP, BP,  WP,  BP,  BP,  BP,  EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, WP,  EMP, EMP, BP,  EMP, EMP }
    };

    private static final Piece[][] APC_BLACK_WIN = {
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, WP,  WP,  EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, BP,  WP,  WP,  EMP, WP  },
        { EMP, WP,  EMP, WP,  WP,  EMP, EMP, EMP },
        { EMP, EMP, WP,  EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, WP,  EMP, EMP, EMP, EMP, EMP }
    };

    private static final Piece[][] TWOGRP_NO_WIN = {
        { EMP, EMP, EMP, BP,  EMP, EMP, EMP, EMP },
        { EMP, WP,  WP,  BP,  EMP, EMP, EMP, EMP },
        { EMP, EMP, BP,  BP,  WP,  WP,  EMP, EMP },
        { EMP, WP,  BP,  WP,  WP,  EMP, EMP, EMP },
        { EMP, BP,  WP,  BP,  BP,  BP,  EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, BP,  EMP, EMP }
    };

    private static final Piece[][] TEST = {
        { WP,  EMP, EMP, BP,  BP,  WP,  EMP, WP  },
        { WP,  EMP, WP,  BP,  EMP, EMP, WP,  WP  },
        { WP,  EMP, WP,  EMP, BP,  EMP, EMP, WP  },
        { EMP, EMP, EMP, EMP, BP,  EMP, EMP, WP  },
        { EMP, EMP, EMP, BP,  EMP, BP,  EMP, EMP },
        { EMP, BP,  BP,  EMP, BP,  BP,  EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
    };

    /** Tests the basic Board.get method. */
    @Test
    public void boardGetTest() {
        Board board = new Board();

        assertEquals(EMP, board.get(1, 1));
        assertEquals(BP, board.get(6, 1));
        assertEquals(WP, board.get(1, 6));
    }

    /** Tests the Board.set method. */
    @Test
    public void boardSetTest() {
        Board board = new Board();
        board.set(4, 4, BP, WP);
        board.set(3, 7, WP);

        assertEquals(WP, board.turn());
        assertEquals(BP, board.get(4, 4));
        assertEquals(WP, board.get(3, 7));

        board.set(3, 7, EMP, BP);

        assertEquals(BP, board.turn());
        assertEquals(EMP, board.get(3, 7));
    }

    /** Tests the Board.copyFrom method. */
    @Test
    public void boardCopyFromTest() {
        Board origBoard = new Board(SOME_PIECES, BP);
        Board copyBoard = new Board(origBoard);

        assertArrayEquals(origBoard.boardState(), copyBoard.boardState());
        assertEquals(origBoard.get(3, 1), copyBoard.get(3, 1));
        assertEquals(origBoard.get(4, 8), copyBoard.get(4, 8));
        assertEquals(origBoard.get(5, 3), copyBoard.get(5, 3));
    }

    /** Tests the Board.isLegal method. */
    @Test
    public void boardIsLegalTest() {
        Board board = new Board();
        Move bpMovesEmp = Move.create(1, 1, 3, 3, board);
        assertFalse(board.isLegal(bpMovesEmp));

        Move bpMovesWp = Move.create(1, 2, 4, 2, board);
        assertFalse(board.isLegal(bpMovesWp));

        Move badMoveDir = Move.create(2, 1, 3, 3, board);
        assertFalse(board.isLegal(badMoveDir));

        Move badHori = Move.create(2, 1, 1, 1, board);
        Move badVert = Move.create(2, 1, 2, 2, board);
        Move badDiag = Move.create(2, 1, 3, 2, board);

        assertFalse(board.isLegal(badHori));
        assertFalse(board.isLegal(badVert));
        assertFalse(board.isLegal(badDiag));

        Board midBoard = new Board(SOME_PIECES, WP);
        Move midBlocked = Move.create(1, 3, 6, 8, midBoard);
        Move endBlocked = Move.create(1, 3, 3, 1, midBoard);

        assertFalse(midBoard.isLegal(midBlocked));
        assertFalse(midBoard.isLegal(endBlocked));
    }

    /** Tests the Board.piecesContiguous method. */
    @Test
    public void boardPiecesContiguousTest() {
        Board noWin = new Board(NO_WIN, BP);
        Board bWin = new Board(BLACK_WIN, BP);
        Board bWin1Pc = new Board(APC_BLACK_WIN, BP);
        Board noWin2Grp = new Board(TWOGRP_NO_WIN, BP);

        assertFalse(noWin.piecesContiguous(BP));
        assertFalse(noWin.piecesContiguous(WP));
        assertTrue(bWin.piecesContiguous(BP));
        assertFalse(bWin.piecesContiguous(WP));
        assertTrue(bWin1Pc.piecesContiguous(BP));
        assertFalse(noWin2Grp.piecesContiguous(WP));
    }

    /** Tests the Board.equals method. */
    @Test
    public void boardEqualsTest() {
        Board b1 = new Board();
        Board b2 = new Board(b1);
        Board b3 = new Board(SOME_PIECES, BP);

        assertTrue(b1.equals(b2));
        assertFalse(b1.equals(b3));
    }

    /** Tests the incr() method in MoveIterator by calling next. */
    @Test
    public void boardMvIterNextTest() {
        Board b = new Board(APC_BLACK_WIN, BP);
        Iterator<Move> iter = b.iterator();
        ArrayList<Move> legalMoves = new ArrayList<Move>();

        while (iter.hasNext()) {
            legalMoves.add(iter.next());
        }

        assertEquals(3, legalMoves.size());
    }

    /** Tests the modified Board.gameOver method. */
    @Test
    public void boardGameOverTest() {
        Board b = new Board(BLACK_WIN, BP);

        assertTrue(b.gameOver());
        assertEquals(BP, b.winner());

        b.set(1, 1, EMP, WP);
        b.gameOver();
        
        assertEquals(BP, b.winner());

        Board bTurn = new Board(TEST, BP);
        Board wTurn = new Board(TEST, WP);
        bTurn.gameOver();
        wTurn.gameOver();

        assertEquals(BP, bTurn.winner());
        assertEquals(BP, wTurn.winner());
    }

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }
}
