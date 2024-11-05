import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;




public class SnakeGame extends JPanel implements ActionListener,KeyListener {
    int gameSpeed = 750;


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
    ArrayList<Tile> snakeBody;


    Tile snakeFood;

    Random rand;

    Timer gameTimer;

    int velocityx; //tile count
    int velocityy;



    SnakeGame(int boardWidth,int boardHeight){ //snake game  constructor
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.BLUE);
        addKeyListener(this);
        setFocusable(true);


        //snake stuff
        snakeHead = new Tile(5,5);
        snakeBody = new ArrayList<Tile>();
        snakeFood = new Tile(10,10);
        velocityx = 1;
        velocityy = 0; // permitted val -1,0,1


        //food stuff
        rand = new Random();

        putFood();
        gameTimer = new Timer(gameSpeed,this); //implement every time food eaten , dec timer by 50ms
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

        //snakebody
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakeBodyTile = snakeBody.get(i);
            g.setColor(Color.green);
            g.fillRect(snakeBodyTile.x*tileSize, snakeBodyTile.y*tileSize, tileSize, tileSize);


        }


    }

    public void putFood(){
        snakeFood.x = rand.nextInt(boardWidth/tileSize); //randomly assign food position
        snakeFood.y = rand.nextInt(boardHeight/tileSize);
    }
    public boolean eatFood(Tile tile1 , Tile tile2){

        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move(){

        if (eatFood(snakeFood, snakeHead)){
            snakeBody.add(new Tile(snakeFood.x,snakeFood.y));

            putFood();
            if (gameSpeed > 50) { // Minimum delay limit
                gameSpeed -= 100;
                gameTimer.setDelay(gameSpeed); // Apply new delay to the timer
            }
        }

        //snakebody  movement
        for (int i = snakeBody.size()-1; i>=0 ; i--) {
            Tile snakeBodyTile = snakeBody.get(i);
            if(i == 0){
                snakeBodyTile.x = snakeHead.x;
                snakeBodyTile.y = snakeHead.y;
            }else {
                Tile snakeBodyTileprev = snakeBody.get(i-1);
                snakeBodyTile.x = snakeBodyTileprev.x;
                snakeBodyTile.y = snakeBodyTileprev.y;
            }

        }

        //snake head
        snakeHead.x += velocityx; //increments movement
        snakeHead.y += velocityy;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); //updates on display
    }

    @Override
    public void keyPressed(KeyEvent e) { //need optimisation

        if(e.getKeyCode() == KeyEvent.VK_LEFT && velocityx != 1){

            velocityx = -1;
            velocityy = 0;
        } if(e.getKeyCode() == KeyEvent.VK_RIGHT && velocityx != -1){
            velocityx = 1;
            velocityy = 0;
        }if(e.getKeyCode() == KeyEvent.VK_UP && velocityy != 1){
            velocityy = -1;
            velocityx = 0;
        } if(e.getKeyCode() == KeyEvent.VK_DOWN && velocityy != -1){
            velocityy = 1;
            velocityx = 0;
        }

    }


    //no use but don't remove else it dies
    @Override
    public void keyReleased(KeyEvent e) {

    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
}
