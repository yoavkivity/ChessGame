package GameWindow;

import Pieces.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

public class MouseInputs extends MouseAdapter {

    // Class static attributes:
    private static final int SQUARE_SIDE = GamePanel.getSquareSide();
    private static final List<Character> lettersCoordinates = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');
    private static final List<Character> numberCoordinates = List.of('8', '7', '6', '5', '4', '3', '2', '1');
    private static final char check = '+';
    private static final char checkmate = '#';

    private final GamePanel gamePanel;
    private Tile selectedTile = null;
    private Tile pickedTile = null;
    private Piece pickedPiece = null;
    private Piece selectedPiece;
    private Piece promotionCopy = null;
    private Piece printPieceStats = null;
    private int selectedSquareCol;
    private int selectedSquareRow;
    private String pieceRecord;
    private String coordinatesRecord;
    private boolean recordCastling = false;

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        selectedSquareCol = e.getX() / SQUARE_SIDE;
        selectedSquareRow = e.getY() / SQUARE_SIDE;

        if (selectedTile != null && selectedTile.getTilePiece() != null) {
            selectedPiece = selectedTile.getTilePiece();
        }
        pickedTile = gamePanel.getTile(selectedSquareRow, selectedSquareCol);
        assert pickedTile != null;
        pickedPiece = pickedTile.getTilePiece();
        if (!calcMoveValidation()) return;

        if (selectedTile == null && pickedPiece != null) {
            selectPiece();
        } else if (selectedTile != null && selectedTile.equals(pickedTile)) {
            removePieceSelection();
        } else if (pickedPiece != null && selectedPiece.getColor().equals(pickedPiece.getColor())) {
            selectOtherPieceInSet();
        } else if (selectedTile != null && selectedPiece != null && pickedPiece == null && (pickedTile.getAvailableMove())) {
            movePiece();
            enPassantAfterMove();
        } else if ((selectedTile != null && pickedTile.getTilePiece() == null && pickedTile.getAvailableAttack())) {
            enPassantViaAttack();
        } else if (selectedTile != null && pickedPiece != null && !(selectedPiece.getColor().equals(pickedPiece.getColor()))) {
            attackPiece();
            checkPromotion();
            selectedTile = null;
        }
        createMoveRecord();
        gamePanel.repaint();
    }

    /**
     * Reset attributes after promotion
     */
    public void resetAttributesAfterPromotion() {
        gamePanel.resetThreatenPosition();
        gamePanel.resetAvailableMovesHighlight();
        gamePanel.nextTurn();
        pickedTile = null;
        selectedTile = null;
        pickedPiece = null;
        promotionCopy = null;
    }

    /**
     * Updates current move's data in the record
     */
    public void updatePromotionRecord(Piece promotedPiece) {
        setPieceRecord(getPieceRecord() + "=" + promotedPiece.getPieceDenote());
        setCoordinatesRecord(calcCoordinatesRecord(promotedPiece.getCol(), promotedPiece.getRow()));
        recordChessMoves();
    }

    /**
     * Calculates picked piece available moves
     */
    public void selectPiece() {
        pickedPiece.resetPiecesAvailableMoves();
        gamePanel.resetAvailableMovesHighlight(); // remove highlight older move
        selectedTile = pickedTile;
        selectedTile.setSelected(true);
        pickedPiece.resetMovesPossibilities(); // reset boolean move calculation values
        pickedPiece.calcPossibleMoves(true); // calc moves and color the tiles in board
        gamePanel.removeIllegalCheckMoves(pickedPiece); // CHECK MOVES REMOVER
        if (gamePanel.getTile(selectedSquareRow, selectedSquareCol) != null && gamePanel.getTile(selectedSquareRow, selectedSquareCol).getTilePiece() != null) {
            printPieceStats = gamePanel.getTile(selectedSquareRow, selectedSquareCol).getTilePiece();
        }
    }

    /**
     * Unhighlight selected piece available moves
     */
    public void removePieceSelection() {
        gamePanel.resetAvailableMovesHighlight();
        gamePanel.resetThreatenPosition();
        selectedTile.setSelected(false);
        selectedTile = null;
        printPieceStats = null;
    }

    /**
     * Remove highlight from first piece available moves and highlight second piece in same set available moves
     */
    public void selectOtherPieceInSet() {
        gamePanel.resetAvailableMovesHighlight();
        gamePanel.resetThreatenPosition();
        selectedTile.setSelected(false);
        selectedTile = pickedTile;
        selectedTile.setSelected(true);
        pickedPiece.resetMovesPossibilities(); // reset boolean move calculation values
        pickedPiece.calcPossibleMoves(true); // calc moves and color the tiles in board
        gamePanel.removeIllegalCheckMoves(pickedPiece);
        if (gamePanel.getTile(selectedSquareRow, selectedSquareCol) != null && gamePanel.getTile(selectedSquareRow, selectedSquareCol).getTilePiece() != null) {
            printPieceStats = gamePanel.getTile(selectedSquareRow, selectedSquareCol).getTilePiece();
        }
    }

    /**
     * Move piece to chosen tile
     */
    public void movePiece() {
        if (selectedPiece.move(selectedSquareRow, selectedSquareCol)) {
            selectedTile.setSelected(false);
            selectedPiece.calcPossibleMoves(true);
            selectedTile.setTilePiece(null);
            selectedPiece.resetMovesPossibilities();
            selectedTile = null;
            gamePanel.resetAvailableMovesHighlight();
            gamePanel.resetThreatenPosition();
            gamePanel.nextTurn();
        }
    }

    /**
     * Checks if there is an enPassant after pawn moved
     */
    public void enPassantAfterMove() {
        if ((selectedPiece instanceof Pawn && selectedPiece.getRow() == PieceColor.getPawnLastRow(selectedPiece.getColor())) && !gamePanel.getPromotion()) {
            try {
                gamePanel.setPromotionPawn(selectedPiece);
                ((Pawn) selectedPiece).promotion(selectedPiece);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Checks id there is an enPassant after pawn attacked
     */
    public void enPassantViaAttack() {
        if (selectedPiece instanceof Pawn) {
            ((Pawn) selectedPiece).enPassantAttack(selectedSquareRow, selectedSquareCol);
            selectedTile.setSelected(false); // attack picked piece
            pickedPiece = selectedPiece;
            pickedTile.setTilePiece(pickedPiece);
            gamePanel.resetThreatenPosition();
            selectedTile.setSelected(false);
            selectedTile = null;
            gamePanel.resetAvailableMovesHighlight();
            gamePanel.nextTurn();
        }
    }

    /**
     * Attack the chosen piece
     */
    public void attackPiece() {
        if (selectedPiece.attack(pickedPiece)) {
            if (pickedTile.getTilePiece() instanceof Pawn) {
                gamePanel.setPromotionPawn(pickedTile.getTilePiece());
            }
            promotionCopy = pickedPiece;
            selectedTile.setSelected(false); // attack picked piece
            pickedPiece = selectedPiece;
            pickedTile.setTilePiece(pickedPiece);
            gamePanel.resetThreatenPosition();
            selectedTile.setSelected(false);
            gamePanel.resetAvailableMovesHighlight();
            gamePanel.nextTurn();
        }
    }

    /**
     * Check if pawn in promotion process
     */
    public void checkPromotion() {
        if (pickedPiece instanceof Pawn) {
            if (gamePanel.getPromotionPawn() instanceof Pawn &&
                    promotionCopy.getRow() == PieceColor.getPawnLastRow(promotionCopy.getColor().getOppositeColor())
                    && !gamePanel.getPromotion()) {
                try {
                    ((Pawn) pickedPiece).promotion(promotionCopy);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                promotionCopy = null;
            }
        }
    }

    /**
     * Creates current move record
     */
    public void createMoveRecord() {
        if (!gamePanel.getPromotion()) {
            if (printPieceStats != null) {
                pieceRecord = pieceRecord();
                printPieceStats = null;
            } else if (!recordCastling) {
                coordinatesRecord = calcCoordinatesRecord();
            }
            if (gamePanel.getIsNextTurn()) {
                recordChessMoves();
            }
            if (recordCastling) {
                recordCastling = false;
            }
        }
    }

    /**
     * Check if click was made properly by chess rules
     */
    public boolean calcMoveValidation() {
        return pickedPiece == null || // no piece picked
                // same color piece picked
                gamePanel.getCurrentColor().equals(pickedPiece.getColor()) ||
                // move & attack
                pickedPiece != null && !(gamePanel.getCurrentColor().equals(pickedPiece.getColor().getOppositeColor())) ||
                selectedPiece != null && !(selectedPiece.getColor().equals(pickedPiece.getColor())) && pickedPiece.getPieceTile().getAvailableAttack();
    }

    /**
     * Get piece move records
     */
    public String pieceRecord() {
        return printPieceStats.getPieceDenote();
    }

    /**
     * Calculates current picked piece coordinates move's record
     */
    public String calcCoordinatesRecord() {
        return "_" + lettersCoordinates.get(selectedSquareCol) + numberCoordinates.get(selectedSquareRow);
    }

    /**
     * Calculates given col and row positions move's record
     */
    public String calcCoordinatesRecord(int col, int row) {
        return "_" + lettersCoordinates.get(col) + numberCoordinates.get(row);
    }

    /**
     * Saves the full move's record in player moves record
     */
    public void recordChessMoves() {
        gamePanel.getCurrentColor().getCurrentPlayer().getPlayerMovesRecord().add(pieceRecord + coordinatesRecord);
    }


    // Getters & Setters:
    /**
     * Get selected tile
     */
    public Tile getSelectedTile() {
        return selectedTile;
    }

    /**
     * Set pieces move record
     */
    public void setPieceRecord(String pieceRecord) {
        this.pieceRecord = pieceRecord;
    }

    /**
     * Set coordinates move record
     */
    public void setCoordinatesRecord(String coordinatesRecord) {
        this.coordinatesRecord = coordinatesRecord;
    }

    /**
     * Set special records for castling
     */
    public void setRecordCastling(boolean recordCastling) {
        this.recordCastling = recordCastling;
    }

    /**
     * Set piece's move's record
     */
    public String getPieceRecord() {
        return pieceRecord;
    }


    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
