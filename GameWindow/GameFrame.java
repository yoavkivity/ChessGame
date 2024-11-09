package GameWindow;

import javax.swing.*;
import java.awt.*;

public class GameFrame {
    protected static final int FRAME_WIDTH = 600;
    protected static final int FRAME_HEIGHT = 600;
    private static JFrame frame = null;
    private static int gamesCounter = 1;

    public GameFrame(GamePanel gamePanel) {
        JFrame jframe = new JFrame("Game Number:" + gamesCounter);
        frame = jframe;
        jframe.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // set end program by clicking exit
        jframe.add(gamePanel);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
        Insets insets = jframe.getInsets();
        int leftBorder = insets.left;
        int rightBorder = insets.right;
        int topBorder = insets.top;
        int bottomBorder = insets.bottom;
        jframe.setBounds(0, 0, FRAME_WIDTH + leftBorder + rightBorder,FRAME_HEIGHT + topBorder + bottomBorder);
        gamesCounter++;
    }

    public static JFrame getFrame(String gameMessage){
        return frame;
    }

}