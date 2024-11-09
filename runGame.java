import GameWindow.GameFrame;
import GameWindow.GamePanel;
import GameWindow.Player;
import Pieces.Piece;
import Pieces.PieceColor;
import PiecesImages.SplitImage;

import java.io.IOException;
import java.util.ArrayList;

public class runGame {
    private final Player whitePlayer;
    private final Player blackPlayer;
    private final GamePanel gamePanel;

    public static void main(String[] args) throws IOException {
        runGame game = new runGame();
    }

    public runGame() throws IOException {
        PieceColor white = PieceColor.WHITE;
        PieceColor black = PieceColor.BLACK;
        this.whitePlayer = new Player(white); // create white player
        this.blackPlayer = new Player(black); // create black player
        this.gamePanel = new GamePanel(white, black, whitePlayer, blackPlayer);
        white.setWhitePlayer(whitePlayer);
        black.setBlackPlayer(blackPlayer);
        this.createChessGame();
        white.setGamePanel(gamePanel);
        black.setGamePanel(gamePanel);
        GameFrame gameFrame = new GameFrame(gamePanel);
        this.gamePanel.requestFocus();
    }


    /**
     * Initiate players and chess game
     */
    public void createChessGame() throws IOException {
        SplitImage.initializePiecesImages();
        ArrayList<Piece> whitePieces = whitePlayer.initializePieces(PieceColor.WHITE, gamePanel); // create white pieces set
        ArrayList<Piece> blackPieces = blackPlayer.initializePieces(PieceColor.BLACK, gamePanel); // create black pieces set
        gamePanel.createBoardTiles(); // create board tiles

        setCastlingTiles();
        gamePanel.setPieceInTile(whitePieces); // set white pieces in tiles
        gamePanel.setPieceInTile(blackPieces); // set black pieces in tiles
        gamePanel.setPlayersPieces(whitePieces, blackPieces); // set white & black pieces in GamePanel attributes
    }

    /**
     * Set white and black kings castling tiles
     */
    public void setCastlingTiles() {
        whitePlayer.getWhiteKingType().markCastlingTile(PieceColor.WHITE);
        whitePlayer.getWhiteKingType().setCastlingTiles();
        blackPlayer.getBlackKingType().markCastlingTile(PieceColor.BLACK);
        blackPlayer.getBlackKingType().setCastlingTiles();
    }

    /**
     * Returns gamePanel object
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }
}



