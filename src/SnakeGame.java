import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;




public class SnakeGame extends JPanel implements ActionListener {


    private class Tile { //for tiling or dividing screen in tiles

        int x;
        int y;
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }


    int boardHeight;
    int boardWidth;
    int tileSize = 25;
    Tile snakeHead;
    Tile snakeFood;

    Random rand;

    Timer gameTimer;

    int velocityx; //tile count
    int velocityy;



    SnakeGame(int boardWidth,int boardHeight){ //snake game width
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.BLUE);
        snakeHead = new Tile(5,5);
        snakeFood = new Tile(10,10);
        velocityx = 0;
        velocityy = 0; // permitted val -1,0,1
        rand = new Random();

        putFood();
        gameTimer = new Timer(1000,this); //implement every time food eaten , dec timer by 50ms
        gameTimer.start();


    }

    public void paint (Graphics g){
        super.paint(g);
        draw(g);
    }
    public void draw(Graphics g){


        //food
        g.setColor(Color.red);
        g.fillRect(snakeFood.x*tileSize, snakeFood.y*tileSize, tileSize,tileSize);

        //snake
        g.setColor(Color.WHITE);
        g.fillRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize,tileSize);


    }

    public void putFood(){
        snakeFood.x = rand.nextInt(boardWidth/tileSize); //randomly assign food position
        snakeFood.y = rand.nextInt(boardHeight/tileSize);
    }
    public void move(){
        snakeHead.x += velocityx; //increments tile count
        snakeHead.y += velocityy;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); //updates on display
    }
}
