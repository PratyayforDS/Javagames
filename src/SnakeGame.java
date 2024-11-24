import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    int gameSpeed = 750;
    int score = 0; // Variable to track the player's score

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

    // Game stuff
    Timer gameTimer;
    int velocityx; //tile count
    int velocityy;
    boolean gameOver = false;

    // Game Over screen
    private GameOverScreen gameOverScreen;

    // Clip for background music
    private Clip backgroundMusicClip;

    SnakeGame(int boardWidth, int boardHeight) { //snake game constructor
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLUE);
        addKeyListener(this);
        setFocusable(true);

        // Initialize snake and game elements
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        snakeFood = new Tile(10, 10);
        velocityx = 1;
        velocityy = 0; // permitted val -1,0,1

        // food stuff
        rand = new Random();
        putFood();

        gameTimer = new Timer(gameSpeed, this); //implement every time food eaten , dec timer by 50ms
        gameTimer.start();

        // Initialize GameOverScreen (but don't add it to the frame yet)
        gameOverScreen = new GameOverScreen(score);

        // Play background music
        playBackgroundMusic("src/assets/snakegamebgm.wav");
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (!gameOver) {
            draw(g);
        } else {
            showGameOver();
        }
    }

    public void draw(Graphics g) {
        // Draw food (apple)
        drawApple(g, snakeFood.x * tileSize, snakeFood.y * tileSize);

        // snake head
        g.setColor(Color.WHITE);
        g.fillOval(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        // snake body
        for (Tile snakeBodyTile : snakeBody) {
            g.setColor(Color.green);
            g.drawRoundRect(snakeBodyTile.x * tileSize, snakeBodyTile.y * tileSize, tileSize, tileSize, 10, 10);
        }

        // Display score in the top-left corner
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);
    }

    public void putFood() {
        // Avoid spawning food in the top-left corner (where the score is displayed)
        // Let's assume the score occupies a space of 2 rows (i.e., y < 2) and x < 5 columns
        int scoreAreaHeight = 2; // height of the score area in tiles
        int scoreAreaWidth = 5;  // width of the score area in tiles

        do {
            // Randomly assign food position, avoiding the top-left corner where the score is
            snakeFood.x = rand.nextInt(boardWidth / tileSize);
            snakeFood.y = rand.nextInt(boardHeight / tileSize);
            System.out.println("food put at " + snakeFood.x * tileSize + ", " + snakeFood.y * tileSize);
        } while (snakeFood.y < scoreAreaHeight && snakeFood.x < scoreAreaWidth); // Ensure food is not in the score area
    }

    public boolean collide(Tile tile1, Tile tile2) { //normally it checks for food eat , may check for collision to snake also
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void gameOvercollide() {
        for (Tile snakePart : snakeBody) { //game over condition
            if (collide(snakePart, snakeHead)) {
                gameOver = true;
            }
        }
    }

    public void move() {
        if (collide(snakeFood, snakeHead)) {
            snakeBody.add(new Tile(snakeFood.x, snakeFood.y));
            score += 10;

            putFood();
            if (gameSpeed > 150) { // Minimum delay limit
                gameSpeed -= 100;
                gameTimer.setDelay(gameSpeed); // Apply new delay to the timer
            }

            // Play sound when food is eaten
            playSound("src/assets/apple-crunch-215258_wqAxfUZg.wav"); // Replace with actual file path
        }

        // snake body movement
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakeBodyTile = snakeBody.get(i);
            if (i == 0) {
                snakeBodyTile.x = snakeHead.x;
                snakeBodyTile.y = snakeHead.y;
            } else {
                Tile prevTile = snakeBody.get(i - 1);
                snakeBodyTile.x = prevTile.x;
                snakeBodyTile.y = prevTile.y;
            }
        }

        // snake head
        snakeHead.x += velocityx; //increments movement
        snakeHead.y += velocityy;

        gameOvercollide();
        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize || snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
            gameOver = true;
        }
    }

    public void showGameOver() {
        gameOverScreen.setFinalScore(score); // Set final score
        gameOverScreen.setSize(getSize()); // Match the game panel's size
        add(gameOverScreen); // Add the GameOverScreen panel on top of the game screen
        gameOverScreen.repaint(); // Refresh to show the game-over screen

        // Stop background music when the game ends
        stopBackgroundMusic();

        // Play game over sound when the game ends
        //playSound("src/assets/game-over-arcade-6435.wav");
    }

    // Method to draw an apple instead of a frog
    private void drawApple(Graphics g, int x, int y) {
        // Draw the apple shape (red circle with a little stem)
        g.setColor(Color.RED);
        g.fillOval(x, y, tileSize, tileSize); // Apple body

        // Draw the stem (brown)
        g.setColor(new Color(139, 69, 19)); // Brown color for the stem
        g.fillRect(x + tileSize / 2 - 3, y - 6, 6, 12); // Stem

        // Draw a small leaf (green)
        g.setColor(Color.GREEN);
        int[] xPoints = {x + tileSize / 2, x + tileSize / 2 - 5, x + tileSize / 2 + 5};
        int[] yPoints = {y - 6, y - 12, y - 12};
        g.fillPolygon(xPoints, yPoints, 3); // Leaf
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
        } else {
            gameTimer.stop();
        }
        repaint(); //updates on display
    }

    @Override
    public void keyPressed(KeyEvent e) {
        playSound("assets/keypressing.wav"); // Replace with actual file path

        if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityx != 1) {
            velocityx = -1;
            velocityy = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityx != -1) {
            velocityx = 1;
            velocityy = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityy != 1) {
            velocityy = -1;
            velocityx = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityy != -1) {
            velocityy = 1;
            velocityx = 0;
        }
    }

    //no use but don't remove else it dies
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // Method to play sound
    private void playSound(String soundFile) {
        try {
            File sound = new File(soundFile);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to play background music
    private void playBackgroundMusic(String musicFile) {
        try {
            File music = new File(musicFile);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(music);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioIn);
            FloatControl volumeControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-25.0f);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the background music
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Stop background music
    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
        }
    }
}
