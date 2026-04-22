import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

public class UserInterface extends Frame {
    private DatabaseTable databaseTable;
    private TextField emailField;
    private Button addButton;
    private Button removeButton;
    private Button clearButton;
    private List userList;
    private Label statusLabel, titleLabel;

    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(128, 128, 128);    // Gray
    private final Color SECONDARY_COLOR = new Color(192, 192, 192);  // Light Gray
    private final Color ACCENT_COLOR = new Color(64, 64, 64);     // Dark Gray
    private final Color DANGER_COLOR = new Color(96, 96, 96);      // Medium Dark Gray
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Light gray
    private final Color CARD_COLOR = new Color(255, 255, 255);      // White
    private final Color TEXT_COLOR = new Color(33, 37, 41);         // Dark gray

    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 11);

    public UserInterface() {
        initializeUI();
        setupEventHandlers();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("👥 User Management System");
        setSize(700, 500);
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(15, 15));
        setResizable(true);

        // Title Panel
        Panel titlePanel = createTitlePanel();

        // Input Panel
        Panel inputPanel = createInputPanel();

        // List Panel
        Panel listPanel = createListPanel();

        // Control Panel
        Panel controlPanel = createControlPanel();

        // Status Panel
        Panel statusPanel = createStatusPanel();

        add(titlePanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(listPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private Panel createTitlePanel() {
        Panel panel = new Panel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(PRIMARY_COLOR);

        titleLabel = new Label("User Management System");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);

        panel.add(titleLabel);
        panel.setPreferredSize(new Dimension(700, 50));
        return panel;
    }

    private Panel createInputPanel() {
        Panel panel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        panel.setBackground(CARD_COLOR);

        Label emailLabel = createStyledLabel("📧 Email Address:");
        emailField = createStyledTextField(30);
        emailField.setText("user@example.com");
        emailField.setForeground(Color.GRAY);
        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (emailField.getText().equals("user@example.com")) {
                    emailField.setText("");
                    emailField.setForeground(TEXT_COLOR);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (emailField.getText().isEmpty()) {
                    emailField.setText("user@example.com");
                    emailField.setForeground(Color.GRAY);
                }
            }
        });

        panel.add(emailLabel);
        panel.add(emailField);
        panel.setPreferredSize(new Dimension(650, 80));

        return panel;
    }

    private Panel createListPanel() {
        Panel panel = new Panel(new BorderLayout());
        panel.setBackground(CARD_COLOR);

        Label listTitle = new Label("📋 Registered Users");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        listTitle.setForeground(PRIMARY_COLOR);

        databaseTable = new DatabaseTable();
        databaseTable.setPreferredSize(new Dimension(650, 200));

        panel.add(listTitle, BorderLayout.NORTH);
        panel.add(databaseTable, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(650, 230));

        return panel;
    }

    private Panel createControlPanel() {
        Panel panel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);

        addButton = createStyledButton("➕ Add User", SECONDARY_COLOR);
        clearButton = createStyledButton("🧹 Clear", ACCENT_COLOR);
        removeButton = createStyledButton("🗑️ Remove Selected", DANGER_COLOR);

        panel.add(addButton);
        panel.add(clearButton);
        panel.add(removeButton);

        return panel;
    }

    private Panel createStatusPanel() {
        Panel panel = new Panel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        statusLabel = new Label("✅ System Ready - Enter email to add users", Label.LEFT);
        statusLabel.setFont(labelFont);
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBackground(new Color(240, 240, 240));

        panel.add(statusLabel, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(700, 30));

        return panel;
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(labelFont);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private TextField createStyledTextField(int columns) {
        TextField field = new TextField(columns);
        field.setFont(labelFont);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_COLOR);
        return field;
    }

    private Button createStyledButton(String text, Color bgColor) {
        Button button = new Button(text);
        button.setFont(buttonFont);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        return button;
    }

    private void setupEventHandlers() {
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedUser();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void addUser() {
        String email = emailField.getText().trim();
        if (email.isEmpty() || email.equals("user@example.com")) {
            showError("Please enter a valid email address!");
            emailField.requestFocus();
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address format!");
            emailField.requestFocus();
            return;
        }

        try {
            User newUser = new User(email);
            databaseTable.addUser(newUser);
            clearForm();
            updateStatus("✅ User added successfully: " + newUser.getUsername(), SECONDARY_COLOR);
        } catch (Exception ex) {
            showError("Error adding user: " + ex.getMessage());
        }
    }

    private void removeSelectedUser() {
        int userCount = databaseTable.getUserCount();
        if (userCount == 0) {
            showError("No users to remove!");
            return;
        }

        int result = showConfirmDialog("Remove User",
            "Are you sure you want to remove the selected user?");

        if (result == 0) { // Yes
            databaseTable.removeUser(userCount - 1);
            updateStatus("🗑️ User removed successfully", ACCENT_COLOR);
        }
    }

    private void clearForm() {
        emailField.setText("user@example.com");
        emailField.setForeground(Color.GRAY);
        updateStatus("🧹 Form cleared - Ready for new entry", ACCENT_COLOR);
        emailField.requestFocus();
    }

    private void confirmExit() {
        if (databaseTable.getUserCount() > 0) {
            int result = showConfirmDialog("Exit Application",
                "You have " + databaseTable.getUserCount() + " registered users. Exit anyway?");
            if (result == 0) { // Yes
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    private void showError(String message) {
        updateStatus("❌ " + message, DANGER_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    private int showConfirmDialog(String title, String message) {
        return JOptionPane.showConfirmDialog(this, message, title,
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public static void main(String[] args) {
        new UserInterface();
    }
}
