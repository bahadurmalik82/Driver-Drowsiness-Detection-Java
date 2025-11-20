import javax.swing.*;         // This is used to create GUI components like JPanel, JLabel, etc.
import java.awt.*;            // This is used for layout and color handling

public class MessagePanel extends JPanel {
    private final JTextArea messageBox;  // This is a box where we will show multiple text messages

    // Constructor – runs when object of MessagePanel is created
    public MessagePanel() {
        setLayout(new BorderLayout()); // This sets layout to "BorderLayout" so we can add components in top and center
        setBackground(Color.LIGHT_GRAY); // This sets background color of the panel to light gray

        messageBox = new JTextArea();   // Create a new area where we can write/display text messages
        messageBox.setEditable(false);  // Make sure user cannot type in this box
        messageBox.setLineWrap(true);   // If a sentence is too long, it will move to the next line
        messageBox.setWrapStyleWord(true); // Makes line break happen between words, not inside a word

        // This label will show at the top of the panel
        JLabel title = new JLabel("☠--- HISTORY MESSAGES ---☠", SwingConstants.CENTER); // Create a label and align text to center
        add(title, BorderLayout.NORTH); // Add title at the top of the panel (NORTH area of BorderLayout)

        // Add the message box in a scroll pane so we can scroll through messages
        JScrollPane scroll = new JScrollPane(messageBox); // Scroll pane lets us scroll if messages overflow
        add(scroll, BorderLayout.CENTER); // Add scroll pane to the center of the panel
    }

    // This method adds a new message to the message box
    public void addMessage(String text) {
        // This makes sure the message is added safely to the text area from GUI thread
        SwingUtilities.invokeLater(() -> messageBox.append(text + "\n")); // Append message to the box with a new line
    }
}