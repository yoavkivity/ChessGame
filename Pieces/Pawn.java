package Pieces;

import java.nio.file.Path;

public class Pawn extends Piece implements PiecesFunctions {
    private boolean isFirstMove;
    private static String pawn = "Pawn";
    private static final Path pawnPath = null;

    public Pawn(boolean alive, boolean isBlockingCheck, int rowCoordinates, int colCoordinates, PieceColor color, boolean isFirstMove) {
        super(alive, isBlockingCheck, pawnPath, pawn, rowCoordinates, colCoordinates, color);
        this.isFirstMove = isFirstMove;
    }

    // TODO create interface functions
    // TODO getters and setters
    // TODO fix all function
    // TODO draw function
    // TODO toString Function

    @Override
    public int[][] move() {
        return new int[0][];
    }

    public Piece Promotion(Pawn pawn, Promotion promotion){
        Piece piece = switch (promotion) {
            case QUEEN -> new Queen(true, false, pawn.getRowCoordinates(), pawn.getColCoordinates(), pawn.getColor());
            case ROOK -> new Rook(true, false, pawn.getRowCoordinates(), pawn.getColCoordinates(), pawn.getColor());
            case BISHOP -> new Bishop(true, false, pawn.getRowCoordinates(), pawn.getColCoordinates(), pawn.getColor());
            case KNIGHT -> new Knight(true, false, pawn.getRowCoordinates(), pawn.getColCoordinates(), pawn.getColor());
        };

        // TODO remove pawn!
        return piece;

    }

    @Override
    public int[][] attack() {
        return new int[0][];
    }

    @Override
    public void calcPossibleMoves() {

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pawn){
            Pawn other = (Pawn) obj;
            if (this.isFirstMove == other.isFirstMove) {
                return super.equals(obj);
            }
            return false;
        }
        return false;
    }
}
