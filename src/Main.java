import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        int boardWidth = 800;
        int boardHeight = 800;
        JFrame frame = new JFrame("ALPHASystem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        // Create a panel to hold the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4)); // 1 row, 4 columns

        // Create buttons
        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");
        JButton button3 = new JButton("Button 3");
        JButton button4 = new JButton("Button 4");

        // Add action listeners to buttons
        button1.addActionListener(new ButtonClickSnake(boardWidth, boardHeight, frame, panel)); //basically fn to call when button is exe
        // Add other buttons' action listeners similarly if you want different actions for them.
        // e.g., button2.addActionListener(new ButtonClickAction(...));

        // Add buttons to the panel
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(button4);

        // Add panel to the frame
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    static class ButtonClickSnake implements ActionListener {
        int boardWidth;
        int boardHeight;
        JFrame frame;
        JPanel panel;

        public ButtonClickSnake(int boardWidth, int boardHeight, JFrame frame, JPanel panel) {
            this.boardWidth = boardWidth;
            this.boardHeight = boardHeight;
            this.frame = frame;
            this.panel = panel;
        }

        public void actionPerformed(ActionEvent e) {
            frame.setTitle("snake game 🐍");
            // Remove the button panel
            frame.remove(panel); //remove button grids

            // Add the SnakeGame component
            SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
            frame.add(snakeGame);

            // Refresh the frame
            frame.pack(); 
            frame.setVisible(true);
            snakeGame.requestFocusInWindow();
        }
    }
}
