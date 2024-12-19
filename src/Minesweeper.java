import  java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class Minesweeper {
    Timer gameTimer;
    int secondsElapsed = 0;
    private Clip backgroundMusicClip;

    public class MineTile extends JButton{
        int r,c;

        public MineTile(int r,int c){
            this.r=r;
            this.c=c;
        }
    }

    int tileSize=70;
    int numRows=8;
    int numCols=numRows;
    int boardWidth =numCols*tileSize;
    int boardHeight=numRows*tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel =new JPanel();
    JPanel controlPanel = new JPanel(); // New control panel for buttons

    int mineCount = 10;
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked=0;
    boolean gameOver=false;

    Minesweeper() {
        playBackgroundMusic();
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + mineCount + " | Time: 0");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        boardPanel.setBackground(Color.GREEN);
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            playSound("src/assets/tap-notification-180637.wav");
                            if (Objects.equals(tile.getText(), "")) {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                    gameTimer.stop(); // Stop timer on game over
                                } else {
                                    checkMIne(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            playSound("src/assets/flag2.wav");
                            if (Objects.equals(tile.getText(), "") && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if (Objects.equals(tile.getText(), "🚩")) {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);
        setMines();

        // Initialize and start the game timer
        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            textLabel.setText("Minesweeper: " + mineCount + " | Time: " + secondsElapsed);
        });
        gameTimer.start();
    }

    private void playBackgroundMusic() {
        try {
            // Load a background music file (adjust the path as needed)
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("src/assets/minebgm.wav")); //use wav only
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioStream);
            FloatControl volumeControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-10.0f);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); // Play the sound in a loop
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading or playing background music.");
        }
    }

    void setMines(){
        mineList=new ArrayList<MineTile>();
        int mineLeft = mineCount;
        while(mineLeft>0){
            int r= random.nextInt(numRows);
            int c=random.nextInt(numCols);

            MineTile tile = board[r][c];
            if(!mineList.contains(tile)){
                mineList.add(tile);
                mineLeft-=1;
            }
        }

    }
    private void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    void revealMines() {
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }
        playSound("src/assets/explosion.wav");
        backgroundMusicClip.stop();

        gameOver = true;
        gameTimer.stop(); // Stop the timer

        // Show Game Over screen
        showEndScreenWithDelay("Game Over",1000);
    }


    void checkMIne(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }
        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked++;
        int minesFound = 0;

        // Check surrounding tiles
        minesFound += countMine(r - 1, c - 1);
        minesFound += countMine(r - 1, c);
        minesFound += countMine(r - 1, c + 1);
        minesFound += countMine(r, c - 1);
        minesFound += countMine(r, c + 1);
        minesFound += countMine(r + 1, c - 1);
        minesFound += countMine(r + 1, c);
        minesFound += countMine(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");
            checkMIne(r - 1, c - 1);
            checkMIne(r - 1, c);
            checkMIne(r - 1, c + 1);
            checkMIne(r, c - 1);
            checkMIne(r, c + 1);
            checkMIne(r + 1, c - 1);
            checkMIne(r + 1, c);
            checkMIne(r + 1, c + 1);
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            gameTimer.stop(); // Stop the timer
            backgroundMusicClip.stop();
            playSound("src/assets/winner.wav");
            // Show Winner screen
            showEndScreenWithDelay("You Win!",2800);
        }

    }


    int countMine(int r,int c){
        if(r<0 ||r>=numRows||c<0||c>=numCols){
            return 0;
        }
        if(mineList.contains(board[r][c])){
            return 1;
        }
        return 0;
    }

    private void showEndScreenWithDelay(String message,int time) {
        // Create a Timer to delay the execution
        Timer delayTimer = new Timer(time, e -> {
            // Show the end screen after 3 seconds
            showEndScreen(message);
        });
        delayTimer.setRepeats(false); // Ensure it runs only once
        delayTimer.start();
    }

    private void showEndScreen(String message) {
        // Remove existing components
        frame.getContentPane().removeAll();

        // Create a new panel for the end screen
        JPanel endPanel = new JPanel();
        endPanel.setLayout(new GridBagLayout());
        endPanel.setBackground(Color.BLACK);

        // Add the end game message
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(Color.BLACK);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS)); // Vertical layout

        // Add the end game message
        JLabel endMessage = new JLabel(message, JLabel.CENTER);
        endMessage.setFont(new Font("Arial", Font.BOLD, 40));
        endMessage.setForeground(Color.WHITE);
        endMessage.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment for BoxLayout
        messagePanel.add(endMessage);

        // Add spacing between the labels
        messagePanel.add(Box.createVerticalStrut(10)); // Add vertical space between labels

        // Add the final time
        JLabel timeMessage = new JLabel("Time: " + secondsElapsed + " seconds", JLabel.CENTER);
        timeMessage.setFont(new Font("Arial", Font.PLAIN, 30));
        timeMessage.setForeground(Color.YELLOW);
        timeMessage.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment for BoxLayout
        messagePanel.add(timeMessage);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment
        gbc.fill = GridBagConstraints.NONE;

        endPanel.add(messagePanel, gbc);

        // Create buttons for Restart and Go Back
        JPanel buttonPanel = getjPanel();
        gbc.gridy = 1; // Place buttons below the messages
        gbc.weighty = 0; // No extra vertical space for buttons
        endPanel.add(buttonPanel, gbc);
        // Add the end panel to the frame
        frame.add(endPanel);

        // Refresh the frame
        frame.revalidate();
        frame.repaint();
    }

    private JPanel getjPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new FlowLayout());

        JButton restartButton = new JButton("Restart");
        restartButton.setBackground(Color.GREEN); // Set the background color
        restartButton.setForeground(Color.WHITE); // Set the text color
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 16)); // Optional: Change font
        restartButton.setFocusPainted(false);


        JButton backButton = new JButton("Menu");
        backButton.setBackground(Color.RED); // Set the background color
        backButton.setForeground(Color.WHITE); // Set the text color
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16)); // Optional: Change font
        backButton.setFocusPainted(false); // Optional: Remove focus outline

        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);



        // Add functionality to Restart button
        restartButton.addActionListener(e -> {
            frame.dispose(); // Close the current frame
            new Minesweeper(); // Restart the game
        });

        // Add functionality to Back button
        backButton.addActionListener(e -> {
            frame.dispose(); // Close the current frame
            // Return to the game menu
            SwingUtilities.invokeLater(() -> {
                JFrame parentFrame = new JFrame("Alpha Game System");
                AlphaGameScreen screen = new AlphaGameScreen(parentFrame);
                parentFrame.add(screen);
                parentFrame.setSize(800, 600);
                parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                parentFrame.setLocationRelativeTo(null);
                parentFrame.setResizable(false);
                parentFrame.setVisible(true);
            });
        });

//        buttonPanel.add(restartButton);
//        buttonPanel.add(backButton);
        buttonPanel.add(Box.createVerticalStrut(20)); // Add vertical spacing
        buttonPanel.add(restartButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Add spacing between buttons
        buttonPanel.add(backButton);
        return buttonPanel;
    }

}
