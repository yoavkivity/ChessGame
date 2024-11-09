package Pieces;

import GameWindow.GamePanel;
import GameWindow.Tile;

import java.io.IOException;

public class Pawn extends Piece {
    private static final String PAWN = "Pawn";
    private boolean isFirstMove = true;
    private boolean isMadeTwoSteps = false;
    private Tile enPassantTile;

    public Pawn(boolean alive, int row, int col, PieceColor color, GamePanel gamePanel) {
        super(alive, PAWN, row, col, color, gamePanel);
        pieceDenote = "P";
    }

    /**
     * Calculates pawn's possible moves
     */
    public void calcPossibleMoves(boolean draw) {
        int row = this.getRow();
        int col = this.getCol();
        PieceColor pieceColor = this.getColor();
        this.resetMovesPossibilities();

        int firstMove = 1, attackMove = 1;
        if (this.isFirstMove) {
            firstMove = pawnMove();
        }
        for (int i = 0; i <= firstMove; i++) {
            removePawnIllegalMoves();
            this.calcStraightMoves(row, col, i, pieceColor, draw);
        }
        this.calcDiagonalMoves(row, col, attackMove, pieceColor, draw);
        checkEnPassant();
    }

    /**
     * Get pawn's possible forward tiles move
     */
    public int pawnMove() {
        if (this.isFirstMove) {
            return 2;
        }
        return 1;
    }

    /**
     * Add pawn's available moves to pawn move list, without set threat color to tile
     */
    public void addPawnAvailableMove(Tile tile) {
        if (gamePanel.removeDuplicateTile(this.pieceMovesCalculation, tile)) {
            this.pieceMovesCalculation.add(tile);
        }
    }


    /**
     * Check for legal enPassant move
     */
    public void checkEnPassant() {
        if (gamePanel.getMouseInputs().getSelectedTile() != null && gamePanel.getMouseInputs().getSelectedTile().getTilePiece() != null) {
            Piece selectedPiece = gamePanel.getMouseInputs().getSelectedTile().getTilePiece();
            if (!(selectedPiece instanceof Pawn)) return;
            if (this.getRow() == PieceColor.getEnPassantRow(selectedPiece.getColor())) {
                int row = selectedPiece.getRow();
                int col = selectedPiece.getCol();
                Piece piece = gamePanel.getTile(row, col).getTilePiece();
                boolean rightTile = checkAdjacentPawns(row, col + 1);
                boolean leftTile = checkAdjacentPawns(row, col - 1);
                approveEnPassant(rightTile, leftTile, piece, selectedPiece, col);
            }
        }
    }

    /**
     * Approve legal enPassant move
     */
    public void approveEnPassant(boolean rightTile, boolean leftTile, Piece piece, Piece selectedPiece, int col) {
        if (rightTile) {
            gamePanel.getTile(PieceColor.getRowAfterEnPassant(selectedPiece.getColor()), col + 1).setAvailableAttack(true);
            gamePanel.getTile(PieceColor.getRowAfterEnPassant(selectedPiece.getColor()), col + 1).setThreatColor(piece.getColor().getOppositeColor(), piece);
            this.enPassantTile = gamePanel.getTile(PieceColor.getRowAfterEnPassant(selectedPiece.getColor()), col + 1);
            if (gamePanel.removeDuplicateTile(selectedPiece.pieceMovesCalculation, this.enPassantTile)) {
                selectedPiece.pieceMovesCalculation.add(this.enPassantTile);
            }
        } else if (leftTile) {
            gamePanel.getTile(PieceColor.getRowAfterEnPassant(selectedPiece.getColor()), col - 1).setAvailableAttack(true);
            gamePanel.getTile(PieceColor.getRowAfterEnPassant(selectedPiece.getColor()), col - 1).setThreatColor(piece.getColor().getOppositeColor(), piece);
            this.enPassantTile = gamePanel.getTile(PieceColor.getRowAfterEnPassant(selectedPiece.getColor()), col - 1);
            if (gamePanel.removeDuplicateTile(selectedPiece.pieceMovesCalculation, this.enPassantTile)) {
                selectedPiece.pieceMovesCalculation.add(this.enPassantTile);
            }
        }
    }

    /**
     * Initiate approved enPassant attack
     */
    public void enPassantAttack(int row, int col) {
        // set attacking pawn
        this.getPieceTile().setTilePiece(null);
        if (gamePanel.getTile(row, col) != null) {
            gamePanel.getTile(row, col).setTilePiece(this);
        }
        this.setRow(row);
        this.setCol(col);

        // remove attacked pawn
        gamePanel.getTile(PieceColor.getEnPassantRow(this.getColor()), col).getTilePiece().setAlive(false);
        if (gamePanel.getTile(PieceColor.getEnPassantRow(this.getColor()), col).getTilePiece() != null) {
            this.removePieceFromSet(gamePanel.getTile(PieceColor.getEnPassantRow(this.getColor()), col).getTilePiece());
        }
        gamePanel.getTile(PieceColor.getEnPassantRow(this.getColor()), col).setTilePiece(null);
    }

    /**
     * Check if enPassant pawns are adjacent
     */
    public boolean checkAdjacentPawns(int row, int col) {
        if (gamePanel.getTile(row, col) != null) {
            if (gamePanel.getTile(row, col).getTilePiece() instanceof Pawn) {
                return ((Pawn) gamePanel.getTile(row, col).getTilePiece()).getIsMadeTwoSteps();
            }
        }
        return false;
    }

    /**
     * Remove pawn illegal move directions before calculation
     */
    public void removePawnIllegalMoves() {
        this.setCheckDownSquare(false);
        this.setCheckUpperSquare(false);
        if (this.getColor().equals(PieceColor.WHITE)) {
            this.setCheckRightSquare(false);
            this.setCheckUpperRightSquare(false);
            this.setCheckDownRightSquare(false);
        } else {
            this.setCheckLeftSquare(false);
            this.setCheckUpperLeftSquare(false);
            this.setCheckDownLeftSquare(false);
        }
    }

    /**
     * Initiate promotion process
     */
    public void promotion(Piece piece) throws IOException {
        int row = piece.getRow();
        int col = piece.getCol();
        Piece pawn = gamePanel.getTile(row, col).getTilePiece();

        if (pawn == null) return;
        pawn.setAlive(false);
        removePieceFromSet(pawn);
        gamePanel.getTile(row, col).setTilePiece(null);
        if (gamePanel.getTile(row, col) != null && pawn instanceof Pawn) {
            gamePanel.setPromotionPawn(pawn);
        }
        gamePanel.setPromotion(true);
    }

    @Override
    public boolean move(int row, int col) {
        if (super.move(row, col)) {
            this.setFirstMove(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean attack(Piece attacked) {
        if (super.attack(attacked)) {
            this.setFirstMove(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pawn other) {
            if (this.isFirstMove == other.isFirstMove) {
                return super.equals(obj);
            }
            return false;
        }
        return false;
    }

    // Getters & Setters:

    /**
     * Set if move is the pawn's first move
     */
    public void setFirstMove(boolean firstMove) {
        isFirstMove = firstMove;
    }

    /**
     * Get if pawn made 2 steps first move
     */
    public boolean getIsMadeTwoSteps() {
        return this.isMadeTwoSteps;
    }

    /**
     * Set that pawn making 2 steps move
     */
    public void setIsMadeTwoSteps(boolean step) {
        this.isMadeTwoSteps = step;
    }

    /**
     * Get pawn's EnPassant move tile object
     */
    public Tile getEnPassantTile() {
        return enPassantTile;
    }

    /**
     * Set pawn's EnPassant move tile object
     */
    public void setEnPassantTile(Tile enPassantTile) {
        this.enPassantTile = enPassantTile;
    }
}
