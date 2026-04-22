import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

// 1. Data Model
class AwtUser {
    private String email;
    private String username;

    public AwtUser(String email) {
        this.email = email;
        this.username = generateUsername(email);
    }

    private String generateUsername(String email) {
        if (email != null && email.contains("@")) {
            return email.split("@")[0];
        }
        return "UnknownUser";
    }

    public String getUsername() { return username; }

    @Override
    public String toString() {
        return String.format("%s (%s)", username, email);
    }
}

// 2. Logic & GUI Component Combined
class AwtUserTable extends java.awt.List {
    // Renamed to 'userList' to avoid conflict with 'java.awt.List'
    private java.util.List<AwtUser> userList = new ArrayList<>();

    public void addUser(AwtUser user) {
        userList.add(user);
        this.add(user.toString()); // Adds string representation to visual AWT List
    }

    public void removeUser(int index) {
        if (index >= 0 && index < userList.size()) {
            userList.remove(index);
            this.remove(index); // Removes from visual AWT List
        }
    }

    public int getUserCount() {
        return userList.size();
    }
}

class AwtDatabaseTable extends AwtUserTable { }

// 3. The Management UI
class AwtUserInterface extends Frame {
    private AwtDatabaseTable databaseTable;
    private TextField emailField;
    private Button addButton, removeButton;
    private Label statusLabel;

    public AwtUserInterface() {
        setTitle("User Management System");
        setSize(450, 400);
        setLayout(new BorderLayout(10, 10));

        databaseTable = new AwtDatabaseTable();
        
        // Input panel
        Panel inputPanel = new Panel(new FlowLayout());
        emailField = new TextField(20);
        addButton = new Button("Add User");
        removeButton = new Button("Remove Last");

        inputPanel.add(new Label("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);

        statusLabel = new Label("Ready", Label.CENTER);

        add(inputPanel, BorderLayout.NORTH);
        add(databaseTable, BorderLayout.CENTER); 
        add(statusLabel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addUser());
        removeButton.addActionListener(e -> removeSelectedUser());
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });

        setVisible(true);
        setLocation(550, 100); 
    }

    private void addUser() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && email.contains("@")) {
            AwtUser newUser = new AwtUser(email);
            databaseTable.addUser(newUser);
            emailField.setText("");
            statusLabel.setText("Added: " + newUser.getUsername());
        } else {
            statusLabel.setText("Invalid Email!");
        }
    }

    private void removeSelectedUser() {
        int count = databaseTable.getUserCount();
        if (count > 0) {
            databaseTable.removeUser(count - 1);
            statusLabel.setText("Removed last user");
        } else {
            statusLabel.setText("No users to remove");
        }
    }
}

// 4. The Server UI & Main Entry
public class CombinedInfoSystem extends Frame {
    private TextArea logArea;
    private Button startButton, openClientButton;
    private volatile boolean isRunning = false;
    private ServerSocket serverSocket;

    public CombinedInfoSystem() {
        setTitle("Database Server Control Panel");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(logArea, BorderLayout.CENTER);

        Panel bottomPanel = new Panel(new GridLayout(1, 2, 5, 5));
        startButton = new Button("Start Server");
        openClientButton = new Button("Open User Manager");
        bottomPanel.add(startButton);
        bottomPanel.add(openClientButton);
        add(bottomPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> toggleServer());
        openClientButton.addActionListener(e -> new AwtUserInterface());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                stopServer();
                System.exit(0);
            }
        });

        setVisible(true);
        setLocation(50, 100);
    }

    private void toggleServer() {
        if (!isRunning) {
            isRunning = true;
            startButton.setLabel("Stop Server");
            new Thread(this::startServer).start();
        } else {
            stopServer();
            startButton.setLabel("Start Server");
        }
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(6060);
            logArea.append("[INFO] Server started on port 6060...\n");

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (IOException e) {
                    if (isRunning) logArea.append("[ERROR] Accept failed: " + e.getMessage() + "\n");
                }
            }
        } catch (IOException e) {
            logArea.append("[ERROR] Could not listen on port 6060.\n");
        }
    }

    private void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            logArea.append("[INFO] Server stopped.\n");
        } catch (IOException e) {
            logArea.append("[ERR] Error closing server: " + e.getMessage() + "\n");
        }
    }

    private void handleClient(Socket socket) {
        String clientAddr = socket.getInetAddress().toString();
        logArea.append("[CONN] Client connected: " + clientAddr + "\n");
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String query;
            while ((query = in.readLine()) != null) {
                logArea.append("[QUERY] " + query + "\n");
                out.println("Processed: " + query.toUpperCase());
            }
        } catch (IOException e) {
            logArea.append("[ERR] Session closed for: " + clientAddr + "\n");
        } finally {
            try { socket.close(); } catch (IOException e) { /* Ignore */ }
        }
    }

    public static void main(String[] args) {
        new CombinedInfoSystem();
    }
}