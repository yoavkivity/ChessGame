package Pieces;

import GameWindow.GamePanel;
import GameWindow.Player;
import GameWindow.Tile;

import java.util.ArrayList;

public enum PieceColor {
    WHITE,
    BLACK;
    private GamePanel gamePanel;
    private Player whitePlayer;
    private Player blackPlayer;

    private static final int WHITE_FIRST_ROW = 7;
    private static final int BLACK_FIRST_ROW = 0;
    private static final int WHITE_PAWN_ROW_CALC = -1;
    private static final int BLACK_PAWN_ROW_CALC = 1;
    private static final int WHITE_PAWN_EN_PASSANT = 3;
    private static final int BLACK_PAWN_EN_PASSANT = 4;
    private static final int WHITE_PAWN_AFTER_EN_PASSANT = 2;
    private static final int BLACK_PAWN_AFTER_EN_PASSANT = 5;

    /**
     * Set gamePanel
     */
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /**
     * Set black player
     */
    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    /**
     * Set white player
     */
    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    /**
     * Get gamePanel
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Get the opposite color by given color
     */
    public PieceColor getOppositeColor() {
        if (this.equals(gamePanel.getWhite())) {
            return gamePanel.getBlack();
        } else {
            return gamePanel.getWhite();
        }
    }

    /**
     * Get player's pieces
     */
    public ArrayList<Piece> getPlayerPieces() {
        if (this.equals(WHITE)) {
            return gamePanel.getWhitePlayerPieces();
        }
        return gamePanel.getBlackPlayerPieces();
    }

    /**
     * Get opposite king by given color
     */
    public Piece getOppositeKing() {
        if (this.equals(WHITE)) {
            return BLACK.blackPlayer.getBlackKing();
        }
        return WHITE.whitePlayer.getWhiteKing();
    }

    /**
     * Get all threaten tiles by given color
     */
    public ArrayList<Tile> getThreatTiles() {
        if (this.equals(PieceColor.WHITE)) {
            return gamePanel.getBlackThreatenTiles();
        }
        return gamePanel.getWhiteThreatenTiles();
    }

    /**
     * Get board first line
     */
    public static int getFirstLine(PieceColor color) {
        if (color.equals(WHITE)) {
            return WHITE_FIRST_ROW;
        }
        return BLACK_FIRST_ROW;
    }

    /**
     * Get pawn's last row that determent by its color
     */
    public static int getPawnLastRow(PieceColor color) {
        if (color.equals(WHITE)) {
            return BLACK_FIRST_ROW;
        }
        return WHITE_FIRST_ROW;
    }

    /**
     * Get pawn calculation value the determent by ots color
     */
    public static int getPawnLine(PieceColor color) {
        if (color.equals(WHITE)) {
            return WHITE_PAWN_ROW_CALC;
        }
        return BLACK_PAWN_ROW_CALC;
    }

    /**
     * Get player's all possible moves
     */
    public ArrayList<Tile> getTotalPlayerTiles() {
        if (this.equals(WHITE)) {
            return WHITE.whitePlayer.getWhiteTotalAvailableMove();
        }
        return BLACK.blackPlayer.getBlackTotalAvailableMove();
    }

    /**
     * Set rook's side, left or right
     */
    public void setRookSide(Piece rook) {
        PieceColor color = rook.getColor();
        int col = rook.getCol();
        if (color.equals(PieceColor.WHITE)) {
            if (col == 0) {
                WHITE.whitePlayer.setLeftWhiteRook(rook);
            } else {
                WHITE.whitePlayer.setRightWhiteRook(rook);
            }
        } else {
            if (col == 0) {
                BLACK.blackPlayer.setLeftBlackRook(rook);
            } else {
                BLACK.blackPlayer.setRightBlackRook(rook);
            }
        }
    }

    /**
     * Get rook's side, left or right
     */
    public Rook getRightRook(PieceColor color) {
        if (color.equals(WHITE)) {
            return (Rook) WHITE.whitePlayer.getRightWhiteRook();
        }
        return (Rook) BLACK.blackPlayer.getRightBlackRook();
    }

    /**
     * Get left rook by given color
     */
    public Rook getLeftRook(PieceColor color) {
        if (color.equals(WHITE)) {
            return (Rook) WHITE.whitePlayer.getLeftWhiteRook();
        }
        return (Rook) BLACK.blackPlayer.getLeftBlackRook();
    }

    /**
     * Get enPassant row by given color
     */
    public static int getEnPassantRow(PieceColor color) {
        if (color.equals(WHITE)) {
            return WHITE_PAWN_EN_PASSANT;
        }
        return BLACK_PAWN_EN_PASSANT;
    }

    /**
     * Get if pawn made two steps before
     */
    public void isPawnMadeTwoSteps(Piece piece, int row) {
        if (piece.getColor().equals(BLACK)) {
            if (row > piece.getRow() + 1) {
                ((Pawn) piece).setIsMadeTwoSteps(true);
            }
        } else if (row < piece.getRow() - 1) {
            ((Pawn) piece).setIsMadeTwoSteps(true);
        }
    }

    /**
     * Set pawn's two steps to false after it made
     */
    public void setTwoStepPawnFalse() {
        if (this.equals(WHITE)) {
            for (Piece piece : gamePanel.getBlackPlayerPieces()) {
                if (piece instanceof Pawn) {
                    ((Pawn) piece).setIsMadeTwoSteps(false);
                    ((Pawn) piece).setEnPassantTile(null);
                }
            }
        } else {
            for (Piece piece : gamePanel.getWhitePlayerPieces()) {
                if (piece instanceof Pawn) {
                    ((Pawn) piece).setIsMadeTwoSteps(false);
                    ((Pawn) piece).setEnPassantTile(null);
                }
            }
        }
    }

    /**
     * Get pawn's row after doing enPassant
     */
    public static int getRowAfterEnPassant(PieceColor color) {
        if (color.equals(WHITE)) {
            return WHITE_PAWN_AFTER_EN_PASSANT;
        }
        return BLACK_PAWN_AFTER_EN_PASSANT;
    }

    /**
     * Get if player is doing check by given color
     */
    public boolean getPlayerCheck() {
        if (this.equals(WHITE)) {
            return gamePanel.getWhiteCheck();
        }
        return gamePanel.getBlackCheck();
    }

    /**
     * Reset all players piece's moves
     */
    public void resetPiecesAvailableMoves() {
        for (Piece piece : getPlayerPieces()) {
            piece.pieceMovesCalculation.clear();
            piece.tilesToRemoveCalculation.clear();
        }
    }

    /**
     * Get current turn player
     */
    public Player getCurrentPlayer() {
        if (this.equals(WHITE)) {
            return gamePanel.getWhitePlayer();
        }
        return gamePanel.getBlackPlayer();
    }
}
