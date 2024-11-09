package Pieces;

import GameWindow.GamePanel;
import GameWindow.Tile;

public class Knight extends Piece {

    public static final String KNIGHT = "Knight";

    public Knight(boolean alive, int row, int col, PieceColor color, GamePanel gamePanel) {
        super(alive, KNIGHT, row, col, color, gamePanel);
        pieceDenote = "N";
    }

    @Override
    public void calcPossibleMoves(boolean draw) {
        int row = this.getRow();
        int col = this.getCol();
        Piece pieceOriginalPosition = gamePanel.getTile(row, col).getTilePiece();
        int twoStep = 2;
        int step = 1;
        PieceColor pieceColor = this.getColor();
        if (pieceColor.equals(PieceColor.BLACK)) {
            twoStep = -2;
        }

        for (int i = 0; i < 8; i++) {
            calcRightLeftSteps(row, col, step, twoStep, draw, pieceColor, pieceOriginalPosition);
            calcUpDownSteps(row, col, step, twoStep, draw, pieceColor, pieceOriginalPosition);
            step *= -1;
            if (i % 2 == 0) {
                twoStep *= -1;
            }
        }
        gamePanel.setThreatenPosition();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Saves knight last position
     */
    public void saveKnightPosition() {
        this.setLastMoveRow(this.getRow());
        this.setLastMoveCol(this.getCol());
    }

    /**
     * Calculates knight up and down moves
     */
    public void calcUpDownSteps(int row, int col, int step, int twoStep, boolean draw, PieceColor pieceColor, Piece pieceOriginalPosition) {
        Tile upDownTile = gamePanel.getTile(row + twoStep, col + step);
        if (upDownTile != null) {
            if (upDownTile.getTilePiece() == null) {
                upDownTile.setThreatColor(pieceColor, pieceOriginalPosition);
                if (draw) {
                    upDownTile.setAvailableMove(true);
                }
            } else if (upDownTile.getTilePiece().getColor() != pieceColor) {
                upDownTile.setThreatColor(pieceColor, pieceOriginalPosition);
                if (draw) {
                    upDownTile.setAvailableAttack(true);
                } else if (upDownTile.getTilePiece() != null && (upDownTile.getTilePiece() instanceof King)) {
                    upDownTile.setThreatColor(pieceColor, pieceOriginalPosition);
                    if (gamePanel.getTile(row, col).getTilePiece() != null) {
                        gamePanel.getTile(row, col).getTilePiece().setCheck(true);
                    }
                }
            }
        }
    }

    /**
     * Calculates knight right and left moves
     */
    public void calcRightLeftSteps(int row, int col, int step, int twoStep, boolean draw, PieceColor pieceColor, Piece pieceOriginalPosition) {
        Tile leftRightTile = gamePanel.getTile(row + step, col + twoStep);
        if (leftRightTile != null) {
            Piece threatenPiece = gamePanel.getTile(row, col).getTilePiece();
            if (leftRightTile.getTilePiece() == null) {
                leftRightTile.setThreatColor(pieceColor, pieceOriginalPosition);
                if (draw) {
                    leftRightTile.setAvailableMove(true);
                }
            } else if (leftRightTile.getTilePiece().getColor() != pieceColor) {
                leftRightTile.setThreatColor(pieceColor, pieceOriginalPosition);
                if (draw) {
                    leftRightTile.setAvailableAttack(true);
                } else if (leftRightTile.getTilePiece() != null && (leftRightTile.getTilePiece() instanceof King)) {
                    leftRightTile.setThreatColor(pieceColor, pieceOriginalPosition);
                    if (threatenPiece != null) {
                        threatenPiece.setCheck(true);
                    }
                }
            }
        }
    }


    // Getters & Setters:
    /**
     * Set knight last move row
     */
    public void setLastMoveRow(int oldRow) {
    }

    /**
     * Set knight last move col
     */
    public void setLastMoveCol(int oldCol) {
    }
}
