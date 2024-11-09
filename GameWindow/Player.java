package GameWindow;

import java.util.ArrayList;

import Pieces.*;

public class Player {
    private final PieceColor color;
    private final ArrayList<String> playerMovesRecord = new ArrayList<>();

    // White player:
    private Piece whiteKing;
    private Piece rightWhiteRook;
    private Piece leftWhiteRook;
    private final ArrayList<Tile> whiteTotalAvailableMove = new ArrayList<>();

    // Black player:
    private Piece blackKing;
    private Piece rightBlackRook;
    private Piece leftBlackRook;
    private final ArrayList<Tile> blackTotalAvailableMove = new ArrayList<>();

    public Player(PieceColor color) {
        this.color = color;
    }

    /**
     * Initialize players pieces in their starting point
     */
    public ArrayList<Piece> initializePieces(PieceColor color, GamePanel gamePanel) {
        ArrayList<Piece> pieces = new ArrayList<>();
        int kingRow = PieceColor.getFirstLine(color);
        int pawnRow = PieceColor.getPawnLine(color);

        // initialize pieces
        Piece rook1 = new Rook(true, kingRow, 0, color, gamePanel);
        Piece rook2 = new Rook(true, kingRow, 7, color, gamePanel);
        Piece knight1 = new Knight(true, kingRow, 1, color, gamePanel);
        Piece knight2 = new Knight(true, kingRow, 6, color, gamePanel);
        Piece bishop1 = new Bishop(true, kingRow, 2, color, gamePanel);
        Piece bishop2 = new Bishop(true, kingRow, 5, color, gamePanel);
        Piece queen = new Queen(true, kingRow, 3, color, gamePanel);
        Piece king = new King(true, kingRow, 4, color, gamePanel);

        // add pieces to pieces list
        pieces.add(rook1);
        pieces.add(knight1);
        pieces.add(bishop1);
        pieces.add(queen);
        pieces.add(bishop2);
        pieces.add(knight2);
        pieces.add(rook2);
        pieces.add(king);

        // add pawns to lists
        for (int i = 0; i < 8; i++) {
            Piece pawn = new Pawn(true, kingRow + pawnRow, i, color, gamePanel);
            pieces.add(pawn);
        }
        saveKings(king);
        return pieces;
    }

    /**
     * Delete attacked piece from its set
     */
    public void removePieceFromSet(Piece piece) {
        for (Piece pieceInSet : piece.getColor().getPlayerPieces()) {
            if (piece.equals(pieceInSet)) {
                piece.getColor().getPlayerPieces().remove(piece);
                break;
            }
        }
    }

    /**
     * Add piece to player pieces set
     */
    public void addPieceToSet(Piece piece) {
        // add piece to player pieces set
        piece.getColor().getPlayerPieces().add(piece);
    }

    /**
     * Resets white and black total moves tiles arraylists
     */
    public void resetTotalMove() {
        if (this.getColor().equals(PieceColor.WHITE)) {
            whiteTotalAvailableMove.clear();
        } else {
            blackTotalAvailableMove.clear();
        }
    }

    /**
     * Saves black and white kings
     */
    public void saveKings(Piece king) {
        if (color.equals(PieceColor.WHITE)) {
            whiteKing = king;
        } else {
            blackKing = king;
        }
    }


    // Getters & Setters:

    /**
     * Get piece color (PieceColor enum type)
     */
    public PieceColor getColor() {
        return color;
    }

    /**
     * Get black player total moves to play
     */
    public ArrayList<Tile> getBlackTotalAvailableMove() {
        return blackTotalAvailableMove;
    }

    /**
     * Get white player total moves to play
     */
    public ArrayList<Tile> getWhiteTotalAvailableMove() {
        return whiteTotalAvailableMove;
    }

    /**
     * Get white player left rook
     */
    public Piece getLeftWhiteRook() {
        return leftWhiteRook;
    }

    /**
     * Get white player right rook
     */
    public Piece getRightWhiteRook() {
        return rightWhiteRook;
    }

    /**
     * Get black player left rook
     */
    public Piece getLeftBlackRook() {
        return leftBlackRook;
    }

    /**
     * Get black player right rook
     */
    public Piece getRightBlackRook() {
        return rightBlackRook;
    }

    /**
     * Set black player left rook
     */
    public void setLeftBlackRook(Piece leftBlackRook) {
        this.leftBlackRook = leftBlackRook;
    }

    /**
     * Set black player right rook
     */
    public void setRightBlackRook(Piece rightBlackRook) {
        this.rightBlackRook = rightBlackRook;
    }

    /**
     * Set white player left rook
     */
    public void setLeftWhiteRook(Piece leftWhiteRook) {
        this.leftWhiteRook = leftWhiteRook;
    }

    /**
     * Set white player right rook
     */
    public void setRightWhiteRook(Piece rightWhiteRook) {
        this.rightWhiteRook = rightWhiteRook;
    }

    /**
     * Get black king
     */
    public Piece getBlackKing() {
        return blackKing;
    }

    /**
     * Get white king
     */
    public Piece getWhiteKing() {
        return whiteKing;
    }

    /**
     * Get black king in King type
     */
    public King getBlackKingType() {
        return (King) getBlackKing();
    }

    /**
     * Get white king in King type
     */
    public King getWhiteKingType() {
        return (King) getWhiteKing();
    }

    /**
     * Get player's moves records
     */
    public ArrayList<String> getPlayerMovesRecord() {
        return playerMovesRecord;
    }
}