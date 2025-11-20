import javax.swing.*; // For GUI components

// Main class to run the application
public class DrowsinessApp {
    // Static panel to display messages (accessible from other classes)
    public static MessagePanel messagePanel;

    // Main method (entry point of the program)
    public static void main(String[] args) {
        // This makes sure GUI updates run on the correct thread
        SwingUtilities.invokeLater(() -> {
            // Create the main window (frame) of the application
            JFrame window = new JFrame("Drowsiness Detection System");

            // When we close the window, the program should stop
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the size of the window (width = 1000, height = 600)
            window.setSize(1000, 600);

            // Create the left part of screen (game panel where car drives)
            GamePanel gamePanel = new GamePanel();

            // Create the center part (camera panel to show camera and detect eyes)
            CameraPanel cameraPanel = new CameraPanel();

            // Create the message panel (right side) where system messages will appear
            messagePanel = new MessagePanel();

            // Add initial messages to show when the app starts
            messagePanel.addMessage("    ___🚕___🚕___ VEHICLE STARTED ___🚕     .");
            messagePanel.addMessage("DETECTING DRIVER EYES 😎...");

            // Create a split between camera (center) and messages (right)
            JSplitPane centerRightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cameraPanel, messagePanel);
            centerRightSplit.setResizeWeight(0.8); // camera gets 60%, message gets 40%

            // Create the full screen split: game (left), camera + message (right)
            JSplitPane fullSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gamePanel, centerRightSplit);
            fullSplit.setResizeWeight(0.2); // game gets 30%, right side gets 70%

            // Add the full layout to the window
            window.getContentPane().add(fullSplit);

            // Make the window visible on the screen
            window.setVisible(true);
        });
    }
}