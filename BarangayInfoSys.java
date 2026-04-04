import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

// 1. Resident Data Model
class Resident {
    private String firstName, lastName, middleName, address, contactNumber, birthDate, gender, civilStatus, occupation, email, emergencyContactName, emergencyContactNumber;
    private long id;

    public Resident(String firstName, String lastName, String middleName,
                   String address, String contactNumber, String birthDate, String gender,
                   String civilStatus, String occupation, String email,
                   String emergencyContactName, String emergencyContactNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.birthDate = birthDate;
        this.gender = gender;
        this.civilStatus = civilStatus;
        this.occupation = occupation;
        this.email = email;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactNumber = emergencyContactNumber;
        this.id = System.currentTimeMillis();
    }

    public String getFullName() { return firstName + " " + middleName + " " + lastName; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public String getGender() { return gender; }
    public String getOccupation() { return occupation; }

    @Override
    public String toString() {
        return String.format("👤 %s | 📍 %s | 📞 %s | %s | 💼 %s",
                           getFullName(), address, contactNumber, gender, occupation);
    }
}

// 2. Additional Data Models
class Certificate {
    private long id;
    private String residentName;
    private String type;
    private String purpose;
    private String issueDate;
    private String status;

    public Certificate(String residentName, String type, String purpose, String issueDate) {
        this.id = System.currentTimeMillis();
        this.residentName = residentName;
        this.type = type;
        this.purpose = purpose;
        this.issueDate = issueDate;
        this.status = "Pending";
    }

    public long getId() { return id; }
    public String getResidentName() { return residentName; }
    public String getType() { return type; }
    public String getPurpose() { return purpose; }
    public String getIssueDate() { return issueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%s) - %s - %s", id, residentName, type, purpose, issueDate, status);
    }
}

class Complaint {
    private long id;
    private String residentName;
    private String subject;
    private String details;
    private String status;
    private String filedDate;

    public Complaint(String residentName, String subject, String details, String filedDate) {
        this.id = System.currentTimeMillis();
        this.residentName = residentName;
        this.subject = subject;
        this.details = details;
        this.filedDate = filedDate;
        this.status = "New";
    }

    public long getId() { return id; }
    public String getResidentName() { return residentName; }
    public String getSubject() { return subject; }
    public String getDetails() { return details; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFiledDate() { return filedDate; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s: %s (%s)", id, residentName, subject, status, filedDate);
    }
}

class PaymentRecord {
    private long id;
    private String payer;
    private String purpose;
    private double amount;
    private String date;

    public PaymentRecord(String payer, String purpose, double amount, String date) {
        this.id = System.currentTimeMillis();
        this.payer = payer;
        this.purpose = purpose;
        this.amount = amount;
        this.date = date;
    }

    public long getId() { return id; }
    public String getPayer() { return payer; }
    public String getPurpose() { return purpose; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return String.format("[%d] %s paid %.2f for %s (%s)", id, payer, amount, purpose, date);
    }
}

class Announcement {
    private long id;
    private String title;
    private String message;
    private String postDate;

    public Announcement(String title, String message, String postDate) {
        this.id = System.currentTimeMillis();
        this.title = title;
        this.message = message;
        this.postDate = postDate;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s): %s", id, title, postDate, message);
    }
}

class SystemUser {
    private long id;
    private String name;
    private String email;
    private String role;
    private String barcodeId;

    public SystemUser(String name, String email, String role, String barcodeId) {
        this.id = System.currentTimeMillis();
        this.name = name;
        this.email = email;
        this.role = role;
        this.barcodeId = barcodeId;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getBarcodeId() { return barcodeId; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", name, email, role);
    }
}

// 3. Resident Table Component
class ResidentTable extends java.awt.List {
    private List<Resident> residents = new ArrayList<>();

    public ResidentTable() {
        setBackground(new Color(255, 255, 255));
        setForeground(new Color(33, 37, 41));
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    public void addResident(Resident resident) {
        residents.add(resident);
        updateDisplayList(residents);
    }

    public void removeResident(int index) {
        if (index >= 0 && index < residents.size()) {
            residents.remove(index);
            updateDisplayList(residents);
        }
    }

    public void updateDisplayList(List<Resident> listToDisplay) {
        this.removeAll();
        for (Resident r : listToDisplay) {
            this.add(r.toString());
        }
    }

    public Resident getResident(int index) {
        return (index >= 0 && index < residents.size()) ? residents.get(index) : null;
    }

    public List<Resident> getAllResidents() { return residents; }
}

// 3. Main UI
public class BarangayInfoSys extends Frame {
    private ResidentTable residentListUI;
    private TextField firstNameField, lastNameField, middleNameField, addressField, contactField, birthDateField, searchField, occupationField, emailField, emergencyNameField, emergencyNumberField;
    private Choice genderChoice, civilStatusChoice;
    private Button addButton, removeButton, searchButton, refreshButton, clearButton;
    private Button certificationsButton, complaintButton, financeButton, announcementButton, userMgmtButton;
    private Label statusLabel, titleLabel;
    private Panel mainPanel;

    // Data stores for additional modules
    private java.util.List<Certificate> certificates = new ArrayList<>();
    private java.util.List<Complaint> complaints = new ArrayList<>();
    private java.util.List<PaymentRecord> payments = new ArrayList<>();
    private java.util.List<Announcement> announcements = new ArrayList<>();
    private java.util.List<SystemUser> systemUsers = new ArrayList<>();
    private SystemUser currentUser;

    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);    // Blue
    private final Color SECONDARY_COLOR = new Color(46, 204, 113);  // Green
    private final Color ACCENT_COLOR = new Color(230, 126, 34);     // Orange
    private final Color DANGER_COLOR = new Color(231, 76, 60);      // Red
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Light gray
    private final Color CARD_COLOR = new Color(255, 255, 255);      // White
    private final Color TEXT_COLOR = new Color(33, 37, 41);         // Dark gray

    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 11);

    public BarangayInfoSys() {
        this(new SystemUser("Admin", "admin@barangay.local", "Administrator", "ADMIN12345"));
    }

    public BarangayInfoSys(SystemUser user) {
        this.currentUser = user;
        this.systemUsers.add(user);
        initializeUI();
        setupEventHandlers();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("🏘️ Barangay San Lorenzo Information System");
        setSize(1000, 700);
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(15, 15));
        setResizable(true);

        // Title Panel
        Panel titlePanel = createTitlePanel();

        // Input Form Panel
        Panel inputPanel = createInputPanel();

        // List Panel
        Panel listPanel = createListPanel();

        // Control Panel
        Panel controlPanel = createControlPanel();

        // Status Panel
        Panel statusPanel = createStatusPanel();

        // Main container with padding
        mainPanel = new Panel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Bottom container
        Panel bottomPanel = new Panel(new BorderLayout());
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(listPanel, BorderLayout.CENTER);
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private Panel createTitlePanel() {
        GlossyHeaderPanel panel = new GlossyHeaderPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setPreferredSize(new Dimension(1000, 70));

        titleLabel = new Label("Barangay San Lorenza Resident Information System - User: " + currentUser.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignment(Label.CENTER);

        panel.add(titleLabel);
        panel.setPreferredSize(new Dimension(1000, 70));
        return panel;
    }

    private class GlossyHeaderPanel extends Panel {
        public GlossyHeaderPanel() {
            setBackground(PRIMARY_COLOR);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR.brighter(), 0, h, PRIMARY_COLOR.darker());
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, w, h, 30, 30);

            g2.setColor(new Color(255, 255, 255, 90));
            g2.fillOval(w - 180, 8, 140, 50);

            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(15, 12, w / 2, h / 2, 30, 30);

            super.paint(g);
        }

        @Override
        public void update(Graphics g) {
            paint(g);
        }
    }

    private Panel createInputPanel() {
        Panel panel = new Panel(new GridBagLayout());
        panel.setBackground(CARD_COLOR);
        panel.setPreferredSize(new Dimension(1000, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Name fields
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createStyledLabel("👤 First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = createStyledTextField(15);
        panel.add(firstNameField, gbc);

        gbc.gridx = 2;
        panel.add(createStyledLabel("Middle Name:"), gbc);
        gbc.gridx = 3;
        middleNameField = createStyledTextField(15);
        panel.add(middleNameField, gbc);

        gbc.gridx = 4;
        panel.add(createStyledLabel("👤 Last Name:"), gbc);
        gbc.gridx = 5;
        lastNameField = createStyledTextField(15);
        panel.add(lastNameField, gbc);

        // Row 2: Gender and Address
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStyledLabel("⚧ Gender:"), gbc);
        gbc.gridx = 1;
        genderChoice = createStyledChoice();
        genderChoice.add("Male");
        genderChoice.add("Female");
        panel.add(genderChoice, gbc);

        gbc.gridx = 2;
        panel.add(createStyledLabel("📍 Address:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3;
        addressField = createStyledTextField(30);
        panel.add(addressField, gbc);

        // Row 3: Contact and Birth Date
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createStyledLabel("📞 Contact #:"), gbc);
        gbc.gridx = 1;
        contactField = createStyledTextField(15);
        panel.add(contactField, gbc);

        gbc.gridx = 2;
        panel.add(createStyledLabel("🎂 Birth Date:"), gbc);
        gbc.gridx = 3;
        birthDateField = createStyledTextField(15);
        birthDateField.setText("MM/DD/YYYY");
        birthDateField.setForeground(Color.GRAY);
        birthDateField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (birthDateField.getText().equals("MM/DD/YYYY")) {
                    birthDateField.setText("");
                    birthDateField.setForeground(TEXT_COLOR);
                }
            }
            public void focusLost(FocusEvent e) {
                if (birthDateField.getText().isEmpty()) {
                    birthDateField.setText("MM/DD/YYYY");
                    birthDateField.setForeground(Color.GRAY);
                }
            }
        });
        panel.add(birthDateField, gbc);

        gbc.gridx = 4;
        panel.add(createStyledLabel("💍 Civil Status:"), gbc);
        gbc.gridx = 5;
        civilStatusChoice = createStyledChoice();
        civilStatusChoice.add("Single");
        civilStatusChoice.add("Married");
        civilStatusChoice.add("Widowed");
        civilStatusChoice.add("Divorced");
        panel.add(civilStatusChoice, gbc);

        // Row 4: Occupation and Email
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createStyledLabel("💼 Occupation:"), gbc);
        gbc.gridx = 1;
        occupationField = createStyledTextField(15);
        panel.add(occupationField, gbc);

        gbc.gridx = 2;
        panel.add(createStyledLabel("📧 Email:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3;
        emailField = createStyledTextField(30);
        panel.add(emailField, gbc);

        // Row 5: Emergency Contacts
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createStyledLabel("🚨 Emergency Contact:"), gbc);
        gbc.gridx = 1;
        emergencyNameField = createStyledTextField(15);
        panel.add(emergencyNameField, gbc);

        gbc.gridx = 2;
        panel.add(createStyledLabel("📞 Emergency #:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3;
        emergencyNumberField = createStyledTextField(20);
        panel.add(emergencyNumberField, gbc);

        // Add border to panel
        panel.setPreferredSize(new Dimension(950, 180));
        return panel;
    }

    private Panel createListPanel() {
        Panel panel = new Panel(new BorderLayout());
        panel.setBackground(CARD_COLOR);

        Label listTitle = new Label("📋 Resident Records");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        listTitle.setForeground(PRIMARY_COLOR);

        residentListUI = new ResidentTable();
        residentListUI.setPreferredSize(new Dimension(950, 250));

        panel.add(listTitle, BorderLayout.NORTH);
        panel.add(residentListUI, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(950, 280));

        return panel;
    }

    private Panel createControlPanel() {
        Panel panel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);

        // Search section
        Panel searchPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BACKGROUND_COLOR);
        Label searchLabel = createStyledLabel("🔍 Search by Name:");
        searchField = createStyledTextField(20);
        searchButton = createStyledButton("Search", SECONDARY_COLOR);
        refreshButton = createStyledButton("Show All", PRIMARY_COLOR);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // Action buttons
        Panel actionPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(BACKGROUND_COLOR);
        addButton = createStyledButton("➕ Add Resident", SECONDARY_COLOR);
        clearButton = createStyledButton("🧹 Clear Form", ACCENT_COLOR);
        removeButton = createStyledButton("🗑️ Delete Selected", DANGER_COLOR);

        actionPanel.add(addButton);
        actionPanel.add(clearButton);
        actionPanel.add(removeButton);

        // Administrative module buttons (new scope)
        Panel modulePanel = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        modulePanel.setBackground(BACKGROUND_COLOR);
        certificationsButton = createStyledButton("📜 Certificates", PRIMARY_COLOR);
        complaintButton = createStyledButton("🚨 Complaints", ACCENT_COLOR);
        financeButton = createStyledButton("💰 Finance", SECONDARY_COLOR);
        announcementButton = createStyledButton("📢 Announcements", new Color(108, 117, 125));
        userMgmtButton = createStyledButton("👥 Users", new Color(52, 73, 94));

        modulePanel.add(certificationsButton);
        modulePanel.add(complaintButton);
        modulePanel.add(financeButton);
        modulePanel.add(announcementButton);
        modulePanel.add(userMgmtButton);

        panel.add(searchPanel);
        panel.add(actionPanel);
        panel.add(modulePanel);

        return panel;
    }

    private Panel createStatusPanel() {
        Panel panel = new Panel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        statusLabel = new Label("✅ Logged in as " + currentUser.getName() + " - " + currentUser.getRole(), Label.LEFT);
        statusLabel.setFont(labelFont);
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBackground(new Color(240, 240, 240));

        panel.add(statusLabel, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(1000, 30));

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

    private Choice createStyledChoice() {
        Choice choice = new Choice();
        choice.setFont(labelFont);
        choice.setBackground(Color.WHITE);
        choice.setForeground(TEXT_COLOR);
        return choice;
    }

    private Button createStyledButton(String text, Color bgColor) {
        GlowingButton button = new GlowingButton(text, bgColor);
        button.setFont(buttonFont);
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private class GlowingButton extends Button {
        private final Color baseColor;
        private boolean hovered;
        private boolean pressed;

        public GlowingButton(String label, Color baseColor) {
            super(label);
            this.baseColor = baseColor;
            setBackground(baseColor);
            setForeground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    pressed = false;
                    repaint();
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    repaint();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color topColor = hovered ? baseColor.brighter() : baseColor;
            Color bottomColor = hovered ? baseColor : baseColor.darker();
            if (pressed) {
                topColor = topColor.darker();
                bottomColor = bottomColor.darker();
            }
            GradientPaint paint = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            if (hovered) {
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 20, 20);
            }

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(getLabel());
            int textHeight = fm.getAscent();
            g2.drawString(getLabel(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
        }

        @Override
        public void update(Graphics g) {
            paint(g);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            return new Dimension(size.width + 20, size.height + 10);
        }
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> addResident());
        removeButton.addActionListener(e -> removeSelected());
        searchButton.addActionListener(e -> searchResidents());
        refreshButton.addActionListener(e -> refreshList());
        clearButton.addActionListener(e -> clearForm());

        certificationsButton.addActionListener(e -> openCertificateModule());
        complaintButton.addActionListener(e -> openComplaintModule());
        financeButton.addActionListener(e -> openFinancialModule());
        announcementButton.addActionListener(e -> openAnnouncementModule());
        userMgmtButton.addActionListener(e -> openUserManagementModule());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        // Add keyboard shortcuts
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    showHelp();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                    clearForm();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    searchResidents();
                }
            }
        });
        setFocusable(true);
    }

    private void addResident() {
        // Input validation
        if (!validateInputs()) {
            return;
        }

        try {
            Resident res = new Resident(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                middleNameField.getText().trim(),
                addressField.getText().trim(),
                contactField.getText().trim(),
                birthDateField.getText().trim(),
                genderChoice.getSelectedItem(),
                civilStatusChoice.getSelectedItem(),
                occupationField.getText().trim(),
                emailField.getText().trim(),
                emergencyNameField.getText().trim(),
                emergencyNumberField.getText().trim()
            );

            residentListUI.addResident(res);
            clearForm();
            updateStatus("✅ Successfully added resident: " + res.getFullName(), SECONDARY_COLOR);

        } catch (Exception ex) {
            updateStatus("❌ Error adding resident: " + ex.getMessage(), DANGER_COLOR);
        }
    }

    private boolean validateInputs() {
        if (firstNameField.getText().trim().isEmpty()) {
            showError("First name is required!");
            firstNameField.requestFocus();
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            showError("Last name is required!");
            lastNameField.requestFocus();
            return false;
        }

        if (addressField.getText().trim().isEmpty()) {
            showError("Address is required!");
            addressField.requestFocus();
            return false;
        }

        if (contactField.getText().trim().isEmpty()) {
            showError("Contact number is required!");
            contactField.requestFocus();
            return false;
        }

        // Email validation (basic)
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            showError("Please enter a valid email address!");
            emailField.requestFocus();
            return false;
        }

        return true;
    }

    private void removeSelected() {
        int idx = residentListUI.getSelectedIndex();
        if (idx == -1) {
            showError("Please select a resident to delete!");
            return;
        }

        Resident selected = residentListUI.getResident(idx);
        int result = showConfirmDialog("Delete Resident",
            "Are you sure you want to delete " + selected.getFullName() + "?");

        if (result == 0) { // Yes
            residentListUI.removeResident(idx);
            updateStatus("🗑️ Resident deleted successfully", ACCENT_COLOR);
        }
    }

    private void searchResidents() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            showError("Please enter a search term!");
            searchField.requestFocus();
            return;
        }

        List<Resident> results = new ArrayList<>();
        for (Resident r : residentListUI.getAllResidents()) {
            if (r.getFullName().toLowerCase().contains(query.toLowerCase())) {
                results.add(r);
            }
        }

        residentListUI.updateDisplayList(results);
        updateStatus("🔍 Found " + results.size() + " resident(s) matching '" + query + "'", PRIMARY_COLOR);
    }

    private void refreshList() {
        residentListUI.updateDisplayList(residentListUI.getAllResidents());
        updateStatus("📋 Showing all " + residentListUI.getAllResidents().size() + " residents", PRIMARY_COLOR);
    }

    private void clearForm() {
        firstNameField.setText("");
        middleNameField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        contactField.setText("");
        birthDateField.setText("MM/DD/YYYY");
        birthDateField.setForeground(Color.GRAY);
        occupationField.setText("");
        emailField.setText("");
        emergencyNameField.setText("");
        emergencyNumberField.setText("");
        genderChoice.select(0);
        civilStatusChoice.select(0);

        updateStatus("🧹 Form cleared - Ready for new entry", ACCENT_COLOR);
        firstNameField.requestFocus();
    }

    // --- New modules for feature scope ---
    private void openCertificateModule() {
        Frame frame = new Frame("Certificate & Clearance Processing");
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout(8, 8));

        java.awt.List list = new java.awt.List();
        for (Certificate c : certificates) {
            list.add(c.toString());
        }

        Panel control = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        Button add = createStyledButton("➕ Add Certificate", SECONDARY_COLOR);
        Button refresh = createStyledButton("🔄 Refresh", PRIMARY_COLOR);
        Button markIssued = createStyledButton("✅ Mark Issued", ACCENT_COLOR);

        control.add(add);
        control.add(markIssued);
        control.add(refresh);

        add.addActionListener(e -> {
            String residentName = JOptionPane.showInputDialog(frame, "Resident Full Name:");
            if (residentName == null || residentName.trim().isEmpty()) return;
            String type = JOptionPane.showInputDialog(frame, "Certificate Type (e.g., Clearance):");
            String purpose = JOptionPane.showInputDialog(frame, "Purpose:");
            String issueDate = JOptionPane.showInputDialog(frame, "Issue Date (MM/DD/YYYY):");

            Certificate cert = new Certificate(residentName, type, purpose, issueDate);
            certificates.add(cert);
            list.add(cert.toString());
            updateStatus("📜 Certificate added for " + residentName, SECONDARY_COLOR);
        });

        markIssued.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0 || idx >= certificates.size()) {
                showError("Choose one certificate to mark issued.");
                return;
            }
            Certificate c = certificates.get(idx);
            c.setStatus("Issued");
            list.replaceItem(c.toString(), idx);
            updateStatus("✅ Certificate " + c.getId() + " marked issued", SECONDARY_COLOR);
        });

        refresh.addActionListener(e -> {
            list.removeAll();
            for (Certificate c : certificates) list.add(c.toString());
        });

        frame.add(new Label("Certificates & Clearance Processing"), BorderLayout.NORTH);
        frame.add(list, BorderLayout.CENTER);
        frame.add(control, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter(){ public void windowClosing(WindowEvent e){ frame.dispose(); }});
        frame.setVisible(true);
    }

    private void openComplaintModule() {
        Frame frame = new Frame("Complaint Management");
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout(8, 8));

        java.awt.List list = new java.awt.List();
        for (Complaint c : complaints) list.add(c.toString());

        Panel control = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        Button add = createStyledButton("➕ Add Complaint", SECONDARY_COLOR);
        Button updateStatus = createStyledButton("🛠️ Update Status", ACCENT_COLOR);
        Button refresh = createStyledButton("🔄 Refresh", PRIMARY_COLOR);

        control.add(add);
        control.add(updateStatus);
        control.add(refresh);

        add.addActionListener(e -> {
            String residentName = JOptionPane.showInputDialog(frame, "Resident Full Name:");
            if (residentName == null || residentName.trim().isEmpty()) return;
            String subject = JOptionPane.showInputDialog(frame, "Complaint Subject:");
            String details = JOptionPane.showInputDialog(frame, "Complaint Details:");
            String date = JOptionPane.showInputDialog(frame, "Filed Date (MM/DD/YYYY):");

            Complaint complaint = new Complaint(residentName, subject, details, date);
            complaints.add(complaint);
            list.add(complaint.toString());
            updateStatus("🚨 Complaint filed for " + residentName, DANGER_COLOR);
        });

        updateStatus.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0 || idx >= complaints.size()) { showError("Select a complaint to update."); return; }
            String[] options = {"New", "In Progress", "Resolved", "Closed"};
            String state = (String) JOptionPane.showInputDialog(frame, "Choose status:", "Status", JOptionPane.PLAIN_MESSAGE, null, options, complaints.get(idx).getStatus());
            if (state != null) {
                complaints.get(idx).setStatus(state);
                list.replaceItem(complaints.get(idx).toString(), idx);
                updateStatus("🔄 Complaint " + complaints.get(idx).getId() + " status updated", SECONDARY_COLOR);
            }
        });

        refresh.addActionListener(e -> {
            list.removeAll();
            for (Complaint c : complaints) list.add(c.toString());
        });

        frame.add(new Label("Complaint Management and Tracking"), BorderLayout.NORTH);
        frame.add(list, BorderLayout.CENTER);
        frame.add(control, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter(){ public void windowClosing(WindowEvent e){ frame.dispose(); }});
        frame.setVisible(true);
    }

    private void openFinancialModule() {
        Frame frame = new Frame("Financial Management");
        frame.setSize(600, 520);
        frame.setLayout(new BorderLayout(8, 8));

        java.awt.List list = new java.awt.List();
        for (PaymentRecord p : payments) list.add(p.toString());

        Panel control = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        Button add = createStyledButton("➕ Record Payment", SECONDARY_COLOR);
        Button report = createStyledButton("📈 Total & Report", ACCENT_COLOR);
        Button refresh = createStyledButton("🔄 Refresh", PRIMARY_COLOR);

        control.add(add);
        control.add(report);
        control.add(refresh);

        add.addActionListener(e -> {
            String payer = JOptionPane.showInputDialog(frame, "Payer Name:");
            if (payer == null || payer.trim().isEmpty()) return;
            String purpose = JOptionPane.showInputDialog(frame, "Payment Purpose:");
            String amountStr = JOptionPane.showInputDialog(frame, "Amount:");
            try {
                double amount = Double.parseDouble(amountStr);
                String date = JOptionPane.showInputDialog(frame, "Payment Date (MM/DD/YYYY):");
                PaymentRecord payment = new PaymentRecord(payer, purpose, amount, date);
                payments.add(payment);
                list.add(payment.toString());
                updateStatus("💰 Recorded payment by " + payer, SECONDARY_COLOR);
            } catch (NumberFormatException nfe) {
                showError("Invalid amount format!");
            }
        });

        report.addActionListener(e -> {
            double total = 0.0;
            for (PaymentRecord p : payments) total += p.getAmount();
            String totalReport = "Total payments: " + String.format("%.2f", total) + " from " + payments.size() + " records.";
            JOptionPane.showMessageDialog(frame, totalReport, "Financial Summary", JOptionPane.INFORMATION_MESSAGE);
            updateStatus("📊 Generated financial summary", SECONDARY_COLOR);
        });

        refresh.addActionListener(e -> {
            list.removeAll();
            for (PaymentRecord p : payments) list.add(p.toString());
        });

        frame.add(new Label("Financial Management"), BorderLayout.NORTH);
        frame.add(list, BorderLayout.CENTER);
        frame.add(control, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter(){ public void windowClosing(WindowEvent e){ frame.dispose(); }});
        frame.setVisible(true);
    }

    private void openAnnouncementModule() {
        Frame frame = new Frame("Online Announcement Posting");
        frame.setSize(620, 520);
        frame.setLayout(new BorderLayout(8, 8));

        java.awt.List list = new java.awt.List();
        for (Announcement a : announcements) list.add(a.toString());

        Panel control = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        Button add = createStyledButton("➕ Post Announcement", SECONDARY_COLOR);
        Button remove = createStyledButton("🗑️ Remove Selected", DANGER_COLOR);
        Button refresh = createStyledButton("🔄 Refresh", PRIMARY_COLOR);

        control.add(add);
        control.add(remove);
        control.add(refresh);

        add.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(frame, "Announcement Title:");
            if (title == null || title.trim().isEmpty()) return;
            String message = JOptionPane.showInputDialog(frame, "Announcement Message:");
            String date = JOptionPane.showInputDialog(frame, "Post Date (MM/DD/YYYY):");
            Announcement announcement = new Announcement(title, message, date);
            announcements.add(announcement);
            list.add(announcement.toString());
            updateStatus("📢 Announcement posted", SECONDARY_COLOR);
        });

        remove.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0 || idx >= announcements.size()) { showError("Select announcement to remove."); return; }
            announcements.remove(idx);
            list.remove(idx);
            updateStatus("🗑️ Announcement removed", ACCENT_COLOR);
        });

        refresh.addActionListener(e -> {
            list.removeAll();
            for (Announcement a : announcements) list.add(a.toString());
        });

        frame.add(new Label("Announcement Posting"), BorderLayout.NORTH);
        frame.add(list, BorderLayout.CENTER);
        frame.add(control, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter(){ public void windowClosing(WindowEvent e){ frame.dispose(); }});
        frame.setVisible(true);
    }

    private void openUserManagementModule() {
        Frame frame = new Frame("User Management & Security");
        frame.setSize(620, 520);
        frame.setLayout(new BorderLayout(8, 8));

        java.awt.List list = new java.awt.List();
        if (systemUsers.isEmpty()) {
            systemUsers.add(currentUser);
        }
        for (SystemUser user : systemUsers) {
            list.add(user.toString());
        }

        Panel control = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        Button add = createStyledButton("➕ Add User", SECONDARY_COLOR);
        Button remove = createStyledButton("🗑️ Remove Selected", DANGER_COLOR);
        Button refresh = createStyledButton("🔄 Refresh", PRIMARY_COLOR);

        control.add(add);
        control.add(remove);
        control.add(refresh);

        add.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(frame, "Name:");
            String email = JOptionPane.showInputDialog(frame, "Email:");
            String role = JOptionPane.showInputDialog(frame, "Role (Administrator/Staff/Viewer):");
            String barcode = JOptionPane.showInputDialog(frame, "Barcode ID:");
            if (name == null || email == null || role == null || barcode == null || name.trim().isEmpty() || email.trim().isEmpty() || role.trim().isEmpty() || barcode.trim().isEmpty()) {
                showError("All fields are required for user creation.");
                return;
            }
            SystemUser user = new SystemUser(name, email, role, barcode.trim());
            systemUsers.add(user);
            list.add(user.toString());
            updateStatus("👤 User added: " + name, SECONDARY_COLOR);
        });

        remove.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0 || idx >= systemUsers.size()) { showError("Select user to remove."); return; }
            SystemUser removed = systemUsers.remove(idx);
            list.remove(idx);
            updateStatus("🗑️ User removed: " + removed.getName(), ACCENT_COLOR);
        });

        refresh.addActionListener(e -> {
            list.removeAll();
            for (SystemUser user : systemUsers) list.add(user.toString());
        });

        frame.add(new Label("User Management and Role Security"), BorderLayout.NORTH);
        frame.add(list, BorderLayout.CENTER);
        frame.add(control, BorderLayout.SOUTH);
        frame.addWindowListener(new WindowAdapter(){ public void windowClosing(WindowEvent e){ frame.dispose(); }});
        frame.setVisible(true);
    }

    private void confirmExit() {
        if (residentListUI.getAllResidents().size() > 0) {
            int result = showConfirmDialog("Exit Application",
                "You have " + residentListUI.getAllResidents().size() + " resident records. Exit anyway?");
            if (result == 0) { // Yes
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    private void showHelp() {
        String helpText = "Barangay Information System Help:\n\n" +
            "• Ctrl+N: Clear form\n" +
            "• Ctrl+S: Search residents\n" +
            "• F1: Show this help\n\n" +
            "Required fields are marked with *\n" +
            "Use the search box to find residents by name\n" +
            "Select a resident from the list to delete them";

        showInfoDialog("Help", helpText);
    }

    private void showError(String message) {
        updateStatus("❌ " + message, DANGER_COLOR);
        // Also show dialog for critical errors
        showMessageDialog("Error", message, JOptionPane.ERROR_MESSAGE);
    }

    private void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    // Dialog helper methods
    private int showConfirmDialog(String title, String message) {
        return JOptionPane.showConfirmDialog(this, message, title,
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    private void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMessageDialog(String title, String message, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    private static void showLoginScreen() {
        Frame loginFrame = new Frame("Login with Barcode Scanner");
        loginFrame.setSize(420, 220);
        loginFrame.setLayout(new GridBagLayout());
        loginFrame.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Label instruction = new Label("Scan your barcode or enter your email, then press Enter:");
        instruction.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginFrame.add(instruction, gbc);

        TextField barcodeField = new TextField(30);
        barcodeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 1;
        loginFrame.add(barcodeField, gbc);

        Label feedback = new Label(" ");
        feedback.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        feedback.setForeground(Color.RED);
        gbc.gridy = 2;
        loginFrame.add(feedback, gbc);

        Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        Button loginButton = new Button("Login");
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        Button exitButton = new Button("Exit");
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);
        buttons.add(loginButton);
        buttons.add(exitButton);

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginFrame.add(buttons, gbc);

        ActionListener doLogin = e -> {
            String code = barcodeField.getText().trim();
            SystemUser loggedInUser;
            if (code.isEmpty()) {
                loggedInUser = new SystemUser("Guest", "guest@barangay.local", "Guest", "GUEST");
            } else {
                loggedInUser = authenticateBarcode(code);
                if (loggedInUser == null) {
                    loggedInUser = createUserFromInput(code);
                }
            }
            loginFrame.dispose();
            new BarangayInfoSys(loggedInUser);
        };

        barcodeField.addActionListener(doLogin);
        loginButton.addActionListener(doLogin);
        exitButton.addActionListener(e -> System.exit(0));

        loginFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private static SystemUser authenticateBarcode(String code) {
        for (SystemUser user : getLoginUsers()) {
            if (code.equalsIgnoreCase(user.getBarcodeId()) || code.equalsIgnoreCase(user.getEmail())) {
                return user;
            }
        }
        return null;
    }

    private static SystemUser createUserFromInput(String code) {
        String email;
        String name;
        if (code.contains("@")) {
            email = code;
            name = code.split("@", 2)[0];
        } else {
            email = code.toLowerCase() + "@barangay.local";
            name = "Guest";
        }
        name = capitalizeName(name);
        return new SystemUser(name, email, "Guest", code);
    }

    private static String capitalizeName(String input) {
        if (input == null || input.isEmpty()) {
            return "Guest";
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1).toLowerCase();
    }

    private static java.util.List<SystemUser> getLoginUsers() {
        java.util.List<SystemUser> users = new ArrayList<>();
        users.add(new SystemUser("Admin", "admin@barangay.local", "Administrator", "ADMIN12345"));
        users.add(new SystemUser("Staff One", "staff1@barangay.local", "Staff", "STAFF12345"));
        users.add(new SystemUser("Viewer", "viewer@barangay.local", "Viewer", "VIEWER12345"));
        return users;
    }

    public static void main(String[] args) {
        showLoginScreen();
    }
}