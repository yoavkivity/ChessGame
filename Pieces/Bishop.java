package Pieces;

import GameWindow.GamePanel;

public class Bishop extends Piece {
    public static final String BISHOP = "Bishop";

    public Bishop(boolean alive, int row, int col, PieceColor color, GamePanel gamePanel) {
        super(alive, BISHOP, row, col, color, gamePanel);
        pieceDenote = "B";
    }

    @Override
    public void calcPossibleMoves(boolean draw) {
        int row = this.getRow();
        int col = this.getCol();
        PieceColor pieceColor = this.getColor();
        this.resetMovesPossibilities();

        // upper right diagonal
        for (int i = 0; i < 8; i++) {
            this.calcDiagonalMoves(row, col, i, pieceColor, draw);
        }
    }
}
