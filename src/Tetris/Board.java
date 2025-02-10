package Tetris;

import Tetris.Shape.Tetrominoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.sound.sampled.*;
import java.io.IOException;
import maingame.AlphaGameScreen;

public class Board extends JPanel {

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private final int PERIOD_INTERVAL = 400;

    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private JLabel statusbar;
    private Shape curPiece;
    private Tetrominoe[] board;
    private Color backgroundColor = Color.BLACK;
    //    private GameOverScreenforTetris tetrisgameover;
    private Clip backgroundMusic;
    private JPanel endPanel;
    private boolean soundPlayed = false;

    private void playBackgroundMusic(String filePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(filePath));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            soundPlayed = false;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public Board(Tetris parent) {
        initBoard(parent);
    }

    private void initBoard(Tetris parent) {
        setFocusable(true);
        statusbar = parent.getStatusBar();
        addKeyListener(new TAdapter());
    }

    private int squareWidth() {
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    private int squareHeight() {
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    private Tetrominoe shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }

    public void start() {
        playBackgroundMusic("/assets/minesweeperbgm.wav");

        curPiece = new Shape();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];

        clearBoard();
        newPiece();
//        tetrisgameover = new GameOverScreenforTetris(numLinesRemoved);
        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }

    private void pause() {
        isPaused = !isPaused;

        if (isPaused) {
            statusbar.setText("paused");
        } else {
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }

    private void initUI() {
        endPanel = new JPanel();
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

    private void playGameOverSound() {
        if (!soundPlayed) {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource("/assets/game-over-arcade-6435.wav"));
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();  // Play the sound
                soundPlayed = true;  // Set the flag to true so the sound is played only once
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace(); // Handle any exceptions that occur
            }
        }
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JLabel gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Monospaced", Font.BOLD, 50));
        gameOverLabel.setForeground(Color.WHITE);

        JLabel scoreLabel = new JLabel("Final Score: " + 0);
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
        panel.setBackground(Color.blue);

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        restartButton.setBackground(Color.GREEN);
        restartButton.setForeground(Color.WHITE);

        JButton backButton = new JButton("Menu");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);

        // Add functionality for Restart button
        restartButton.addActionListener(e -> {
            restartGame();
        });

        // Add functionality for Menu button
        backButton.addActionListener(e -> {
            backToMenu();
        });

        panel.add(restartButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(backButton);

        return panel;
    }

    public void showGameOver() {
        // Stop the timer and background music
        timer.stop();
        stopBackgroundMusic();
        playGameOverSound();
        // Create and configure the end panel
        endPanel = new JPanel(new GridBagLayout());
        endPanel.setBackground(new Color(0, 0, 0, 200)); // Semi-transparent black

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Add Game Over message
        JLabel gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Monospaced", Font.BOLD, 50));
        gameOverLabel.setForeground(Color.WHITE);
        endPanel.add(gameOverLabel, gbc);

        // Add final score
        gbc.gridy++;
        JLabel scoreLabel = new JLabel("Final Score: " + numLinesRemoved);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 30));
        scoreLabel.setForeground(Color.WHITE);
        endPanel.add(scoreLabel, gbc);

        // Add buttons
        gbc.gridy++;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        restartButton.setBackground(Color.GREEN);
        restartButton.setForeground(Color.WHITE);
        restartButton.addActionListener(e -> restartGame());

        JButton backButton = new JButton("Menu");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> backToMenu());

        buttonPanel.add(restartButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(backButton);

        endPanel.add(buttonPanel, gbc);

        // Add the end panel to the Board
        setLayout(new BorderLayout());
        add(endPanel, BorderLayout.CENTER);

        // Refresh UI
        revalidate();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(backgroundColor);
        doDrawing(g);
    }

    private void restartGame() {
        removeAll(); // Clear all components
        revalidate();
        repaint();
        start(); // Restart the game
    }

    private void backToMenu() {
        removeAll(); // Clear all components from the board
        revalidate();
        repaint();

        // Dispose of the current game window
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (currentFrame != null) {
            currentFrame.dispose();
        }

        // Open the AlphaGameScreen
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Alpha Game System");
            AlphaGameScreen screen = new AlphaGameScreen(frame);

            frame.add(screen);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true); // Instantiate the AlphaGameScreen
        });
    }

    private void doDrawing(Graphics g) {
        var size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Tetrominoe.NoShape) {
                    drawSquare(g, j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareWidth(),
                        boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            newY--;
        }
        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            board[i] = Tetrominoe.NoShape;
        }
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }
        removeFullLines();
        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void newPiece() {
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();
        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoe.NoShape);
            timer.stop();
            showGameOver();
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }
            if (shapeAt(x, y) != Tetrominoe.NoShape) {
                return false;
            }
        }
        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void removeFullLines() {
        int numFullLines = 0;
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Tetrominoe.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }
        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoe shape) {
        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };

        var color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private void doGameCycle() {
        update();
        repaint();
    }

    private void update() {
        if (isPaused) {
            return;
        }
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (curPiece.getShape() == Tetrominoe.NoShape) {
                return;
            }
            int keycode = e.getKeyCode();
            switch (keycode) {
                case KeyEvent.VK_P -> pause();
                case KeyEvent.VK_LEFT -> tryMove(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> tryMove(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN -> tryMove(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_UP -> tryMove(curPiece.rotateLeft(), curX, curY);
                case KeyEvent.VK_SPACE -> dropDown();
                case KeyEvent.VK_D -> oneLineDown();
            }
        }
    }
}
