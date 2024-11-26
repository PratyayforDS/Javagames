import Tetris.Tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class AlphaGameScreen extends JPanel implements KeyListener, MouseListener {
    private String[] menuOptions = {"Snake", "Mine Sweeper", "Tetris", "Sudoku", "Exit"};
    private int selectedOption = 0; // Track the currently selected option
    private Font pixelFont;
    private JFrame parentFrame; // Reference to the parent frame
    private Clip backgroundMusicClip; // To hold the background music clip
    private Clip traversalClip; //traversal
    public AlphaGameScreen(JFrame frame) {
        this.parentFrame = frame;

        // Load custom font
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/assets/PressStart2P-Regular.ttf")).deriveFont(24f);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font. Using default font.");
            pixelFont = new Font("SansSerif", Font.PLAIN, 24);
        }

        // Add event listeners
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();

        // Play background music when Alpha menu is displayed
        playBackgroundMusic();
    }

    private void playBackgroundMusic() {
        try {
            // Load a background music file (adjust the path as needed)
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("src/assets/MainTheme.wav")); //use wav only
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioStream);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); // Play the sound in a loop
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading or playing background music.");
        }
    }


    // Method to play sound

    private void playkeySound(String soundFile) {
        try {
            File sound = new File(soundFile);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Adjust the volume of the keypress sound
//            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//            volumeControl.setValue(-15.0f); // Set the volume lower (e.g., -15 dB)

            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop(); // Stop the music when leaving the menu screen
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw hills
        g.setColor(Color.GREEN);
        g.fillOval(10, getHeight() - 120, 180, 90); // Small hill
        g.fillOval(255, getHeight() - 170, 280, 140); // Large hill
        g.fillOval(595, getHeight() - 120, 180, 90); // Small hill

        // Title Banner
        g.setColor(Color.RED);
        g.fillRect(100, 50, getWidth() - 200, 100); // Red banner
        g.setColor(Color.BLACK);
        g.setFont(pixelFont); // Set custom pixel font

        // Shadow text for title
        g.drawString("ALPHA GAME SYSTEM", 190, 120);
        g.setColor(Color.WHITE);
        g.drawString("ALPHA GAME SYSTEM", 188, 118);

        // Menu Options
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == selectedOption) {
                g.setColor(Color.YELLOW); // Highlight selected option
            } else {
                g.setColor(Color.WHITE);
            }
            int stringWidth = g.getFontMetrics().stringWidth(menuOptions[i]);
            int x = (getWidth() - stringWidth) / 2; // Center alignment
            g.drawString(menuOptions[i], x, 250 + i * 50);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        playkeySound("src/assets/Pokemon (A Button) - Sound Effect (HD).wav");
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {

            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
        } else if (keyCode == KeyEvent.VK_DOWN) {

            selectedOption = (selectedOption + 1) % menuOptions.length;
        } else if (keyCode == KeyEvent.VK_ENTER) {
            handleMenuSelection(selectedOption);
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int y = e.getY();
        for (int i = 0; i < menuOptions.length; i++) {
            int optionY = 250 + i * 50;
            if (y > optionY - 20 && y < optionY + 20) {
                handleMenuSelection(i);
                repaint();
                break;
            }
        }
    }

    private void handleMenuSelection(int option) {
        if (option == 0) {
            // Start Game logic here
            stopBackgroundMusic(); // Stop the music when transitioning to the game
            startSnakeGame();
        } else if (option == 1) {
            // Load Game logic here (Minesweeper)
            stopBackgroundMusic(); // Stop the music when transitioning to the game
            loadMinesweeperGame();
        } else if (option == 2) {

            parentFrame.dispose();
            stopBackgroundMusic();
            var game = new Tetris();
            game.setVisible(true);

        } else if (option == 3) {
            stopBackgroundMusic(); // Stop the music when exiting;
            loadSudoku();
            System.out.println("Done");

        } else if (option ==4){
            System.exit(0);
        }
    }

    private void startSnakeGame() {
        parentFrame.setTitle("Snake Game 🐍");
        parentFrame.getContentPane().removeAll();

        SnakeGame snakeGame = new SnakeGame(790, 590);
        parentFrame.add(snakeGame);

        snakeGame.setFocusable(true);
        snakeGame.requestFocusInWindow();

        parentFrame.revalidate();
        parentFrame.repaint();
    }

    private void loadMinesweeperGame() {
        // Close the AlphaGameScreen by removing its components
        parentFrame.dispose();
        // Add the Minesweeper GUI to the parent frame
        Minesweeper minesweeper = new Minesweeper();
        parentFrame.add(minesweeper.frame);  // Add the full Minesweeper frame

        parentFrame.revalidate();
        parentFrame.repaint();
    }

    private void loadSudoku() {
        // Stop the background music when switching to Sudoku
        stopBackgroundMusic();

        // Create a new instance of SudokuGame and make it visible
        SwingUtilities.invokeLater(() -> {
            SudokuGame sudoku = new SudokuGame();
            sudoku.setVisible(true);
        });

        // Optionally, hide the current frame
        parentFrame.setVisible(false);
    }

    // Unused event methods
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Alpha Game System");
        AlphaGameScreen screen = new AlphaGameScreen(frame);

        frame.add(screen);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
