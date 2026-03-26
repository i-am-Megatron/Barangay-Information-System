import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserInterface extends Frame {
    private DatabaseTable databaseTable;
    private TextField emailField;
    private Button addButton;
    private Button removeButton;
    private List userList;
    private Label statusLabel;

    public UserInterface() {
        setTitle("User Management System");
        setSize(600, 400);
        setLayout(new BorderLayout(10, 10));

        // Initialize components
        databaseTable = new DatabaseTable();

        // Input panel
        Panel inputPanel = new Panel(new FlowLayout());
        Label emailLabel = new Label("Email:");
        emailField = new TextField(25);
        addButton = new Button("Add User");
        removeButton = new Button("Remove Selected");

        inputPanel.add(emailLabel);
        inputPanel.add(emailField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);

        // Status label
        statusLabel = new Label("Ready");
        statusLabel.setAlignment(Label.CENTER);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(databaseTable, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // Event handlers
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

        // Window closing handler
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void addUser() {
        String email = emailField.getText().trim();
        if (!email.isEmpty()) {
            User newUser = new User(email);
            databaseTable.addUser(newUser);
            emailField.setText("");
            statusLabel.setText("User added: " + newUser.getUsername());
        } else {
            statusLabel.setText("Please enter an email address");
        }
    }

    private void removeSelectedUser() {
        // For simplicity, remove the last user
        // In a real application, you'd implement selection
        int userCount = databaseTable.getUserCount();
        if (userCount > 0) {
            databaseTable.removeUser(userCount - 1);
            statusLabel.setText("Last user removed");
        } else {
            statusLabel.setText("No users to remove");
        }
    }

    public static void main(String[] args) {
        new UserInterface();
    }
}
