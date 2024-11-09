package GameWindow;

import Pieces.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.io.BufferedWriter;


public class GamePanel extends JPanel {

    // Class static attributes:
    private static final int SQUARE_SIDE = 75;
//    private static final String IMAGES_PATH = "../Chess/PiecesImages";
    private static final String IMAGES_PATH = Paths.get("").toAbsolutePath().resolve("PiecesImages") + File.separator;
    private static final Color DARK_TILE_COLOR = new Color(118, 150, 86);
    private static final Color BRIGHT_TILE_COLOR = new Color(238, 238, 210);
    private static final Color PICKED_PIECE_COLOR = new Color(255, 229, 110);
    private static final Color HIGHLIGHT_MOVE_COLOR = PICKED_PIECE_COLOR.brighter();
    private static final Color HIGHLIGHT_ATTACK_COLOR = new Color(236, 124, 124);


    // Game logic
    private boolean runGame = true;
    private boolean isNextTurn = false;
    private int turnsNumber = 0;
    private ArrayList<String> gameMoves = new ArrayList<>();

    // gamePanel setting:
    private final MouseInputs mouseInputs;
    private final ArrayList<Tile> boardTiles = new ArrayList<>();
    private Graphics graph;

    // Pawn promotion:
    private Piece promotionPawn = null;
    private boolean promotion = false;

    // White Player settings:
    private final Player whitePlayer;
    private final PieceColor white;
    private boolean whiteCheck;
    private ArrayList<Piece> whitePlayerPieces;
    private final ArrayList<Tile> whiteThreatenTiles = new ArrayList<>();
    private ArrayList<Piece> whitePieces;

    // Black Player settings
    private final Player blackPlayer;
    private final PieceColor black;
    private boolean blackCheck;
    private ArrayList<Piece> blackPlayerPieces;
    private final ArrayList<Tile> blackThreatenTiles = new ArrayList<>();
    private ArrayList<Piece> blackPieces;

    // Initialize game:

    /**
     * Create GamePanel object (game screen)
     */
    public GamePanel(PieceColor white, PieceColor black, Player whitePlayer, Player blackPlayer) {
        this.white = white;
        this.black = black;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.mouseInputs = new MouseInputs(this);
        this.setPreferredSize(new Dimension(GameFrame.FRAME_WIDTH, GameFrame.FRAME_HEIGHT));
        this.addMouseListener(mouseInputs);
        this.addMouseMotionListener(mouseInputs);
    }

    /**
     * Initialize the board tiles
     */
    public void createBoardTiles() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = new Tile(row, col, this);
                this.boardTiles.add(tile);
            }
        }
    }

    /**
     * Set players pieces
     */
    public void setPlayersPieces(ArrayList<Piece> white, ArrayList<Piece> black) {
        whitePlayerPieces = white;
        blackPlayerPieces = black;
    }

    /**
     * Update turn number and prepare for next turn
     */
    public void prepareNextTurn() {
        setIsNextTurn(false);
        updateTurnNumber();
    }

    /**
     * Check if there is a win or draw
     */
    public void determentDrawOrWin(int totalPlayerMoves) {
        if (!runGame) return;

        if (isCheckmate(totalPlayerMoves)) {
            String gameMessage = getCurrentColor() + " is the winner!";
            System.out.println(gameMessage);
            runGame = false; // end game by checkmate

        } else if (isDraw(totalPlayerMoves)) {
            String gameMessage = "That's a Draw!";
            System.out.println(gameMessage);
            runGame = false; // end game by draw
        }
    }

    /**
     * Updates players pieces set
     */
    public void updatePlayersPiecesSet() {
        whitePieces = white.getPlayerPieces();
        blackPieces = black.getPlayerPieces();
    }



    // Game logic:

    @Override
    public void paintComponent(Graphics graph) {
        if (!runGame){
            exportMovesToFile();
            return;
        }
        if (!promotion) {
            updatePlayersPiecesSet();
            resetPlayersPiecesMoves();
            passGraphObject(graph);
            updatePlayersChecks();
            paintTiles();
            drawPieceInPosition(whitePieces, blackPieces);
            promotionPawn = null;

            if (isNextTurn) { // if piece moved or attacked
                resetThreatenPosition();
                printTurnStats();
                determentDrawOrWin(totalPlayerMoves());
                getCurrentColor().setTwoStepPawnFalse(); // update pawn location for En Passant
                resetPiecesMoves();
                prepareNextTurn();
            }

        } else if (promotionPawn != null) { // promote pawn
            Piece promotedPiece = initiatePromotion();
            resetPromotionValues();
            mouseInputs.updatePromotionRecord(promotedPiece);
            paintComponent(graph);
        }
    }

    /**
     * Saves game's moves in ttx file
     */
    public void exportMovesToFile() {
        String filePath = "../Chess/gameMoves.txt";
        try {
            Files.createDirectories(Paths.get("../Chess"));
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String move : gameMoves) {
                writer.write(move);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while exporting moves: " + e.getMessage());
        }
    }


    /**
     * Set next turn into true value in order to let other player play
     */
    public void nextTurn() {
        isNextTurn = true;
    }

    /**
     * Update turn number
     */
    public void updateTurnNumber() {
        turnsNumber++;
    }

    /**
     * Checks for checkmate on board
     */
    public boolean isCheckmate(int totalMoves) {
        PieceColor color = getCurrentColor().getOppositeColor();
        return (totalMoves == 0 && color.getPlayerCheck());
    }

    /**
     * Check for draw
     */
    public boolean isDraw(int totalMoves) {
        PieceColor color = getCurrentColor().getOppositeColor();
        return (totalMoves == 0 && !color.getPlayerCheck());
    }

    /**
     * Counts player's available moves number
     */
    public int totalPlayerMoves(ArrayList<Piece> pieces) {
        int totalMovesCounter = 0;
        for (Piece piece : pieces) {
            resetAvailableMovesHighlight(); // remove highlight older move
            piece.resetMovesPossibilities(); // reset boolean move calculation values
            piece.calcPossibleMoves(false); // calc moves and color the tiles in board
            removeIllegalCheckMoves(piece); // CHECK MOVES REMOVER
            totalMovesCounter += piece.pieceMovesCalculation.size();
        }
        return totalMovesCounter;
    }

    /**
     * calculates player total moves
     */
    public int totalPlayerMoves() {
        PieceColor color = getCurrentColor().getOppositeColor();
        int totalMoves = totalPlayerMoves(color.getPlayerPieces());
        resetThreatenPosition();
        return totalMoves;
    }

    /**
     * Return if player doing checks
     */
    public boolean checksIdentifier(PieceColor color) {
        PieceColor oppositeColor = color.getOppositeColor();
        resetPieceChecks(oppositeColor.getPlayerPieces());
        int checks = checkCounter(oppositeColor.getPlayerPieces());
        setThreatenPosition();
        return checks > 0;
    }

    /**
     * Count the amount of checks player doing
     */
    public int checkCounter(ArrayList<Piece> pieces) {
        int checkCount = 0;
        for (Piece piece : pieces) {
            piece.setCheck(false);
            if (!(piece instanceof King)) {
                piece.resetMovesPossibilities();
                piece.calcPossibleMoves(false);
                if (piece.getCheck()) {
                    checkCount++;
                }
            }
            updateTotalPlayerMoves(piece);
        }
        return checkCount;
    }

    /**
     * update players total moves
     */
    public void updateTotalPlayerMoves(Piece piece) {
        for (Tile tile : piece.pieceMovesCalculation) {
            if (removeDuplicateTile(piece.getColor().getTotalPlayerTiles(), tile)) {
                piece.getColor().getTotalPlayerTiles().add(tile);
            }
        }
    }

    /**
     * remove illegal moves that exposing/not blocking checks
     */
    public void removeIllegalCheckMoves(Piece pickedPiece) {
        ArrayList<Tile> pieceMovesCopy = new ArrayList<>(pickedPiece.pieceMovesCalculation);
        int originalRow = pickedPiece.getRow();
        int originalCol = pickedPiece.getCol();
        pickedPiece.getPieceTile().setTilePiece(null);
        calcLegalMoves(pieceMovesCopy, pickedPiece);
        setPieceOriginalPosition(pickedPiece, originalRow, originalCol, pieceMovesCopy);
    }

    /**
     * Calculates piece legal moves
     */
    public void calcLegalMoves(ArrayList<Tile> pieceMovesCopy, Piece pickedPiece) {
        Piece checkBlockPiece = null;
        for (Tile tile : pieceMovesCopy) {
            Tile realTile = tile.findTileInList(boardTiles);
            if (realTile == null) return;
            int row = realTile.getRow();
            int col = realTile.getCol();
            Piece piece = getTile(row, col).getTilePiece();
            Tile pickedTile = getTile(row, col);

            if (piece != null && !(piece.getColor()).equals(pickedPiece.getColor())) {
                checkBlockPiece = piece;
                pickedTile.setTilePiece(null);
                checkBlockPiece.setAlive(false);
                getPlayerByColor(piece.getColor()).removePieceFromSet(checkBlockPiece);
            }

            pickedPiece.setRow(row);
            pickedPiece.setCol(col);
            pickedPiece.getPieceTile().setTilePiece(pickedPiece);

            boolean isCheck = checksIdentifier(pickedPiece.getColor());
            illegalMoveDefined(isCheck, pickedPiece, realTile);
            restoreCheckBlockPiece(checkBlockPiece, pickedTile);
            pickedPiece.getPieceTile().setTilePiece(null);
        }
    }

    /**
     * Remove illegal move from piece's available move arraylist
     */
    public void illegalMoveDefined(boolean isCheck, Piece pickedPiece, Tile realTile) {
        if (isCheck) {
            if (removeDuplicateTile(pickedPiece.tilesToRemoveCalculation, realTile)) {
                pickedPiece.tilesToRemoveCalculation.add(realTile);
                realTile.setAvailableMove(false);
                realTile.setAvailableAttack(false);
            }
        }
    }

    /**
     * Restore piece back to the tile and player's set
     */
    public void restoreCheckBlockPiece(Piece checkBlockPiece, Tile pickedTile) {
        if (checkBlockPiece == null) return;
        if (removeDuplicatePiece(checkBlockPiece.getColor().getPlayerPieces(), checkBlockPiece)) {
            getPlayerByColor(checkBlockPiece.getColor()).addPieceToSet(checkBlockPiece);
        }
        checkBlockPiece.setAlive(true);
        pickedTile.setTilePiece(checkBlockPiece);
    }

    /**
     * Set and Passes Graph object to relevant function
     */
    public void passGraphObject(Graphics graph) {
        this.graph = graph; // set graph
        super.paintComponent(graph); // pass Graphic obj to Jcomponent to prevent image glitching
        for (Tile tile : boardTiles) {
            tile.setGraph(graph);
        }
    }


    // Tile & Piece functions:

    /**
     * Set each piece in its tile
     */
    public void setPieceInTile(ArrayList<Piece> pieces) {
        for (Piece piece : pieces) {
            setTilePiece(piece);
        }
    }

    /**
     * Set threat color for each tile
     */
    public void setThreatenPosition() {
        for (Tile tile : boardTiles) {
            if (tile.getWhiteThreat()) {
                if (removeDuplicateTile(whiteThreatenTiles, tile)) {
                    whiteThreatenTiles.add(tile);
                }
            }
            if (tile.getBlackThreat()) {
                if (removeDuplicateTile(blackThreatenTiles, tile)) {
                    blackThreatenTiles.add(tile);
                }
            }
        }
    }

    /**
     * Prevent from adding duplicate tiles to given array
     */
    public boolean removeDuplicateTile(ArrayList<Tile> threatenTiles, Tile tile) {
        for (Tile threatTile : threatenTiles) {
            if (threatTile.equals(tile)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prevent from adding duplicate pieces to given array
     */
    public boolean removeDuplicatePiece(ArrayList<Piece> pieces, Piece piece) {
        for (Piece pieceInSet : pieces) {
            if (pieceInSet.equals(piece)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Set piece in its tile
     */
    public void setTilePiece(Piece piece) {
        int row = piece.getRow();
        int col = piece.getCol();
        int index = row * 8 + col;
        this.boardTiles.get(index).setTilePiece(piece);
    }

    /**
     * Print current turn number and move played
     */
    public void printTurnStats() {
        String turnInfo ="Turn Number " + (this.turnsNumber + 1) + ":  " + getMoveRecord();
        System.out.println(turnInfo);
        this.gameMoves.add(turnInfo);
    }

    /**
     * Resets every available move for every piece on board
     */
    public void resetPlayersPiecesMoves() {
        white.resetPiecesAvailableMoves();
        black.resetPiecesAvailableMoves();
        whitePlayer.resetTotalMove();
        blackPlayer.resetTotalMove();
    }

    /**
     * Updates players check situation
     */
    public void updatePlayersChecks() {
        whiteCheck = checksIdentifier(PieceColor.WHITE);
        blackCheck = checksIdentifier(PieceColor.BLACK);
    }

    /**
     * Restore the original pickedPiece position
     */
    public void setPieceOriginalPosition(Piece pickedPiece, int originalRow, int originalCol, ArrayList<Tile> pieceMovesCopy) {
        pickedPiece.setRow(originalRow);
        pickedPiece.setCol(originalCol);
        pickedPiece.getPieceTile().setTilePiece(pickedPiece);
        pieceMovesCopy.removeAll(pickedPiece.tilesToRemoveCalculation);
        setPieces(pickedPiece.getColor().getPlayerPieces());
        pickedPiece.pieceMovesCalculation.clear();
        pickedPiece.pieceMovesCalculation.addAll(pieceMovesCopy);
    }

    /**
     * Reset check for every piece
     */
    public void resetPieceChecks(ArrayList<Piece> pieces) {
        for (Piece piece : pieces) {
            if (piece.getCheck()) {
                piece.setCheck(false);
            }
        }
    }

    /**
     * Reset pieces available move tiles arraylists moves
     */
    public void resetPiecesMoves() {
        for (Piece piece : whitePlayerPieces) {
            piece.pieceMovesCalculation.clear();
            piece.tilesToRemoveCalculation.clear();
            piece.pieceAvailableTiles.clear();
        }
        for (Piece piece : blackPlayerPieces) {
            piece.pieceMovesCalculation.clear();
            piece.tilesToRemoveCalculation.clear();
            piece.pieceAvailableTiles.clear();
        }
    }

    /**
     * Resets white and black threats for each tile
     */
    public void resetThreatenPosition() {
        for (Tile tile : boardTiles) {
            tile.setWhiteThreat(false);
            tile.setBlackThreat(false);
        }
        whiteThreatenTiles.clear();
        blackThreatenTiles.clear();
    }

    /**
     * Reset tiles available moves
     */
    public void resetAvailableMovesHighlight() {
        for (Tile tile : boardTiles) {
            if (tile.getAvailableMove()) {
                tile.setAvailableMove(false);
            }
            if (tile.getAvailableAttack()) {
                tile.setAvailableAttack(false);
            }
            if (tile.getKingMove()) {
                tile.setKingMove(false);
            }
            if (tile.getCastlingTile()) {
                tile.setCastlingTile(false);
            }
        }
    }

    /**
     * Reset promotion attributes for next play
     */
    public void resetPromotionAttributes() {
        setPromotion(false);
        promotionPawn = null;
        resetAvailableMovesHighlight();
    }

    /**
     * Returns current player turn color
     */
    public PieceColor getCurrentColor() {
        if (getTurnNumber() % 2 == 0) {
            return white;
        }
        return black;
    }

    /**
     * Return picked tile from game board tiles
     */
    public Tile getTile(int row, int col) {
        for (Tile tile : getBoardTiles()) {
            if ((tile.getRow() == row) && (tile.getCol() == col)) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Set piece object in its owen tile
     */
    public void setPieces(ArrayList<Piece> pieces) {
        for (Piece piece : pieces) {
            int row = piece.getRow();
            int col = piece.getCol();
            getTile(row, col).setTilePiece(piece);
        }
    }


    // Promotion:

    /**
     * Scan from user wanted piece
     */
    public String scanPromotionPickedPiece() {
        String name = "";
        Scanner scan = new Scanner(System.in);
        boolean toggle = true;
        System.out.println("Enter wanted piece name: ");
        while (toggle) {
            String piece = scan.nextLine();
            if (piece.equalsIgnoreCase("queen") || piece.equalsIgnoreCase("rook") || piece.equalsIgnoreCase("knight") || piece.equalsIgnoreCase("bishop")) {
                toggle = false;
                name = piece;
            } else {
                System.out.println("Enter valid piece name (queen, rook, bishop, knight): ");
            }
        }
        return name;
    }

    /**
     * Initialize the promotion pawn to the picked piece
     */
    public Piece initializePromotedPiece(String name, int row, int col, PieceColor color, GamePanel gamePanel) {
        name = name.toLowerCase();
        return switch (name) {
            case "queen" -> new Queen(true, row, col, color, gamePanel);
            case "rook" -> new Rook(true, row, col, color, gamePanel);
            case "bishop" -> new Bishop(true, row, col, color, gamePanel);
            case "knight" -> new Knight(true, row, col, color, gamePanel);
            default -> throw new IllegalArgumentException("Invalid piece name: " + name);
        };
    }

    /**
     * Reset promotion attributes after promoting
     */
    public void resetPromotionValues() {
        setPromotion(false);
        promotionPawn.removePieceFromSet(promotionPawn);
        mouseInputs.resetAttributesAfterPromotion();
        resetPromotionAttributes();
    }

    /**
     * Start pawn promotion process
     */
    public Piece initiatePromotion() {
        int row = promotionPawn.getRow();
        int col = promotionPawn.getCol();
        PieceColor color = promotionPawn.getColor();
        String name = scanPromotionPickedPiece();
        Piece promotedPiece = initializePromotedPiece(name, row, col, color, this);
        getTile(row, col).setTilePiece(promotedPiece);
        promotedPiece.addPieceToSet(promotedPiece);
        return promotedPiece;
    }


    // Draw function:

    /**
     * Draw white and black pieces on board
     */
    public void drawPieceInPosition(ArrayList<Piece> whitePlayerPieces, ArrayList<Piece> blackPlayerPieces) {
        try {
            createPiecesSet(whitePlayerPieces, graph);
            createPiecesSet(blackPlayerPieces, graph);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to draw game pieces.");
        }
    }

    /**
     * Paint each tile by its role
     */
    public void paintTiles() {
        for (Tile tile : boardTiles) {
            if ((tile.getRow() + tile.getCol()) % 2 == 0) {
                tile.setTileColor(BRIGHT_TILE_COLOR);
                tile.getGraph().setColor(BRIGHT_TILE_COLOR);
                tile.getGraph().fillRect(tile.getCol() * SQUARE_SIDE, tile.getRow() * SQUARE_SIDE, SQUARE_SIDE, SQUARE_SIDE);
            } else {
                tile.setTileColor(DARK_TILE_COLOR);
                tile.getGraph().setColor(DARK_TILE_COLOR);
                tile.getGraph().fillRect(tile.getCol() * SQUARE_SIDE, tile.getRow() * SQUARE_SIDE, SQUARE_SIDE, SQUARE_SIDE);
            }
            if (tile.getSelected()) {
                tile.setTileColor(PICKED_PIECE_COLOR);
                tile.getGraph().setColor(PICKED_PIECE_COLOR);
                tile.getGraph().fillRect(tile.getCol() * SQUARE_SIDE, tile.getRow() * SQUARE_SIDE, SQUARE_SIDE, SQUARE_SIDE);
            }
            if (tile.getAvailableMove()) {
                tile.setTileColor(HIGHLIGHT_MOVE_COLOR);
                tile.getGraph().setColor(HIGHLIGHT_MOVE_COLOR);
                tile.getGraph().fillRect(tile.getCol() * SQUARE_SIDE, tile.getRow() * SQUARE_SIDE, SQUARE_SIDE, SQUARE_SIDE);
            }
            if (tile.getAvailableAttack()) {
                tile.setTileColor(HIGHLIGHT_ATTACK_COLOR);
                tile.getGraph().setColor(HIGHLIGHT_ATTACK_COLOR);
                tile.getGraph().fillRect(tile.getCol() * SQUARE_SIDE, tile.getRow() * SQUARE_SIDE, SQUARE_SIDE, SQUARE_SIDE);
            }
            if (tile.getCastlingTile()) {
                tile.setTileColor(HIGHLIGHT_MOVE_COLOR);
                tile.getGraph().setColor(HIGHLIGHT_MOVE_COLOR);
                tile.getGraph().fillRect(tile.getCol() * SQUARE_SIDE, tile.getRow() * SQUARE_SIDE, SQUARE_SIDE, SQUARE_SIDE);
            }
        }
    }

    /**
     * Arrange and drawing pieces on board
     */
    public void createPiecesSet(ArrayList<Piece> pieces, Graphics graph) throws IOException {
        Iterator<Piece> iterator = pieces.iterator();
        while (iterator.hasNext()) {
            Piece piece = iterator.next();
            if (piece.getIsAlive()) {
                String color = piece.getColor().toString().substring(0, 1).toUpperCase() + piece.getColor().toString().substring(1).toLowerCase();
                String pieceName = piece.getPieceName();
                drawPiece(piece, IMAGES_PATH + color + "_" + pieceName + ".png", graph);
            } else {
                iterator.remove(); // Remove the piece from the ArrayList using the iterator
            }
        }
    }

    /**
     * Draw given piece on board in its tile
     */
    public void drawPiece(Piece piece, String path, Graphics graph) throws IOException { // draw the pieces on the chess board
        Image img = ImageIO.read(new File(path));
        graph.drawImage(img, SQUARE_SIDE * piece.getCol(), SQUARE_SIDE * piece.getRow(), SQUARE_SIDE, SQUARE_SIDE, this); // draw piece
       // piece.setPieceImage(img); // set the matched image to piece
    }


    // Getter and Setter:

    /**
     * Return game's turn number
     */
    public int getTurnNumber() {
        return turnsNumber;
    }

    /**
     * Returns white player pieces
     */
    public ArrayList<Piece> getWhitePlayerPieces() {
        return whitePlayerPieces;
    }

    /**
     * Returns black player pieces
     */
    public ArrayList<Piece> getBlackPlayerPieces() {
        return blackPlayerPieces;
    }

    /**
     * Returns game player object by given color
     */
    public Player getPlayerByColor(PieceColor color) {
        if (color.equals(PieceColor.WHITE)) {
            return getWhitePlayer();
        }
        return getBlackPlayer();
    }

    /**
     * Returns white threaten tile arraylist
     */
    public ArrayList<Tile> getWhiteThreatenTiles() {
        return whiteThreatenTiles;
    }

    /**
     * Returns black threaten tile arraylist
     */
    public ArrayList<Tile> getBlackThreatenTiles() {
        return blackThreatenTiles;
    }

    /**
     * Returns white player acting a check
     */
    public boolean getWhiteCheck() {
        return whiteCheck;
    }

    /**
     * Returns black player acting a check
     */
    public boolean getBlackCheck() {
        return blackCheck;
    }

    /**
     * Returns move record
     */
    public String getMoveRecord() {
        int lastRecord = getCurrentColor().getCurrentPlayer().getPlayerMovesRecord().size() - 1;
        return getCurrentColor().getCurrentPlayer().getPlayerMovesRecord().get(lastRecord);
    }

    /**
     * Returns boolean value of if player finished its move
     */
    public Boolean getIsNextTurn() {
        return isNextTurn;
    }

    /**
     * Set boolean value that shows if player finished its turn
     */
    public void setIsNextTurn(boolean nextTurn) {
        isNextTurn = nextTurn;
    }

    /**
     * Return square size
     */
    public static int getSquareSide() {
        return SQUARE_SIDE;
    }

    /**
     * Returns game board tiles arraylist
     */
    public ArrayList<Tile> getBoardTiles() {
        return boardTiles;
    }

    /**
     * Return game gamePanel object
     */
    public GamePanel getGamePanel() {
        return this;
    }

    /**
     * Returns white PieceColor object
     */
    public PieceColor getWhite() {
        return white;
    }

    /**
     * Returns black PieceColor object
     */
    public PieceColor getBlack() {
        return black;
    }

    /**
     * Returns white player object
     */
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    /**
     * Returns black player object
     */
    public Player getBlackPlayer() {
        return blackPlayer;
    }

    /**
     * Returns gamePanel's mouseInput object
     */
    public MouseInputs getMouseInputs() {
        return mouseInputs;
    }

    /**
     * Returns promotion pawn
     */
    public Piece getPromotionPawn() {
        return promotionPawn;
    }

    /**
     * Set pawn as promotion pawn
     */
    public void setPromotionPawn(Piece promotionPawn) {
        this.promotionPawn = promotionPawn;
    }

    /**
     * Set gamePanel promotion process value
     */
    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    /**
     * Get if promotion is in process
     */
    public boolean getPromotion() {
        return promotion;
    }

    /**
     * Get king type piece by players color
     */
    public King getKingByColor(PieceColor color) {
        if (color.equals(PieceColor.WHITE)) {
            return getWhitePlayer().getWhiteKingType();
        }
        return getBlackPlayer().getBlackKingType();
    }

    /**
     * Get player's pieces set by given color
     */
    public ArrayList<Piece> getPlayerPieces(PieceColor color){
        if (color.equals(PieceColor.WHITE)){
            return getWhitePlayerPieces();
        }
        return getBlackPlayerPieces();
    }
}