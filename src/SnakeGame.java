import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;




public class SnakeGame extends JPanel {

    int boardHeight;
    int boardWidth;

    SnakeGame(int boardWidth,int boardHeight){ //snake game width
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.BLUE);


    }
}
