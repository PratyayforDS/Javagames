import javax.swing.*;
import java.awt.*;
//must see at last

public class GameOverScreen extends JPanel {

    private int finalScore;

    // Constructor to set the final score
    public GameOverScreen(int finalScore) {
        this.finalScore = finalScore;
        setOpaque(false); // Allows underlying game screen to show through if needed
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set color and font for "Game Over" text
        g.setColor(Color.white);
        g.setFont(new Font("Monospaced", Font.BOLD, 50));

        String message = "GAME OVER";

        // Center the "Game Over" text
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int x = (getWidth() - metrics.stringWidth(message)) / 2;
        int y = getHeight() / 2 - metrics.getHeight();

        g.drawString(message, x, y);

        // Display the final score below the "Game Over" message
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String scoreMessage = "Final Score: " + finalScore;
        int scoreX = (getWidth() - g.getFontMetrics().stringWidth(scoreMessage)) / 2;
        int scoreY = y + metrics.getHeight() + 40;

        g.drawString(scoreMessage, scoreX, scoreY);
    }

    // Method to set the final score if it needs to be updated
    public void setFinalScore(int score) {
        this.finalScore = score;
        repaint();
    }
}
//how to use this gameover screen

//private GameOverScreen gameOverScreen;
// in starting of your game class

//then implement this method also in your game logic

//public void showGameOver() {
//    gameOverScreen.setFinalScore(score); // Set final score
//    gameOverScreen.setSize(getSize()); // Match the game panel's size
//    add(gameOverScreen); // Add the GameOverScreen panel on top of the game screen
//    gameOverScreen.repaint(); // Refresh to show the game-over screen
//} ///must copy for other games
