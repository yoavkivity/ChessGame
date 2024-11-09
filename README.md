_**Chess Game**_

_Chess Game is a local multiplayer chess game developed from scratch in Java._

_The game follows the official chess rules and provides a smooth, interactive experience for two players._


![chess](https://github.com/user-attachments/assets/8791b0c6-8aae-43f4-9278-e2d879327acb)



**_Features:_**

**Local multiplayer:** Play between two players on the same computer.

**Full rule implementation:** All chess rules, including castling and en passant, are fully supported.

**Piece movement:** The game highlights valid moves for the selected piece.

**Legal move checks:** The game checks for valid moves, check, and checkmate scenarios.

**User-friendly interface:** The game displays a clean and intuitive interface for playing chess.


![enpassant](https://github.com/user-attachments/assets/925f4108-4993-4e04-bf76-3d1da837aa88)


**_Key classes:_**

**runGame:** The entry point of the game. It initializes the players, the chessboard, and starts the game loop.

**SplitImage:** Handles the splitting of the piece images. It takes a single image of all the pieces and splits it into smaller images for each individual piece.

**Piece:** An abstract class representing a chess piece. This class defines basic functionality common to all pieces (like movement rules and position).

**PieceColor:** Manages operations related to the color of the pieces (black or white).

**GamePanel:** Responsible for the main game loop and for checking legal moves, check, and checkmate conditions.

**MouseInputs:** Handles mouse input, calculating the tile and piece where the player clicks.

**Tile: **Manages the 64 squares on the chessboard. It holds information about the piece in each square and highlights potential moves (yellow for possible moves, red for capture moves).



_**Clone the repository:**_

_git clone https://github.com/yoavkivity/ChessGame.git_

_cd ChessGame_

_dir /s /b *.java > files.txt_

_javac -d . @files.txt_

_java runGame.java_

