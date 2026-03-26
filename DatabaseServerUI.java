import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

// 1. Extend Frame to use AWT window features
public class DatabaseServerUI extends Frame {
    private TextArea logArea;
    private Button startButton;
    private boolean isRunning = false;
    private ServerSocket serverSocket;

    public DatabaseServerUI() {
        // UI Setup
        setTitle("Database Server Control Panel");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN); // Classic terminal look
        add(logArea, BorderLayout.CENTER);

        startButton = new Button("Start Server");
        add(startButton, BorderLayout.SOUTH);

        // Handle Window Closing (AWT requires this manually)
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                stopServer();
                System.exit(0);
            }
        });

        // Button Action
        startButton.addActionListener(e -> toggleServer());

        setVisible(true);
    }

    private void toggleServer() {
        if (!isRunning) {
            // Start server in a background thread to keep UI responsive
            new Thread(this::startServer).start();
            startButton.setLabel("Stop Server");
        } else {
            stopServer();
            startButton.setLabel("Start Server");
        }
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(6060);
            isRunning = true;
            logArea.append("[INFO] Server started on port 6060...\n");

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                // Handle each client in its own thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            if (isRunning) logArea.append("[ERROR] " + e.getMessage() + "\n");
        }
    }

    private void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) serverSocket.close();
            logArea.append("[INFO] Server stopped.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        String clientAddr = socket.getInetAddress().toString();
        logArea.append("[CONN] Client connected: " + clientAddr + "\n");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String query;
            while ((query = in.readLine()) != null) {
                logArea.append("[QUERY] " + clientAddr + ": " + query + "\n");

                if (query.equalsIgnoreCase("exit")) {
                    logArea.append("[DISC] Client requested exit.\n");
                    break;
                }

                // Logic: Simulate Database Response
                String response = "Processed: " + query.toUpperCase();
                out.println(response);
            }
        } catch (IOException e) {
            logArea.append("[ERR] Client session closed unexpectedly.\n");
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public static void main(String[] args) {
        new DatabaseServerUI();
    }
}