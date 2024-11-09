package Pieces;

import GameWindow.GamePanel;
import GameWindow.Tile;

public class Rook extends Piece {
    private static final String ROOK = "Rook";
    private boolean canCast = true;

    public Rook(boolean alive, int row, int col, PieceColor color, GamePanel gamePanel) {
        super(alive, ROOK, row, col, color, gamePanel);
        if (gamePanel.getTurnNumber() < 1) {
            this.getColor().setRookSide(this);
        }
        pieceDenote = "R";
    }

    public void moveCastledRook(int row, int col) {
        this.getPieceTile().setTilePiece(null);
        Tile tile = gamePanel.getTile(row, col);
        this.setRow(row);
        this.setCol(col);
        tile.setTilePiece(this);
        this.setCanCast(false);
        removeCastlingTile();
    }

    @Override
    public boolean move(int row, int col) {
        if (super.move(row, col)) {
            this.setCanCast(false);
            removeCastlingTile();
            return true;
        }

        if (gamePanel.getKingByColor(this.getColor()).getIsCastling()) {
            this.setRow(col);
            this.setCol(row);
            gamePanel.getTile(col, row).setTilePiece(this);
            gamePanel.getKingByColor(this.getColor()).setIsCastling(false);
            this.setCanCast(false);
        }
        return false;
    }

    @Override
    public boolean attack(Piece attacked) {
        if (super.attack(attacked)) {
            this.setCanCast(false);
            removeCastlingTile();
            return true;
        }
        return false;
    }

    @Override
    public void calcPossibleMoves(boolean draw) {
        int row = this.getRow();
        int col = this.getCol();
        PieceColor pieceColor = this.getColor();
        this.resetMovesPossibilities();

        // check available tiles in piece column
        for (int i = 0; i < 8; i++) {
            this.calcStraightMoves(row, col, i, pieceColor, draw);
        }
    }

    /**
     * Remove player's rook castling tiles
     */
    public void removeCastlingTile() {
        if (this.getColor().equals(PieceColor.WHITE)) {
            if (gamePanel.getWhitePlayer().getRightWhiteRook().equals(this)) {
                gamePanel.getWhitePlayer().getWhiteKingType().getRightCastlingTile().setCastlingTile(false);
            } else {
                gamePanel.getWhitePlayer().getWhiteKingType().getLeftCastlingTile().setCastlingTile(false);
            }
        } else {
            if (gamePanel.getBlackPlayer().getRightBlackRook().equals(this)) {
                gamePanel.getBlackPlayer().getBlackKingType().getRightCastlingTile().setCastlingTile(false);
            } else {
                gamePanel.getBlackPlayer().getBlackKingType().getLeftCastlingTile().setCastlingTile(false);
            }
        }
    }

    // Getters & Setters:
    public boolean getCanCast() {
        return canCast;
    }

    public void setCanCast(boolean cast) {
        this.canCast = cast;
    }
}
