package Pieces;

import GameWindow.GamePanel;
import GameWindow.Tile;

import java.util.ArrayList;

public class King extends Piece {
    public static final String KING = "King";
    private boolean canCast = true;
    private boolean isCastling;
    private ArrayList<Tile> kingPossibleTile = new ArrayList<>();
    private ArrayList<Tile> oppositeKingPossibleTile = new ArrayList<>();
    private final ArrayList<Tile> rightCastlingTiles = new ArrayList<>();
    private final ArrayList<Tile> leftCastlingTiles = new ArrayList<>();
    private Tile rightCastlingTile;
    private Tile leftCastlingTile;
    private int[] xPosKing;
    private int[] yPosKing;
    private int[] xPosOppositeKing;
    private int[] yPosOppositeKing;
    private int xPos, yPos;

    public King(boolean alive, int row, int col, PieceColor color, GamePanel gamePanel) {
        super(alive, KING, row, col, color, gamePanel);
        pieceDenote = "K";
        this.isCastling = false;
    }


    @Override
    public boolean move(int row, int col) {
        if (super.move(row, col)) {
            this.setCanCast(false);
            if (gamePanel.getTile(row, col).getCastlingTile()) {
                if (col == 6) {
                    this.getColor().getRightRook(this.getColor()).moveCastledRook(row, col - 1);
                    gamePanel.getMouseInputs().setCoordinatesRecord("");
                    gamePanel.getMouseInputs().setPieceRecord("O-O");
                    gamePanel.getMouseInputs().setRecordCastling(true);

                } else {
                    this.getColor().getLeftRook(this.getColor()).moveCastledRook(row, col + 1);
                    gamePanel.getMouseInputs().setCoordinatesRecord("");
                    gamePanel.getMouseInputs().setPieceRecord("O-O-O");
                    gamePanel.getMouseInputs().setRecordCastling(true);
                }
            }
            removeCastlingTiles();
            return true;
        }
        return false;
    }

    @Override
    public boolean attack(Piece attacked) {
        if (super.attack(attacked)) {
            this.setCanCast(false);
            removeCastlingTiles();
            return true;
        }
        return false;
    }

    @Override
    public void calcPossibleMoves(boolean draw) {
        this.resetMovesPossibilities();
        calcKingMove();
    }

    /**
     * Prevent from creating situation of 2 adjacent kings after left castling
     */
    public boolean preventAdjacentKingsLeftCastling() {
        int kingRow = PieceColor.getFirstLine(this.getColor());
        int pawnRow = PieceColor.getPawnLine(this.getColor());
        // remove tile (1,6) and tile (6,6) are the only moves that can create an adjacent kings
        return (this.getColor().getOppositeKing().getPieceTile()).equals(gamePanel.getTile(kingRow + pawnRow, 6));
    }

    /**
     * Prevent from creating situation of 2 adjacent kings after right castling
     */
    public boolean preventAdjacentKingsRightCastling() {
        int kingRow = PieceColor.getFirstLine(this.getColor());
        int pawnRow = PieceColor.getPawnLine(this.getColor());
        // remove tile (1,1), (1,2), (6,1), (6,2), are the only moves that can create an adjacent kings
        if ((this.getColor().getOppositeKing().getPieceTile()).equals(gamePanel.getTile(kingRow + pawnRow, 2))) {
            return true;
        }
        return (this.getColor().getOppositeKing().getPieceTile()).equals(gamePanel.getTile(kingRow + pawnRow, 1));
    }

    /**
     * Set king's left and right castling tiles
     */
    public void markCastlingTile(PieceColor color) {
        // set castling tiles
        int side = PieceColor.getFirstLine(color);
        // left
        this.leftCastlingTiles.add(gamePanel.getTile(side, 1));
        this.leftCastlingTiles.add(gamePanel.getTile(side, 2));
        this.leftCastlingTiles.add(gamePanel.getTile(side, 3));
        // right
        this.rightCastlingTiles.add(gamePanel.getTile(side, 5));
        this.rightCastlingTiles.add(gamePanel.getTile(side, 6));
    }

    /**
     * Check if left castling is valid
     */
    public void checkLeftCastling() {
        boolean canCastling = true;
        if ((this.getColor().getLeftRook(this.getColor()).getCanCast())) {
            for (Tile tile : this.getLeftCastlingTiles()) {
                if (tile.getTilePiece() != null || this.getThreatByColor() ||
                        !(checkColorThreatTile(this.getLeftCastlingTiles(), this.getColor())) || preventAdjacentKingsRightCastling() ||
                        !(this.getColor().getLeftRook(this.getColor()).getIsAlive())) {
                    canCastling = false;
                }
            }
            if (canCastling) {
                this.leftCastlingTile.setCastlingTile(true);
            }
        }
    }

    /**
     * Check if right castling is valid
     */
    public void checkRightCastling() {
        boolean canCastling = true;
        if ((this.getColor().getRightRook(this.getColor()).getCanCast())) {
            for (Tile tile : this.getRightCastlingTiles()) {
                if ((tile.getTilePiece() != null) || this.getThreatByColor() ||
                        !(checkColorThreatTile(this.getRightCastlingTiles(), this.getColor())) || preventAdjacentKingsLeftCastling() ||
                        !(this.getColor().getRightRook(this.getColor()).getIsAlive())) {
                    canCastling = false;
                }
            }
            if (canCastling) {
                this.rightCastlingTile.setCastlingTile(true);
            }
        }
    }

    /**
     * Check if castling is possible
     */
    public void checkCastling() {
        if (this.getCanCast() && !this.getCheck()) {
            checkRightCastling();
            checkLeftCastling();
        } else {
            this.leftCastlingTile.setCastlingTile(false);
            this.rightCastlingTile.setCastlingTile(false);
        }
    }

    /**
     * Check if tile has threat color
     */
    public boolean checkColorThreatTile(ArrayList<Tile> tiles, PieceColor color) {
        if (color.equals(PieceColor.WHITE)) {
            for (Tile tile : tiles) {
                if (tile.getBlackThreat()) {
                    return false;
                }
            }
        } else {
            for (Tile tile : tiles) {
                if (tile.getWhiteThreat()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Calculates king moves
     */
    public void kingMoves(Tile tile) {
        if (tile != null) {
            if (gamePanel.removeDuplicateTile(kingPossibleTile, tile)) {
                kingPossibleTile.add(tile);
            }
        }
    }

    /**
     * Calculates opposite king moves
     */
    public void oppositeKingMove(Tile tile) {
        if (tile != null) {
            if (gamePanel.removeDuplicateTile(oppositeKingPossibleTile, tile)) {
                oppositeKingPossibleTile.add(tile);
            }
        }
    }

    /**
     * Remove adjacent kings from king possible moves
     */
    public void removeAdjacentKingsMoves(Tile tile, ArrayList<Tile> tilesToRemove) {
        for (Tile oppositeKingMove : oppositeKingPossibleTile) {
            if (tile.equals(oppositeKingMove)) {
                if (gamePanel.removeDuplicateTile(tilesToRemove, tile)) {
                    tilesToRemove.add(tile);
                }
            }
        }
    }

    /**
     * Remove occupied tile that contain same pieces color as king from its possible moves
     */
    public void removeOccupiedTile(Tile tile, ArrayList<Tile> tilesToRemove, PieceColor color) {
        if (tile.getTilePiece() != null) {
            PieceColor pieceColor = tile.getTilePiece().getColor();
            if (color.equals(pieceColor)) {
                if (gamePanel.removeDuplicateTile(tilesToRemove, tile)) {
                    tilesToRemove.add(tile);
                }
            }
        }
    }

    /**
     * Remove threaten tiles from king possible moves
     */
    public void removeThreatenTiles(Tile tile, ArrayList<Tile> threatenTiles, ArrayList<Tile> tilesToRemove) {
        for (Tile threatenTile : threatenTiles) {
            if (tile.equals(threatenTile)) {
                if (gamePanel.removeDuplicateTile(tilesToRemove, tile)) {
                    tilesToRemove.add(tile);
                }
            }
        }
    }

    /**
     * Add castling tile to king possible moves
     */
    public void addCastlingTiles() {
        this.checkCastling();
        if (this.rightCastlingTile.getCastlingTile()) {
            if (gamePanel.removeDuplicateTile(kingPossibleTile, this.rightCastlingTile)) {
                kingPossibleTile.add(this.rightCastlingTile);
            }
        }
        if (this.leftCastlingTile.getCastlingTile()) {
            if (gamePanel.removeDuplicateTile(kingPossibleTile, this.leftCastlingTile)) {
                kingPossibleTile.add(this.leftCastlingTile);
            }
        }
    }

    /**
     * Reset king possible tile arraylist
     */
    public void resetKingPossibleTiles() {
        for (Tile tile : kingPossibleTile) {
            tile.setAvailableMove(false);
            tile.setAvailableAttack(false);
            tile.setKingMove(false);
            tile.setCastlingTile(false);
        }
    }

    /**
     * Reset all the tiles of remove arraylist
     */
    public void resetRemoveTiles(ArrayList<Tile> tilesToRemove) {
        for (Tile tileToRemove : tilesToRemove) {
            tileToRemove.setAvailableMove(false);
            tileToRemove.setAvailableAttack(false);
            tileToRemove.setKingMove(false);
            tileToRemove.setCastlingTile(false);
        }
    }

    /**
     * Set tile as approved as castling tiles
     */
    public void approveCastlingTiles(Tile tile) {
        if (this.canCast) {
            if (tile.equals(this.rightCastlingTile)) {
                if (this.getColor().getRightRook(this.getColor()).getCanCast()) {
                    tile.setCastlingTile(true);
                }
            }
            if (tile.equals(this.leftCastlingTile)) {
                if (this.getColor().getLeftRook(this.getColor()).getCanCast()) {
                    tile.setCastlingTile(true);
                }
            }
        }
    }

    /**
     * Set king possible tiles as possible moves / attack
     */
    public void setMoveAndAttackMoves(PieceColor color) {
        for (Tile tile : kingPossibleTile) {
            tile.setKingMove(true);
            approveCastlingTiles(tile);
            if (tile.getTilePiece() != null) {
                if (tile.getTilePiece().getColor() != color) {
                    tile.setAvailableAttack(true);
                }
            } else {
                tile.setAvailableMove(true);
            }
        }
    }

    /**
     * Calculates both kings all moves, legal and illegal
     */
    public void calcKingsAllMoves(PieceColor color) {
        // get opposite king location
        King oppositeKing = (King) color.getOppositeKing();
        int xPosOpposite, yPosOpposite;
        if (oppositeKing != null) {
            xPosOpposite = oppositeKing.getRow();
            yPosOpposite = oppositeKing.getCol();
        } else {
            throw new RuntimeException(oppositeKing.getColor() + "hasn't found!");
        }
        // king 9 nearby tiles
        xPosKing = new int[]{xPos + 1, xPos + 1, xPos + 1, xPos - 1, xPos - 1, xPos - 1, xPos, xPos};
        yPosKing = new int[]{yPos - 1, yPos, yPos + 1, yPos - 1, yPos, yPos + 1, yPos + 1, yPos - 1};

        // opposite king 9 nearby tiles
        xPosOppositeKing = new int[]{xPosOpposite + 1, xPosOpposite + 1, xPosOpposite + 1, xPosOpposite - 1, xPosOpposite - 1, xPosOpposite - 1, xPosOpposite, xPosOpposite};
        yPosOppositeKing = new int[]{yPosOpposite - 1, yPosOpposite, yPosOpposite + 1, yPosOpposite - 1, yPosOpposite, yPosOpposite + 1, yPosOpposite + 1, yPosOpposite - 1};

    }

    public void updateKingsAvailableMoves(int[] xPosKing, int[] yPosKing, int[] xPosOppositeKing, int[] yPosOppositeKing) {
        int row, col, oppositeRow, oppositeCol;
        for (int i = 0; i < 8; i++) {
            // king moves
            row = xPosKing[i];
            col = yPosKing[i];
            Tile tile = gamePanel.getTile(row, col);
            kingMoves(tile);

            // opposite king moves
            oppositeRow = xPosOppositeKing[i];
            oppositeCol = yPosOppositeKing[i];
            tile = gamePanel.getTile(oppositeRow, oppositeCol);
            oppositeKingMove(tile);
        }
    }

    /**
     * Calculates king legal moves
     */
    public void calcKingMove() {
        gamePanel.checksIdentifier(gamePanel.getWhite());
        gamePanel.checksIdentifier(gamePanel.getBlack());
        xPos = this.getRow();
        yPos = this.getCol();
        PieceColor color = this.getColor();
        ArrayList<Tile> tilesToRemove = new ArrayList<>();
        this.kingPossibleTile = new ArrayList<>();
        this.oppositeKingPossibleTile = new ArrayList<>();

        calcKingsAllMoves(color);
        // get king color threaten tiles
        gamePanel.setThreatenPosition(); // make 2 total threats arrays
        ArrayList<Tile> threatenTiles = color.getThreatTiles();

        // update available move for each king
        updateKingsAvailableMoves(xPosKing, yPosKing, xPosOppositeKing, yPosOppositeKing);

        // remove illegal moves
        for (Tile tile : kingPossibleTile) {
            removeAdjacentKingsMoves(tile, tilesToRemove);
            removeOccupiedTile(tile, tilesToRemove, color);
            removeThreatenTiles(tile, threatenTiles, tilesToRemove);
        }
        addCastlingTiles();
        resetKingPossibleTiles();
        resetRemoveTiles(tilesToRemove);
        kingPossibleTile.removeAll(tilesToRemove);
        setMoveAndAttackMoves(color);
        for (Tile tile : kingPossibleTile) {
            if (gamePanel.removeDuplicateTile(this.pieceAvailableTiles, tile)) {
                this.pieceAvailableTiles.add(tile);
            }
        }
        this.pieceMovesCalculation.clear();
        this.pieceMovesCalculation.addAll(this.pieceAvailableTiles);
    }

    /**
     * Gets the tiles involved in castling on the left side.
     */
    public ArrayList<Tile> getLeftCastlingTiles() {
        return leftCastlingTiles;
    }

    /**
     * Gets the tiles involved in castling on the right side.
     */
    public ArrayList<Tile> getRightCastlingTiles() {
        return rightCastlingTiles;
    }

    /**
     * Sets the castling tiles for the piece.
     */
    public void setCastlingTiles() {
        rightCastlingTile = gamePanel.getTile(PieceColor.getFirstLine(this.getColor()), 6);
        leftCastlingTile = gamePanel.getTile(PieceColor.getFirstLine(this.getColor()), 2);
    }

    /**
     * Gets the tile involved in castling on the right side.
     */
    public Tile getRightCastlingTile() {
        return rightCastlingTile;
    }

    /**
     * Gets the tile involved in castling on the left side.
     */
    public Tile getLeftCastlingTile() {
        return leftCastlingTile;
    }

    /**
     * Removes the castling status from the castling tiles.
     */
    public void removeCastlingTiles() {
        this.rightCastlingTile.setCastlingTile(false);
        this.leftCastlingTile.setCastlingTile(false);
    }

    /**
     * Get tile threat color by given color
     */
    public boolean getThreatByColor() {
        if (this.getColor().equals(PieceColor.WHITE)) {
            return this.getPieceTile().getBlackThreat();
        }
        return this.getPieceTile().getWhiteThreat();
    }

    /**
     * Sets whether the piece can perform castling.
     */
    public void setCanCast(boolean canCast) {
        this.canCast = canCast;
    }

    /**
     * Checks if the piece can perform castling.
     */
    public boolean getCanCast() {
        return this.canCast;
    }

    /**
     * Set if king is in castling process
     */
    public void setIsCastling(boolean isCastling) {
        this.isCastling = isCastling;
    }

    /**
     * Get if king is in castling process
     */
    public boolean getIsCastling(){
        return isCastling;
    }
}

