import javax.swing.*;              // For GUI elements like JPanel, Timer, etc.
import java.awt.*;                 // For drawing graphics (like cars, trees, etc.)
import java.awt.event.*;           // For keyboard and timer actions
import java.util.ArrayList;        // For list of enemy cars

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;                        // Timer to update game screen every few milliseconds
    private int carX = 130, carY = 400;         // Player car's position (X and Y)
    private final ArrayList<Rectangle> enemies = new ArrayList<>(); // List of enemy cars
    private int roadOffsetY = 0;                // Used to scroll the road lines vertically

    public static boolean autoMode = false;     // Static variable to check if car is in Auto-Driving Mode

    // Constructor - runs once when object of GamePanel is created
    public GamePanel() {
        setFocusable(true);         // To make sure keyboard input works on this panel
        addKeyListener(this);       // Register the keyboard listener for this panel
        setPreferredSize(new Dimension(300, 600));  // Set panel size (width x height)
        setBackground(Color.DARK_GRAY);       // Set background color to dark gray (road)

        // Add one enemy car at starting position
        enemies.add(new Rectangle(130, -100, 40, 60));

        // Create timer and start it (updates every 30 milliseconds)
        timer = new Timer(30, this);
        timer.start();
    }

    // Called repeatedly by the Timer to update game state
    @Override
    public void actionPerformed(ActionEvent e) {
        // Move the road lines down to create scrolling effect
        roadOffsetY = (roadOffsetY + 5) % getHeight();

        // Move each enemy car downwards
        for (Rectangle enemy : enemies) {
            enemy.y += 5;

            // If enemy goes out of screen, reset its position to top
            if (enemy.y > getHeight()) {
                enemy.y = -100;
                // Randomize X position (left, center, right)
                enemy.x = (int) (Math.random() * 3) * 60 + 60;
            }

            // Check if enemy hits the player car
            Rectangle player = new Rectangle(carX, carY, 40, 60);
            if (enemy.intersects(player)) {
                timer.stop(); // Stop game when crash happens

                // Ask user if they want to restart
                int result = JOptionPane.showConfirmDialog(this, "You had an accident..🤕 Restart?", "Game Over", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    // Reset game variables
                    carX = 130;
                    carY = 400;
                    enemy.y = -100;
                    timer.start(); // Restart timer
                } else {
                    System.exit(0); // Exit game if user says NO
                }
            }
        }

        // If Auto Mode is active, then use automatic movement
        if (autoMode) {
            autoDrive();
        }

        repaint(); // Re-draw everything
    }

    // Automatic car movement logic to avoid enemy cars
    private void autoDrive() {
        Rectangle player = new Rectangle(carX, carY, 40, 60);
        for (Rectangle enemy : enemies) {
            // Check if enemy is near the player's Y and same X position (danger zone)
            if (Math.abs(enemy.y - player.y) < 100 && enemy.x == carX) {
                // Try moving left if space is clear
                if (carX > 60 && !isCarAt(carX - 60)) {
                    carX -= 60;
                }
                // Else try moving right if space is clear
                else if (carX < getWidth() - 100 && !isCarAt(carX + 60)) {
                    carX += 60;
                }
                break; // Only move once per cycle
            }
        }
    }

    // Check if another car is already at a given X position
    private boolean isCarAt(int x) {
        for (Rectangle enemy : enemies) {
            // If enemy car is near the desired X and Y position
            if (Math.abs(enemy.x - x) < 10 && Math.abs(enemy.y - carY) < 100) {
                return true; // Not safe to move here
            }
        }
        return false; // Safe to move
    }

    // Draw the game visuals
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the road background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw white dashed road lines
        g.setColor(Color.WHITE);
        for (int i = roadOffsetY % 40; i < getHeight(); i += 40) {
            g.fillRect(getWidth() / 2 - 5, i, 10, 20);
        }

        // Draw green trees on the side of the road
        g.setColor(new Color(34, 139, 34));
        for (int i = 0; i < getHeight(); i += 60) {
            g.fillOval(10, i, 20, 20);                         // Left tree
            g.fillOval(getWidth() - 30, i + 30, 20, 20);       // Right tree
        }

        // Draw the player car (red)
        g.setColor(Color.RED);
        g.fillRect(carX, carY, 40, 60);

        // Draw enemy cars (cyan)
        g.setColor(Color.CYAN);
        for (Rectangle enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
        }
    }

    // When a key is pressed
    @Override
    public void keyPressed(KeyEvent e) {
        // Only move if Auto Mode is OFF (Manual Mode)
        if (!autoMode) {
            // A = move left
            if (e.getKeyCode() == KeyEvent.VK_A && carX > 60) carX -= 20;

            // D = move right
            if (e.getKeyCode() == KeyEvent.VK_D && carX < getWidth() - 100) carX += 20;

            // W = move up
            if (e.getKeyCode() == KeyEvent.VK_W && carY > 0) carY -= 20;

            // S = move down
            if (e.getKeyCode() == KeyEvent.VK_S && carY < getHeight() - 60) carY += 20;

            repaint(); // Redraw with new position
        }
    }

    // Not used, but required to implement KeyListener
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}