package Pieces;

import GameWindow.GamePanel;
import GameWindow.Tile;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Piece {

    // Piece details:
    private final String pieceName;
    private final PieceColor color;
    private int row;
    private int col;
    private boolean alive;
    public String pieceDenote;
    protected final GamePanel gamePanel;

    // Piece available tiles
    private boolean check = false;
    public final ArrayList<Tile> pieceMovesCalculation = new ArrayList<>();
    public final ArrayList<Tile> tilesToRemoveCalculation = new ArrayList<>();
    public final ArrayList<Tile> pieceAvailableTiles = new ArrayList<>();
    public final ArrayList<Tile> illegalMovesToRemove = new ArrayList<>();

    // Straight calculations toggles:
    private boolean checkUpperSquare = true;
    private boolean checkDownSquare = true;
    private boolean checkLeftSquare = true;
    private boolean checkRightSquare = true;

    private boolean upToggle = false;
    private boolean downToggle = false;
    private boolean leftToggle = false;
    private boolean rightToggle = false;

    private boolean upLeftToggle = false;
    private boolean upRightToggle = false;
    private boolean downLeftToggle = false;
    private boolean downRightToggle = false;

    // Diagonal calculations toggles:
    private boolean checkDownRightSquare = true;
    private boolean checkUpperRightSquare = true;
    private boolean checkDownLeftSquare = true;
    private boolean checkUpperLeftSquare = true;


    // Constructor
    public Piece(boolean alive, String pieceName, int row, int col, PieceColor color, GamePanel gamePanel) {
        this.alive = alive;
        this.pieceName = pieceName;
        this.row = row;
        this.col = col;
        this.color = color;
        this.gamePanel = gamePanel;
    }

    // Piece moves function:
    /**
     * Moves the piece to the ask position
     */
    public boolean move(int row, int col) {
        this.getPieceTile().setTilePiece(null);
        Tile tile = gamePanel.getTile(row, col);
        if ((tile != null) && (tile.getAvailableMove())) {
            if (this instanceof Knight) {
                ((Knight) this).saveKnightPosition();
            }
            if (this instanceof Pawn) {
                this.getColor().isPawnMadeTwoSteps(this, row);
            }
            this.setRow(row);
            this.setCol(col);
            tile.setTilePiece(this);
            return true;
        }
        return false;
    }

    /**
     * Attack piece with picked piece
     */
    public boolean attack(Piece attacked) {
        this.getPieceTile().setTilePiece(null);
        Tile attackedTile = gamePanel.getTile(attacked.getRow(), attacked.getCol());
        if ((attackedTile != null) && (attackedTile.getAvailableAttack())) {
            if (!(attacked instanceof King)) {
                attacked.setAlive(false);
                attackedTile.setTilePiece(null);
                removePieceFromSet(attacked);
                this.setRow(attacked.getRow());
                this.setCol(attacked.getCol());
                attackedTile.setTilePiece(this);

                return true;
            }
        }
        return false;
    }

    /**
     * Calculates piece possible moves at current moment
     */
    public abstract void calcPossibleMoves(boolean draw);

    // Moves calculation functions:

    /**
     * Calculates piece's straight moves
     */
    protected void calcStraightMoves(int row, int col, int index, PieceColor pieceColor, boolean draw) {
        gamePanel.setPieces(gamePanel.getWhite().getPlayerPieces()); // set logical white pieces in tile
        gamePanel.setPieces(gamePanel.getBlack().getPlayerPieces()); // set logical black pieces in tile
        Piece piece = gamePanel.getTile(row, col).getTilePiece();
        boolean isPawn = piece instanceof Pawn;
        Piece threatenPiece = gamePanel.getTile(row, col).getTilePiece();


        // backward
        Piece pieceOriginalPosition = Objects.requireNonNull(gamePanel.getTile(row, col)).getTilePiece();
        int right = col - index;
        Tile rightTile = gamePanel.getTile(row, right);
        if (rightTile != null && piece != null) {
            calcRight(right, index, draw, isPawn, rightTile, pieceColor, piece, pieceOriginalPosition, threatenPiece);
        }

        // forward
        int left = col + index;
        Tile leftTile = gamePanel.getTile(row, left);
        if (leftTile != null && piece != null) {
            calcLeft(left, index, draw, isPawn, leftTile, pieceColor, piece,  pieceOriginalPosition);
        }
        // forward
        int forward = row - index;
        Tile forwardTile = gamePanel.getTile(forward, col);
        if (forwardTile != null && piece != null) {
            calcForward(forward, index, draw, isPawn, forwardTile, pieceColor, piece, pieceOriginalPosition, threatenPiece);
        }

        // backward
        int backward = row + index;
        Tile backwardTile = gamePanel.getTile(backward, col);
        if (backwardTile != null && piece != null) {
            calcBackwards(backward, index, draw, isPawn, backwardTile, pieceColor, piece, pieceOriginalPosition, threatenPiece);
        }
    }

    /**
     * Calculates piece possible moves to move right
     */
    public void calcRight(int right, int index, boolean draw, boolean isPawn, Tile rightTile, PieceColor pieceColor, Piece piece, Piece pieceOriginalPosition, Piece threatenPiece) {
        if ((right < 8) && (right >= 0) && checkUpperSquare) {
            if (rightTile.getTilePiece() == null) {
                if (index > 0) {
                    rightTile.setThreatColor(pieceColor, pieceOriginalPosition);
                }
                if (draw) {
                    rightTile.setAvailableMove(true);
                    if (upToggle) {
                        piece.addIllegalMove(rightTile);
                    }
                }
            } else if (rightTile.getTilePiece().getColor() != pieceColor) {
                if (index > 0) {
                    rightTile.setThreatColor(pieceColor, pieceOriginalPosition);
                    checkUpperSquare = false;
                }
                if (draw && !isPawn) {
                    rightTile.setAvailableAttack(true);
                    if (upToggle) {
                        piece.addIllegalMove(rightTile);
                    }
                } else if (rightTile.getTilePiece() != null && (rightTile.getTilePiece() instanceof King)) {
                    if (threatenPiece != null) {
                        threatenPiece.setCheck(true);
                        checkUpperSquare = true;
                        upToggle = true;
                    }
                }
            } else if (index > 0) {
                rightTile.setThreatColor(pieceColor, null);
                checkUpperSquare = false;
            }
        }
    }

    /**
     * Calculates piece possible moves to move left
     */
    public void calcLeft(int left, int index, boolean draw, boolean isPawn, Tile leftTile, PieceColor pieceColor, Piece piece , Piece pieceOriginalPosition) {
        if ((left < 8) && (left >= 0) && checkDownSquare) {
            if (leftTile.getTilePiece() == null) {
                if (index > 0) {
                    leftTile.setThreatColor(pieceColor, pieceOriginalPosition);
                }
                if (draw) {
                    leftTile.setAvailableMove(true);
                    if (downToggle) {
                        piece.addIllegalMove(leftTile);
                    }
                }
            } else if (leftTile.getTilePiece().getColor() != pieceColor) {
                if (index > 0) {
                    leftTile.setThreatColor(pieceColor, pieceOriginalPosition);
                    checkDownSquare = false;
                }
                if (draw) {
                    if (index > 0 && !isPawn) {
                        leftTile.setAvailableAttack(true);
                        if (downToggle) {
                            piece.addIllegalMove(leftTile);
                        }
                    }
                } else if (leftTile.getTilePiece() != null && (leftTile.getTilePiece() instanceof King)) {
                    if (gamePanel.getTile(row, col).getTilePiece() != null) {
                        gamePanel.getTile(row, col).getTilePiece().setCheck(true);
                        checkDownSquare = true;
                        downToggle = true;
                    }
                }
            } else if (index > 0) {
                leftTile.setThreatColor(pieceColor, null);
                checkDownSquare = false;
            }
        }
    }

    /**
     * Calculates piece possible moves to move forward
     */
    public void calcForward(int forward, int index, boolean draw, boolean isPawn, Tile forwardTile, PieceColor pieceColor, Piece piece, Piece pieceOriginalPosition, Piece threatenPiece) {
        if ((forward < 8) && (forward >= 0) && checkLeftSquare) {
            if (forwardTile.getTilePiece() == null) {
                if (index > 0){
                    if (isPawn){
                        ((Pawn) piece).addPawnAvailableMove(forwardTile);
                    }else{
                        forwardTile.setThreatColor(pieceColor, pieceOriginalPosition);
                    }
                }

                if (draw) {
                    forwardTile.setAvailableMove(true);
                    if (leftToggle) {
                        piece.addIllegalMove(forwardTile);
                    }
                }
            } else if (forwardTile.getTilePiece().getColor() != pieceColor) {
                if (index > 0 && !isPawn) {
                    forwardTile.setThreatColor(pieceColor, pieceOriginalPosition);
                    checkLeftSquare = false;
                }
                if (draw) {
                    if (index > 0 && !isPawn) {
                        forwardTile.setAvailableAttack(true);
                        if (leftToggle) {
                            piece.addIllegalMove(forwardTile);
                        }
                    }
                } else if (forwardTile.getTilePiece() != null && (forwardTile.getTilePiece() instanceof King)) {
                    if (threatenPiece != null && !isPawn) {
                        threatenPiece.setCheck(true);
                        checkLeftSquare = true;
                        leftToggle = true;
                    }
                }
            } else if (index > 0 && !isPawn) {
                forwardTile.setThreatColor(pieceColor, null);
                checkLeftSquare = false;
            }
        }
    }

    /**
     * Calculates piece possible moves to move backwards
     */
    public void calcBackwards(int backwards, int index, boolean draw, boolean isPawn, Tile backwardTile, PieceColor pieceColor, Piece piece, Piece pieceOriginalPosition, Piece threatenPiece) {
        if ((backwards < 8) && (backwards >= 0) && checkRightSquare) {
            if (backwardTile.getTilePiece() == null) {
                if (index > 0){
                    if (isPawn){
                        ((Pawn) piece).addPawnAvailableMove(backwardTile);
                    }else{
                        backwardTile.setThreatColor(pieceColor, pieceOriginalPosition);
                    }
                }
                if (draw) {
                    backwardTile.setAvailableMove(true);
                    if (rightToggle) {
                        piece.addIllegalMove(backwardTile);
                    }
                }
            } else if (backwardTile.getTilePiece().getColor() != pieceColor) {
                if (index > 0  && !isPawn) {
                    backwardTile.setThreatColor(pieceColor, pieceOriginalPosition);
                    checkRightSquare = false;
                }
                if (draw && !isPawn) {
                    backwardTile.setAvailableAttack(true);
                    if (rightToggle) {
                        piece.addIllegalMove(backwardTile);
                    }
                } else if (backwardTile.getTilePiece() != null && (backwardTile.getTilePiece() instanceof King)) {
                    if (threatenPiece != null && !isPawn) {
                        threatenPiece.setCheck(true);
                        checkRightSquare = true;
                        rightToggle = true;
                    }
                }
            } else if (index > 0 && !isPawn) {
                backwardTile.setThreatColor(pieceColor, null);
                checkRightSquare = false;
            }
        }
    }

    /**
     * Calculates piece possible moves to move in down right diagonal direction
     */
    public void calcDownRight(int rowForward, int colForward, int index, boolean draw, boolean isPawn, Tile downRightTile, PieceColor pieceColor, Piece piece) {
        if (checkDownRightSquare && (colForward < 8) && (rowForward < 8)) {
            if (downRightTile.getTilePiece() == null) {
                downRightTile.setThreatColor(pieceColor, piece);
                if (draw && !isPawn) {
                    downRightTile.setAvailableMove(true);
                    if (downRightToggle) {
                        piece.addIllegalMove(downRightTile);
                    }
                }
            } else if (downRightTile.getTilePiece().getColor() != pieceColor) {
                if (index > 0) {
                    downRightTile.setThreatColor(pieceColor, piece);
                    checkDownRightSquare = false;
                }
                downRightTile.setThreatColor(pieceColor, piece);
                if (draw) {
                    downRightTile.setAvailableAttack(true);
                    if (downRightToggle) {
                        piece.addIllegalMove(downRightTile);
                    }
                } else if (downRightTile.getTilePiece() != null &&
                        (downRightTile.getTilePiece() instanceof King)) {
                    if (piece != null) {
                        piece.setCheck(true);
                        checkDownRightSquare = true;
                        downRightToggle = true;
                    }
                }
            } else if (index > 0) {
                downRightTile.setThreatColor(pieceColor, null);
                checkDownRightSquare = false;
            }
        }
    }

    /**
     * Calculates piece possible moves to move in down right diagonal direction
     */
    public void calcUpRight(int colForward, int colBackwards, int index, boolean draw, boolean isPawn, Tile upRightTile, PieceColor pieceColor, Piece piece) {
        if (checkUpperRightSquare && (colForward < 8) && (colBackwards >= 0)) {
            if (upRightTile.getTilePiece() == null) {
                upRightTile.setThreatColor(pieceColor, piece);
                if (draw && !isPawn) {
                    upRightTile.setAvailableMove(true);
                    if (upRightToggle) {
                        piece.addIllegalMove(upRightTile);
                    }
                }
            } else if (upRightTile.getTilePiece().getColor() != pieceColor) {
                if (index > 0) {
                    upRightTile.setThreatColor(pieceColor, piece);

                    upRightTile.setThreatColor(pieceColor, piece);

                    checkUpperRightSquare = false;
                }
                upRightTile.setThreatColor(pieceColor, piece);
                if (draw) {
                    upRightTile.setAvailableAttack(true);
                    if (upRightToggle) {
                        piece.addIllegalMove(upRightTile);
                    }
                } else {
                    if (upRightTile.getTilePiece() != null &&
                            (upRightTile.getTilePiece() instanceof King)) {
                        if (piece != null) {
                            piece.setCheck(true);
                            checkUpperRightSquare = true;
                            upRightToggle = true;
                        }
                    }
                }
            } else {
                if (index > 0) {
                    upRightTile.setThreatColor(pieceColor, null);
                    checkUpperRightSquare = false;
                }
            }
        }
    }

    /**
     * Calculates piece possible moves to move in down left diagonal direction
     */
    public void calcDownLeft(int rowForward, int rowBackwards, int index, boolean draw, boolean isPawn, Tile downLeftTile, PieceColor pieceColor, Piece piece) {
        if (checkDownLeftSquare && (rowBackwards >= 0) && (rowForward < 8)) {
            if (downLeftTile.getTilePiece() == null) {
                downLeftTile.setThreatColor(pieceColor, piece);
                if (draw && !isPawn) {
                    downLeftTile.setAvailableMove(true);
                    if (downLeftToggle) {
                        piece.addIllegalMove(downLeftTile);
                    }
                }
            } else if (downLeftTile.getTilePiece().getColor() != pieceColor) {
                if (index > 0) {
                    downLeftTile.setThreatColor(pieceColor, piece);
                    checkDownLeftSquare = false;
                }
                downLeftTile.setThreatColor(pieceColor, piece);
                if (draw) {
                    downLeftTile.setAvailableAttack(true);
                    if (downLeftToggle) {
                        piece.addIllegalMove(downLeftTile);
                    }
                } else if (downLeftTile.getTilePiece() != null &&
                        (downLeftTile.getTilePiece() instanceof King)) {
                    if (piece != null) {
                        piece.setCheck(true);
                        checkDownLeftSquare = true;
                        downLeftToggle = true;
                    }
                }
            } else if (index > 0) {
                downLeftTile.setThreatColor(pieceColor, null); ////////////////////////
                checkDownLeftSquare = false;
            }
        }
    }

    /**
     * Calculates piece possible moves to move in up left diagonal direction
     */
    public void calcUpLeft(int rowBackwards, int colBackwards, int index, boolean draw, boolean isPawn, Tile upLeftTile, PieceColor pieceColor, Piece piece) {
        if (checkUpperLeftSquare && (rowBackwards >= 0) && (colBackwards >= 0)) {
            if (upLeftTile.getTilePiece() == null) {
                upLeftTile.setThreatColor(pieceColor, piece);
                if (draw && !isPawn) {
                    upLeftTile.setAvailableMove(true);
                    if (upLeftToggle) {
                        piece.addIllegalMove(upLeftTile);
                    }
                }
            } else if (upLeftTile.getTilePiece().getColor() != pieceColor) {
                if (index > 0) {
                    upLeftTile.setThreatColor(pieceColor, piece);
                    checkUpperLeftSquare = false;
                }
                if (draw) {
                    upLeftTile.setAvailableAttack(true);
                    if (upLeftToggle) {
                        piece.addIllegalMove(upLeftTile);
                    }
                } else if (upLeftTile.getTilePiece() != null &&
                        (upLeftTile.getTilePiece() instanceof King)) {
                    if (piece != null) {
                        piece.setCheck(true);
                        checkUpperLeftSquare = true;
                        upLeftToggle = true;
                    }
                }
            } else if (index > 0) {
                upLeftTile.setThreatColor(pieceColor, null);
                checkUpperLeftSquare = false;
            }
        }
    }

    /**
     * Calculates piece's diagonal moves
     */
    protected void calcDiagonalMoves(int row, int col, int index, PieceColor pieceColor, boolean draw) {
        gamePanel.setPieces(gamePanel.getWhite().getPlayerPieces()); // set logical white pieces in tile
        gamePanel.setPieces(gamePanel.getBlack().getPlayerPieces()); // set logical black pieces in tile;


        Piece piece = gamePanel.getTile(row, col).getTilePiece();
        int colForward = row + index;
        int rowForward = col + index;
        boolean isPawn = piece instanceof Pawn;
        Tile downRightTile = gamePanel.getTile(colForward, rowForward);
        if (downRightTile != null && piece != null) {
            calcDownRight(rowForward, col, index, draw, isPawn, downRightTile, pieceColor, piece);
        }

        // upper left diagonal
        int colBackwards = col - index;
        Tile upRightTile = gamePanel.getTile(colForward, colBackwards);
        if (upRightTile != null && piece != null) {
            calcUpRight(colForward, colBackwards, index, draw, isPawn, upRightTile, pieceColor, piece);
        }

        // bottom right
        int rowBackwards = row - index;
        Tile downLeftTile = gamePanel.getTile(rowBackwards, rowForward);
        if (downLeftTile != null && piece != null) {
            calcDownLeft(rowForward, rowBackwards, index, draw, isPawn, downLeftTile, pieceColor, piece);
        }

        // bottom left
        Tile upLeftTile = gamePanel.getTile(rowBackwards, colBackwards);
        if (upLeftTile != null && piece != null) {
            calcUpLeft(rowBackwards, colBackwards, index, draw, isPawn, upLeftTile, pieceColor, piece);
        }
    }

    /**
     * Reset piece's move toggles that keep checking next tile for available moves
     */
    public void resetMovesPossibilities() {
        checkUpperSquare = true;
        checkDownSquare = true;
        checkLeftSquare = true;
        checkRightSquare = true;

        // Upper
        checkUpperRightSquare = true;
        checkUpperLeftSquare = true;
        //Down
        checkDownRightSquare = true;
        checkDownLeftSquare = true;
    }

    /**
     * Resets each piece available moves tiles arraylists
     */
    public void resetPiecesAvailableMoves() {
        for (Piece piece : gamePanel.getWhitePlayerPieces()) {
            piece.pieceMovesCalculation.clear();
            piece.tilesToRemoveCalculation.clear();
        }
        for (Piece piece : gamePanel.getBlackPlayerPieces()) {
            piece.pieceMovesCalculation.clear();
            piece.tilesToRemoveCalculation.clear();
        }
    }

    /**
     * Remove piece from pieces set
     */
    public void removePieceFromSet(Piece removePiece) {
        for (Piece piece : gamePanel.getPlayerPieces(removePiece.getColor())) {
            if (piece.equals(removePiece)){
                gamePanel.getPlayerPieces(removePiece.getColor()).remove(removePiece);
                break;
            }
        }
    }

    /**
     * Add piece to player pieces set
     */
    public void addPieceToSet(Piece piece) {
        gamePanel.getPlayerPieces(piece.getColor()).add(piece);
    }

    /**
     * Add tile to illegal move arraylist
     */
    public void addIllegalMove(Tile tile) {
        if (gamePanel.removeDuplicateTile(this.illegalMovesToRemove, tile)) {
            this.illegalMovesToRemove.add(tile);
        }
    }


    // Getters & Setters:
    /**
     * Get piece col coordination
     */
    public int getCol() {
        return col;
    }

    /**
     * Get piece row coordination
     */
    public int getRow() {
        return row;
    }

    /**
     * Set piece col coordination
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Set piece row coordination
     */
    public void setRow(int rowCoordinates) {
        this.row = rowCoordinates;
    }

    /**
     * Get the tile that the piece sits in
     */
    public Tile getPieceTile() {
        return gamePanel.getTile(this.getRow(), this.getCol());
    }

    /**
     * Set the piece as making check
     */
    public void setCheck(boolean check) {
        this.check = check;
    }

    /**
     * Get if piece making check
     */
    public boolean getCheck() {
        return this.check;
    }

    /**
     * Set upper left square for keep checking for available moves
     */
    public void setCheckUpperLeftSquare(boolean checkUpperLeftSquare) {
        this.checkUpperLeftSquare = checkUpperLeftSquare;
    }

    /**
     * Set upper down left for keep checking for available moves
     */
    public void setCheckDownLeftSquare(boolean checkDownLeftSquare) {
        this.checkDownLeftSquare = checkDownLeftSquare;
    }

    /**
     * Set upper upper right for keep checking for available moves
     */
    public void setCheckUpperRightSquare(boolean checkUpperRightSquare) {
        this.checkUpperRightSquare = checkUpperRightSquare;
    }

    /**
     * Set down right square for keep checking for available moves
     */
    public void setCheckDownRightSquare(boolean checkDownRightSquare) {
        this.checkDownRightSquare = checkDownRightSquare;
    }

    /**
     * Set down square for keep checking for available moves
     */
    public void setCheckDownSquare(boolean checkDownSquare) {
        this.checkDownSquare = checkDownSquare;
    }

    /**
     * Set right square for keep checking for available moves
     */
    public void setCheckRightSquare(boolean checkRightSquare) {
        this.checkRightSquare = checkRightSquare;
    }

    /**
     * Set upper square for keep checking for available moves
     */
    public void setCheckUpperSquare(boolean checkUpperSquare) {
        this.checkUpperSquare = checkUpperSquare;
    }

    /**
     * Set left square for keep checking for available moves
     */
    public void setCheckLeftSquare(boolean checkLeftSquare) {
        this.checkLeftSquare = checkLeftSquare;
    }

    /**
     * Set if piece alive or not
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Get piece's color
     */
    public PieceColor getColor() {
        return color;
    }

    /**
     * Get if piece in alive
     */
    public boolean getIsAlive() {
        return alive;
    }

    /**
     * Get piece's name
     */
    public String getPieceName() {
        return pieceName;
    }

    /**
     * Get piece's denote (short piece name for notation)
     */
    public String getPieceDenote() {
        return this.pieceDenote;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Piece other) {
            return (this.alive == other.alive) && (Objects.equals(this.pieceName, other.pieceName)) &&
                    (this.row == other.row) && (this.col == other.col) && (this.color == other.color);
        }
        return false;
    }

//    /**
//     * Add tile to available moves tiles arraylist
//     */
//    public void addAvailableMove(Tile tile) {
//        if (gamePanel.removeDuplicateTile(this.pieceAvailableTiles, tile)) {
//            this.pieceAvailableTiles.add(tile);
//        }
//    }
//    /**
//     * Set piece's image
//     */
//    public void setPieceImage(Image pieceImage) {
//    }
//
//    /**
//     *
//     */
//    public void setPiecePath(Path piecePath) {
//    }
//
//    public int setTiles() {
//        this.pieceAvailableTiles.addAll(this.pieceMovesCalculation);
//        if (this instanceof Pawn) {
//            if (((Pawn) this).getEnPassantTile() != null) {
//                this.addAvailableMove(((Pawn) this).getEnPassantTile());
//            }
//        }
//        if (this instanceof King) {
//            if (((King) this).getLeftCastlingTile().getCastlingTile()) {
//                if (((King) this).getLeftCastlingTile() != null) {
//                    this.addAvailableMove(((King) this).getLeftCastlingTile());
//                }
//            }
//            if (((King) this).getRightCastlingTile().getCastlingTile()) {
//                if (((King) this).getRightCastlingTile() != null) {
//                    this.addAvailableMove(((King) this).getRightCastlingTile());
//                }
//            }
//        }
//        this.pieceAvailableTiles.removeAll(this.tilesToRemoveCalculation);
//        this.pieceAvailableTiles.removeAll(this.illegalMovesToRemove);
//        return this.pieceAvailableTiles.size();
//    }
}




