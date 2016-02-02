package loa;

/** A Player that prompts for moves and reads them from its Game.
 *  @author Lucy Chen */
class HumanPlayer extends Player {

    /** A HumanPlayer that plays the SIDE pieces in GAME.  It uses
     *  GAME.getMove() as a source of moves.  */
    HumanPlayer(Piece side, Game game) {
        super(side, game);
    }

    @Override
    Move makeMove() {
        Move move = getGame().getMove();
        return move;
    }

}
