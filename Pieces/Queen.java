package Pieces;

import GameWindow.GamePanel;

public class Queen extends Piece {
    private static final String QUEEN = "Queen";

    public Queen(boolean alive, int row, int col, PieceColor color, GamePanel gamePanel) {
        super(alive, QUEEN, row, col, color,gamePanel);
        pieceDenote = "Q";
    }

    @Override
    public void calcPossibleMoves(boolean draw) {
        int row = this.getRow();
        int col = this.getCol();
        PieceColor pieceColor = this.getColor();
        this.resetMovesPossibilities();

        for (int i = 0; i < 8; i++) {
            this.calcStraightMoves(row, col, i, pieceColor, draw);
            this.calcDiagonalMoves(row, col, i, pieceColor, draw);
        }
    }
}
