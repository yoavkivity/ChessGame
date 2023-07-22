package Pieces;

import java.awt.*;
import java.nio.file.Path;
import java.util.Objects;

public abstract class Piece {
    private boolean alive;              // check if used
    private boolean isBlockingCheck;    // check if used
    private Path WhitePiecePath;           // check if used
    private Path BlackPiecePath;
    private String pieceName;
    private int rowCoordinates;
    private int colCoordinates;
    private PieceColor color;
    private Image pieceImage;

    //TODO check if all the fields are needed

    public Piece(boolean alive, boolean isBlockingCheck, Path WhitePiecePath, String pieceName, int rowCoordinates, int colCoordinates, PieceColor color){
        this.alive = alive;
        this.isBlockingCheck = isBlockingCheck;
        this.WhitePiecePath = WhitePiecePath;
        this.pieceName = pieceName;
        this.rowCoordinates = rowCoordinates;
        this.colCoordinates = colCoordinates;
        this.color = color;
    }

    public int getColCoordinates() {
        return colCoordinates;
    }

    public int getRowCoordinates() {
        return rowCoordinates;
    }

    public PieceColor getColor() {
        return color;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isBlockingCheck() {
        return isBlockingCheck;
    }

    public String getPieceName() {
        return pieceName;
    }

    public void setPieceImage(Image pieceImage) {
        this.pieceImage = pieceImage;
    }



    public Image getPieceImage() {
        return pieceImage;
    }

    @Override
    public String toString() {
        return super.toString();
        // TODO create toString method
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Piece){
            Piece other = (Piece) obj;
            return (this.alive == other.alive) && (Objects.equals(this.pieceName, other.pieceName)) &&
                    (this.isBlockingCheck == other.isBlockingCheck) && (this.rowCoordinates == other.rowCoordinates) &&
                    (this.colCoordinates == other.colCoordinates) && (this.color == other.color);
        }
        return false;
    }
}
