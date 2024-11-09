package PiecesImages;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SplitImage {
    private SplitImage() {
    }

    // path to save the pieces images
    private static final String IMAGES_PATH = "..\\Chess\\PiecesImages\\";

    // pieces name array
    private static final String[] PIECES_NAMES = {"White_King", "White_Queen", "White_Bishop", "White_Knight", "White_Rook", "White_Pawn"
            , "Black_King", "Black_Queen", "Black_Bishop", "Black_Knight", "Black_Rook", "Black_Pawn"};

    /**
     * Split all pieces images to individual images
     */
    public static void createPiecesImages() throws IOException {
        System.out.println(IMAGES_PATH + "chess.png");
        BufferedImage source = ImageIO.read(new File(IMAGES_PATH + "chess.png")); // read all pieces picture
        int imageWidth = source.getWidth(); // get picture's width
        int imageHeight = source.getHeight(); // get picture's height

        int index = 0;
        for (int x = 0; x < imageHeight; x = x + 200) {
            for (int y = 0; y < imageWidth; y = y + 200) {
                ImageIO.write(source.getSubimage(y, x, 200, 200), "png", // splits the image the black and white pieces images, and save them in directory
                        new File(IMAGES_PATH + PIECES_NAMES[index++] + ".png")); //
            }
        }
    }

    /**
     * Checks if pieces images exists
     */
    public static boolean checkImagePathsExist() {
        for (String name : PIECES_NAMES) {
            File directory = new File(IMAGES_PATH);
            File file = new File(directory, name + ".png");
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    /**
     * initialize pieces image files
     */
    public static void initializePiecesImages() throws IOException {
        if (!checkImagePathsExist()) {
            createPiecesImages();
        }
    }
}