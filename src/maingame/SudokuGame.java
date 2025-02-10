package maingame;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

public class SudokuGame extends JFrame {
    private static final int SIZE = 9;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private int[][] solution = new int[SIZE][SIZE];
    private int[][] puzzle = new int[SIZE][SIZE];
    private Clip backgroundMusicClip;

    public SudokuGame() {
        setTitle("Sudoku Game");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Calculate the center of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y); // Set the location explicitly

        JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE));
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                boardPanel.add(cells[row][col]);
            }
        }

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton newGameButton = new JButton("New Game");
        JButton solutionButton = new JButton("Show Solution");
        JButton checkButton = new JButton("Check Answer");
        JButton menuButton = new JButton("Menu");

        buttonPanel.add(newGameButton);
        buttonPanel.add(solutionButton);
        buttonPanel.add(checkButton);
        buttonPanel.add(menuButton);

        add(boardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        menuButton.addActionListener(e -> {
            this.dispose(); // Close the current frame
            // Return to the game menu
            stopBackgroundMusic();
            SwingUtilities.invokeLater(() -> {
                AlphaGameScreen.main(new String[0]);
            });
        });

        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopBackgroundMusic();
                generateNewPuzzle();
                displayPuzzle();
            }
        });

        solutionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displaySolution();
            }
        });

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkSolution();
            }
        });

        generateNewPuzzle();
        displayPuzzle();
//        playBackgroundMusic("src/assets/sudokuBG.wav");
    }

    private void generateNewPuzzle() {
        generateSolution();
        copySolutionToPuzzle();
        removeSomeNumbers();
        playBackgroundMusic("/assets/sudokuBG.wav");
    }

    private void generateSolution() {
        // Fill with a valid Sudoku solution (simplified for example)
        int[][] predefinedSolution = {
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };

        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(predefinedSolution[i], 0, solution[i], 0, SIZE);
        }
    }

    private void copySolutionToPuzzle() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                puzzle[row][col] = solution[row][col];
            }
        }
    }

    private void removeSomeNumbers() {
        Random random = new Random();
        for (int i = 0; i < SIZE * SIZE * 0.5; i++) { // Remove approximately half of the numbers
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            puzzle[row][col] = 0; // Set to 0 to represent an empty cell
        }
    }

    private void displayPuzzle() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (puzzle[row][col] == 0) {
                    cells[row][col].setText("");
                    cells[row][col].setEditable(true);
                } else {
                    cells[row][col].setText(String.valueOf(puzzle[row][col]));
                    cells[row][col].setEditable(false);
                }
            }
        }
    }

    private void displaySolution() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText(String.valueOf(solution[row][col]));
            }
        }
    }

    private void checkSolution() {
        stopBackgroundMusic();
        boolean isCorrect = true;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = cells[row][col].getText();
                if (text.isEmpty() || Integer.parseInt(text) != solution[row][col]) {
                    cells[row][col].setBackground(Color.RED);
                    isCorrect = false;
                } else {
                    cells[row][col].setBackground(Color.GREEN);
                }
            }
        }
        if (isCorrect) {
            JOptionPane.showMessageDialog(this, "Congratulations! You solved the puzzle!");
        } else {
            JOptionPane.showMessageDialog(this, "Some cells are incorrect. Keep trying!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuGame game = new SudokuGame();
            game.setVisible(true);
        });
    }

    private void playBackgroundMusic(String musicFile) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(musicFile));
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioIn);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the background music
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
        }
    }
}
