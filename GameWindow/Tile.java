package GameWindow;

import Pieces.Piece;
import Pieces.PieceColor;

import java.awt.*;
import java.util.ArrayList;

import Pieces.Pawn;

public class Tile {

    // Tile basics details:
    private final GamePanel gamePanel;
    private Graphics graph;
    private final int col;
    private final int row;
    private Piece tilePiece = null;

    // Tile state:
    private boolean selected = false;
    private boolean availableMove = false;
    private boolean availableAttack = false;
    private boolean kingMove = false;
    private boolean castlingTile = false;
    private final ArrayList<Tile> attackTiles = new ArrayList<>();

    // Tile threat state:
    private boolean whiteThreat = false;
    private boolean blackThreat = false;


    public Tile(int row, int col, GamePanel gamePanel) {
        this.row = row;
        this.col = col;
        this.gamePanel = gamePanel;
    }

    /**
     * Set tile's color threats
     */
    public void setThreatColor(PieceColor color, Piece piece) {
        setTileThreat(color);
        if (piece == null) return;
        if (checkThreatValidation(piece)) {
            if (gamePanel.removeDuplicateTile(piece.pieceMovesCalculation, this)) {
                piece.pieceMovesCalculation.add(this);
            }
        }
    }

    /**
     * Check if tile is threaten by piece
     */
    public boolean checkThreatValidation(Piece piece) {
        boolean isValidTile = true;
        for (Tile tile : piece.pieceMovesCalculation) {
            isValidTile = checkPawnThreatValidation(piece, tile);
            if (tile.equals(this)) {
                return false;
            }
            return isValidTile;
        }
        return isValidTile;
    }

    /**
     * Check pawns tile threats
     */
    public boolean checkPawnThreatValidation(Piece piece, Tile tile) {
        boolean isValidTile = true;
        if (piece instanceof Pawn) {
            // prevents counting attacking pawn tile when: there isn't piece in tile, there is same color piece int tile
            if ((this.getCol() != piece.getCol()) &&
                    ((this.getTilePiece() == null) || (this.getTilePiece().getColor().equals(piece.getColor())))) {
                isValidTile = false;
            }
            // prevents counting move forwards tile when forward tile has a piece inside
            if (this.getCol() == piece.getCol()) {
                if (tile.getTilePiece() != null) {
                    isValidTile = false;
                }
            }
            if (((Pawn) piece).getEnPassantTile() != null) {
                isValidTile = true;
            }
        }
        return isValidTile;
    }

    /**
     * Set Tile's threat color
     */
    public void setTileThreat(PieceColor color) {
        if (color.equals(PieceColor.WHITE)) {
            this.whiteThreat = true;
        }
        if (color.equals(PieceColor.BLACK)) {
            this.blackThreat = true;
        }
    }

    /**
     * Get specific tile in tile list
     */
    public Tile findTileInList(ArrayList<Tile> tiles) {
        for (Tile tile : tiles) {
            if (tile.equals(this)) {
                return tile;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile other) {
            return (this.getRow() == other.getRow() && this.getCol() == other.getCol());
        }
        return false;
    }


    // Getters & Setters:

    /**
     * Get tile's col
     */
    public int getCol() {
        return col;
    }

    /**
     * Get tile's row
     */
    public int getRow() {
        return row;
    }

    /**
     * Set tile's color in order to paint them on board game
     */
    public void setTileColor(Color tileColor) {
    }

    /**
     * Set Graphics object for drawing
     */
    public void setGraph(Graphics graph) {
        this.graph = graph;
    }

    /**
     * Get Graphics object from gamPanel object
     */
    public Graphics getGraph() {
        return graph;
    }

    /**
     * Set tile as selected tile
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Get if tile is an available move
     */
    public boolean getAvailableMove() {
        return this.availableMove;
    }

    /**
     * Set if tile is an available move
     */
    public void setAvailableMove(boolean availableMove) {
        this.availableMove = availableMove;
    }

    /**
     * Get if tile is an available attack
     */
    public boolean getAvailableAttack() {
        return this.availableAttack;
    }

    /**
     * Set if tile is an available attack
     */
    public void setAvailableAttack(boolean availableAttack) {
        this.availableAttack = availableAttack;
        attackTiles.add(this);
    }

    /**
     * Get the piece set in the given tile
     */
    public Piece getTilePiece() {
        return tilePiece;
    }

    /**
     * Set given piece in tile
     */
    public void setTilePiece(Piece tilePiece) {
        this.tilePiece = tilePiece;
    }

    /**
     * Get if tile is selected
     */
    public boolean getSelected() {
        return this.selected;
    }

    /**
     * Set move as current player king available move
     */
    public void setKingMove(boolean kingMove) {
        this.kingMove = kingMove;
    }

    /**
     * Get if move is a current player king available move
     */
    public boolean getKingMove() {
        return this.kingMove;
    }

    /**
     * Set tile as castling participant tile
     */
    public void setCastlingTile(boolean castlingTile) {
        this.castlingTile = castlingTile;
    }

    /**
     * Get if tile is a castling participant tile
     */
    public boolean getCastlingTile() {
        return this.castlingTile;
    }

    /**
     * Set tile threat to white
     */
    public void setWhiteThreat(boolean whiteThreat) {
        this.whiteThreat = whiteThreat;
    }

    /**
     * Get if tile threat is white
     */
    public boolean getWhiteThreat() {
        return this.whiteThreat;
    }

    /**
     * Set tile threat to black
     */
    public void setBlackThreat(boolean blackThreat) {
        this.blackThreat = blackThreat;
    }

    /**
     * Get if tile threat is black white
     */
    public boolean getBlackThreat() {
        return this.blackThreat;
    }

}
