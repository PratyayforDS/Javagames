import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class GameOverScreen extends JPanel {

    private int finalScore;
    private boolean soundPlayed = false;  // Flag to ensure sound is only played once

    // Constructor to set the final score
    public GameOverScreen(int finalScore) {
        this.finalScore = finalScore;
        setOpaque(false); // Allows underlying game screen to show through if needed
    }

    // Method to load and play the game-over sound
    private void playGameOverSound() {
        if (!soundPlayed) {
            try {
                // Replace with the correct path to your sound file
                File soundFile = new File("src/assets/game-over-arcade-6435.wav");
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();  // Play the sound

                soundPlayed = true;  // Set the flag to true so the sound is played only once
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace(); // Handle any exceptions that occur
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Play the sound when the screen is first drawn
        playGameOverSound();

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
