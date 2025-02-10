package maingame;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class GameOverScreen extends JPanel {

    private int finalScore;
    private JPanel endPanel;
    private JFrame parentFrame; // Reference to the parent frame for restarting logic

    public GameOverScreen(int finalScore, JFrame parentFrame) {
        this.finalScore = finalScore;
        this.parentFrame = parentFrame;

        setOpaque(false);
        endPanel = new JPanel();
        endPanel.setLayout(new GridBagLayout());
        endPanel.setBackground(Color.BLUE);

        setLayout(new BorderLayout());
        add(endPanel, BorderLayout.CENTER);

        playGameOverSound(); // Play sound when game over screen appears
        initUI();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Add message panel
        JPanel messagePanel = createMessagePanel();
        endPanel.add(messagePanel, gbc);

        // Add button panel
        gbc.gridy = 1;
        JPanel buttonPanel = createButtonPanel();
        endPanel.add(buttonPanel, gbc);
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JLabel gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(loadFont("/assets/PressStart2P-Regular.ttf", 50));
        gameOverLabel.setForeground(Color.WHITE);

        JLabel scoreLabel = new JLabel("Final Score: " + finalScore);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 30));
        scoreLabel.setForeground(Color.WHITE);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(gameOverLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(scoreLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLUE);

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        restartButton.setBackground(Color.GREEN);
        restartButton.setForeground(Color.WHITE);

        JButton backButton = new JButton("Menu");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);

        // Add functionality for Restart button
        restartButton.addActionListener(e -> restartSnakeGame());

        // Add functionality for Menu button
        backButton.addActionListener(e -> goBackToMenu());

        panel.add(restartButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(backButton);

        return panel;
    }

    private void restartSnakeGame() {
        // Reset the frame and start the Snake game
        parentFrame.getContentPane().removeAll();

        SnakeGame snakeGame = new SnakeGame(790, 590, parentFrame);
        parentFrame.add(snakeGame);

        snakeGame.setFocusable(true);
        snakeGame.requestFocusInWindow();

        parentFrame.revalidate();
        parentFrame.repaint();
    }

    private void goBackToMenu() {
        // Go back to AlphaGameScreen
        parentFrame.getContentPane().removeAll();

        AlphaGameScreen alphaGameScreen = new AlphaGameScreen(parentFrame);
        parentFrame.add(alphaGameScreen);

        alphaGameScreen.setFocusable(true);
        alphaGameScreen.requestFocusInWindow();

        parentFrame.revalidate();
        parentFrame.repaint();
    }

    private void playGameOverSound() {
        try {
            URL soundURL = getClass().getResource("/assets/GameOver.wav");
            if (soundURL != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } else {
                System.err.println("GameOver sound file not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Font loadFont(String path, float size) {
        try {
            URL fontURL = getClass().getResource(path);
            if (fontURL != null) {
                return Font.createFont(Font.TRUETYPE_FONT, fontURL.openStream()).deriveFont(size);
            }
        } catch (Exception e) {
            System.err.println("Font loading failed, using default font.");
        }
        return new Font("Monospaced", Font.BOLD, (int) size);
    }


    // Allow updating the final score dynamically
    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
        repaint();
    }
}
