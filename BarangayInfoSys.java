import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.Timer;

// 1. Resident Data Model
class Resident {
    private String firstName, lastName, middleName, address, contactNumber, birthDate, gender, civilStatus, occupation, email, emergencyContactName, emergencyContactNumber;
    private int age;
    private String householdId;
    private long id;

    public Resident(String firstName, String lastName, String middleName,
                   String address, String contactNumber, String birthDate, String gender,
                   String civilStatus, String occupation, String email,
                   String emergencyContactName, String emergencyContactNumber, int age, String householdId) {
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
        this.age = age;
        this.householdId = householdId;
        this.id = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMiddleName() { return middleName; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public String getBirthDate() { return birthDate; }
    public String getGender() { return gender; }
    public String getCivilStatus() { return civilStatus; }
    public String getOccupation() { return occupation; }
    public String getEmail() { return email; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactNumber() { return emergencyContactNumber; }
    public int getAge() { return age; }
    public String getHouseholdId() { return householdId; }
    public String getFullName() { return firstName + " " + middleName + " " + lastName; }

    @Override
    public String toString() {
        return String.format("👤 %s | 📍 %s | 📞 %s | %s | 💼 %s | 🎂 Age: %d | 🏠 Household: %s",
                           getFullName(), address, contactNumber, gender, occupation, age, householdId);
    }
}

// 2. Enhanced Data Models
class Certificate {
    private long id;
    private String residentName;
    private String residentId; // Reference to resident ID
    private String type;
    private String purpose;
    private String issueDate;
    private String expiryDate;
    private String status;
    private String issuedBy;
    private String controlNumber;
    private double fee;
    private String notes;

    public Certificate(String residentName, String residentId, String type, String purpose, String issueDate, double fee) {
        this.id = System.currentTimeMillis();
        this.residentName = residentName;
        this.residentId = residentId;
        this.type = type;
        this.purpose = purpose;
        this.issueDate = issueDate;
        this.expiryDate = calculateExpiryDate(issueDate, type);
        this.status = "Pending";
        this.issuedBy = "";
        this.controlNumber = generateControlNumber();
        this.fee = fee;
        this.notes = "";
    }

    private String calculateExpiryDate(String issueDate, String type) {
        // Simple expiry calculation - 1 year for most certificates, 6 months for clearances
        try {
            if (type.toLowerCase().contains("clearance")) {
                return "6 months from issue date";
            } else {
                return "1 year from issue date";
            }
        } catch (Exception e) {
            return "1 year from issue date";
        }
    }

    private String generateControlNumber() {
        return "CERT-" + System.currentTimeMillis();
    }

    // Getters and setters
    public long getId() { return id; }
    public String getResidentName() { return residentName; }
    public String getResidentId() { return residentId; }
    public String getType() { return type; }
    public String getPurpose() { return purpose; }
    public String getIssueDate() { return issueDate; }
    public String getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
    public String getIssuedBy() { return issuedBy; }
    public String getControlNumber() { return controlNumber; }
    public double getFee() { return fee; }
    public String getNotes() { return notes; }

    public void setStatus(String status) { this.status = status; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s) - %s - %s - ₱%.2f",
                           controlNumber, residentName, type, purpose, issueDate, status, fee);
    }
}

class Complaint {
    private long id;
    private String residentName;
    private String residentId;
    private String subject;
    private String details;
    private String category;
    private String priority;
    private String status;
    private String filedDate;
    private String resolvedDate;
    private String assignedTo;
    private String resolution;
    private String contactInfo;
    private java.util.List<String> updates;

    public Complaint(String residentName, String residentId, String subject, String details,
                    String category, String priority, String contactInfo) {
        this.id = System.currentTimeMillis();
        this.residentName = residentName;
        this.residentId = residentId;
        this.subject = subject;
        this.details = details;
        this.category = category;
        this.priority = priority;
        this.contactInfo = contactInfo;
        this.status = "New";
        this.filedDate = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        this.resolvedDate = "";
        this.assignedTo = "";
        this.resolution = "";
        this.updates = new java.util.ArrayList<>();
        addUpdate("Complaint filed");
    }

    public void addUpdate(String update) {
        String timestamp = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date());
        updates.add(timestamp + ": " + update);
    }

    // Getters and setters
    public long getId() { return id; }
    public String getResidentName() { return residentName; }
    public String getResidentId() { return residentId; }
    public String getSubject() { return subject; }
    public String getDetails() { return details; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getFiledDate() { return filedDate; }
    public String getResolvedDate() { return resolvedDate; }
    public String getAssignedTo() { return assignedTo; }
    public String getResolution() { return resolution; }
    public String getContactInfo() { return contactInfo; }
    public java.util.List<String> getUpdates() { return updates; }

    public void setStatus(String status) {
        this.status = status;
        if (status.equals("Resolved") || status.equals("Closed")) {
            this.resolvedDate = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        }
    }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%s) - %s - Priority: %s",
                           id, residentName, subject, category, status, priority);
    }
}

class PaymentRecord {
    private long id;
    private String payer;
    private String payerId;
    private String purpose;
    private String category;
    private double amount;
    private double discount;
    private String paymentMethod;
    private String referenceNumber;
    private String date;
    private String recordedBy;
    private String status;
    private String notes;

    public PaymentRecord(String payer, String payerId, String purpose, String category,
                        double amount, double discount, String paymentMethod, String recordedBy) {
        this.id = System.currentTimeMillis();
        this.payer = payer;
        this.payerId = payerId;
        this.purpose = purpose;
        this.category = category;
        this.amount = amount;
        this.discount = discount;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = generateReferenceNumber();
        this.date = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        this.recordedBy = recordedBy;
        this.status = "Completed";
        this.notes = "";
    }

    private String generateReferenceNumber() {
        return "PAY-" + System.currentTimeMillis();
    }

    // Getters and setters
    public long getId() { return id; }
    public String getPayer() { return payer; }
    public String getPayerId() { return payerId; }
    public String getPurpose() { return purpose; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public double getDiscount() { return discount; }
    public double getNetAmount() { return amount - discount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getDate() { return date; }
    public String getRecordedBy() { return recordedBy; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format("[%s] %s - ₱%.2f (%s) - %s - %s",
                           referenceNumber, payer, getNetAmount(), purpose, paymentMethod, date);
    }
}

class Announcement {
    private long id;
    private String title;
    private String message;
    private String category;
    private String priority;
    private String postDate;
    private String expiryDate;
    private String postedBy;
    private String status;
    private int viewCount;
    private java.util.List<String> targetAudience;

    public Announcement(String title, String message, String category, String priority, String postedBy) {
        this.id = System.currentTimeMillis();
        this.title = title;
        this.message = message;
        this.category = category;
        this.priority = priority;
        this.postDate = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date());
        this.expiryDate = calculateExpiryDate(priority);
        this.postedBy = postedBy;
        this.status = "Active";
        this.viewCount = 0;
        this.targetAudience = new java.util.ArrayList<>();
        targetAudience.add("All Residents");
    }

    private String calculateExpiryDate(String priority) {
        int days = priority.equals("Urgent") ? 7 : priority.equals("Important") ? 30 : 90;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, days);
        return new java.text.SimpleDateFormat("MM/dd/yyyy").format(cal.getTime());
    }

    public void incrementViewCount() {
        viewCount++;
    }

    // Getters and setters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public String getPostDate() { return postDate; }
    public String getExpiryDate() { return expiryDate; }
    public String getPostedBy() { return postedBy; }
    public String getStatus() { return status; }
    public int getViewCount() { return viewCount; }
    public java.util.List<String> getTargetAudience() { return targetAudience; }

    public void setStatus(String status) { this.status = status; }
    public void addTargetAudience(String audience) { targetAudience.add(audience); }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%s) - Posted: %s - Views: %d",
                           id, title, category, priority, postDate, viewCount);
    }
}

class SystemUser {
    private long id;
    private String name;
    private String email;
    private String role;
    private String barcodeId;
    private String department;
    private boolean isActive;
    private String lastLogin;
    private java.util.List<String> permissions;
    private String createdDate;
    private String passwordHash; // For future password authentication

    public SystemUser(String name, String email, String role, String barcodeId) {
        this.id = System.currentTimeMillis();
        this.name = name;
        this.email = email;
        this.role = role;
        this.barcodeId = barcodeId;
        this.department = "General";
        this.isActive = true;
        this.lastLogin = "";
        this.createdDate = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        this.passwordHash = "";
        this.permissions = new java.util.ArrayList<>();
        initializePermissions();
    }

    private void initializePermissions() {
        permissions.clear();
        switch (role.toLowerCase()) {
            case "administrator":
                permissions.addAll(java.util.Arrays.asList(
                    "READ_RESIDENTS", "WRITE_RESIDENTS", "DELETE_RESIDENTS",
                    "READ_CERTIFICATES", "WRITE_CERTIFICATES", "APPROVE_CERTIFICATES",
                    "READ_COMPLAINTS", "WRITE_COMPLAINTS", "RESOLVE_COMPLAINTS",
                    "READ_FINANCE", "WRITE_FINANCE", "GENERATE_REPORTS",
                    "READ_ANNOUNCEMENTS", "WRITE_ANNOUNCEMENTS", "MANAGE_ANNOUNCEMENTS",
                    "MANAGE_USERS", "SYSTEM_SETTINGS", "BACKUP_DATA"
                ));
                break;
            case "staff":
                permissions.addAll(java.util.Arrays.asList(
                    "READ_RESIDENTS", "WRITE_RESIDENTS",
                    "READ_CERTIFICATES", "WRITE_CERTIFICATES",
                    "READ_COMPLAINTS", "WRITE_COMPLAINTS",
                    "READ_FINANCE", "WRITE_FINANCE",
                    "READ_ANNOUNCEMENTS", "WRITE_ANNOUNCEMENTS"
                ));
                break;
            case "viewer":
                permissions.addAll(java.util.Arrays.asList(
                    "READ_RESIDENTS", "READ_CERTIFICATES", "READ_COMPLAINTS",
                    "READ_FINANCE", "READ_ANNOUNCEMENTS"
                ));
                break;
        }
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public void updateLastLogin() {
        this.lastLogin = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date());
    }

    // Getters and setters
    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getBarcodeId() { return barcodeId; }
    public String getDepartment() { return department; }
    public boolean isActive() { return isActive; }
    public String getLastLogin() { return lastLogin; }
    public String getCreatedDate() { return createdDate; }
    public java.util.List<String> getPermissions() { return permissions; }

    public void setDepartment(String department) { this.department = department; }
    public void setActive(boolean active) { this.isActive = active; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s - %s - %s",
                           name, email, role, department, isActive ? "Active" : "Inactive");
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
    private TextField firstNameField, lastNameField, middleNameField, addressField, contactField, birthDateField, searchField, occupationField, emailField, emergencyNameField, emergencyNumberField, ageField, householdIdField;
    private Choice genderChoice, civilStatusChoice;
    private Button addButton, removeButton, searchButton, refreshButton, clearButton, saveButton, saveAndOpenButton;
    private Button certificationsButton, complaintButton, financeButton, announcementButton, userMgmtButton;
    private Label statusLabel, titleLabel;
    private Panel mainPanel;

    private AccessDatabaseManager dbManager;
    private final String databasePath = new File(System.getProperty("user.dir"), "barangay.accdb").getAbsolutePath();

    // Data stores for additional modules
    private java.util.List<Certificate> certificates = new ArrayList<>();
    private java.util.List<Complaint> complaints = new ArrayList<>();
    private java.util.List<PaymentRecord> payments = new ArrayList<>();
    private java.util.List<Announcement> announcements = new ArrayList<>();
    private java.util.List<SystemUser> systemUsers = new ArrayList<>();
    private SystemUser currentUser;

    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(128, 128, 128);    // Gray
    private final Color SECONDARY_COLOR = new Color(192, 192, 192);  // Light Gray
    private final Color ACCENT_COLOR = new Color(64, 64, 64);     // Dark Gray
    private final Color DANGER_COLOR = new Color(96, 96, 96);      // Medium Dark Gray
    private final Color SUCCESS_COLOR = new Color(34, 139, 34);    // Green
    private final Color INFO_COLOR = new Color(70, 130, 180);      // Steel Blue
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Light gray
    private final Color CARD_COLOR = new Color(255, 255, 255);      // White
    private final Color TEXT_COLOR = new Color(33, 37, 41);         // Dark gray

    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 11);

    // Permission checking methods
    private boolean hasPermission(String permission) {
        return currentUser != null && currentUser.hasPermission(permission);
    }

    private void checkPermission(String permission) throws SecurityException {
        if (!hasPermission(permission)) {
            throw new SecurityException("Access denied: Insufficient permissions for " + permission);
        }
    }

    private void showAccessDenied(String feature) {
        JOptionPane.showMessageDialog(this,
            "Access Denied: You don't have permission to access " + feature + ".\n\n" +
            "Your current role: " + (currentUser != null ? currentUser.getRole() : "Unknown") + "\n" +
            "Please contact your administrator for access.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE);
    }

    public BarangayInfoSys() {
        this(new SystemUser("Admin", "admin@barangay.local", "Administrator", "ADMIN12345"));
    }

    public BarangayInfoSys(SystemUser user) {
        this.currentUser = user;
        this.systemUsers.add(user);
        // Update last login
        if (currentUser != null) {
            currentUser.updateLastLogin();
        }
        initializeUI();
        setupDatabase();
        setupEventHandlers();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("🏘️ Barangay San Lorenzo Information System");
        setSize(1000, 700);
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(15, 15));
        setResizable(true);

        // Create menu bar
        setMenuBar(createMenuBar());

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

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> confirmExit());
        fileMenu.add(exitItem);

        // Modules Menu
        Menu modulesMenu = new Menu("Modules");

        MenuItem certificateItem = new MenuItem("📜 Barangay Clearance & Certificate Processing");
        certificateItem.addActionListener(e -> openCertificateModule());
        modulesMenu.add(certificateItem);

        MenuItem complaintItem = new MenuItem("🚨 Complaint Management & Tracking");
        complaintItem.addActionListener(e -> openComplaintModule());
        modulesMenu.add(complaintItem);

        MenuItem financeItem = new MenuItem("💰 Financial Management");
        financeItem.addActionListener(e -> openFinancialModule());
        modulesMenu.add(financeItem);

        MenuItem announcementItem = new MenuItem("📢 Online Announcement Posting");
        announcementItem.addActionListener(e -> openAnnouncementModule());
        modulesMenu.add(announcementItem);

        MenuItem userItem = new MenuItem("👥 User Management & Security");
        userItem.addActionListener(e -> openUserManagementModule());
        modulesMenu.add(userItem);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.addActionListener(e -> showHelp());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(modulesMenu);
        menuBar.add(helpMenu);

        return menuBar;
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

        // Row 6: Age and Household ID
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createStyledLabel("🎂 Age:"), gbc);
        gbc.gridx = 1;
        ageField = createStyledTextField(5);
        panel.add(ageField, gbc);

        gbc.gridx = 2;
        panel.add(createStyledLabel("🏠 Household ID:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3;
        householdIdField = createStyledTextField(15);
        panel.add(householdIdField, gbc);

        // Add border to panel
        panel.setPreferredSize(new Dimension(950, 220));
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
        saveButton = createStyledButton("💾 Save to Access", new Color(108, 117, 125));

        actionPanel.add(addButton);
        actionPanel.add(clearButton);
        actionPanel.add(removeButton);
        actionPanel.add(saveButton);

        saveAndOpenButton = createStyledButton("💾 Save + Open Access", new Color(52, 73, 94));
        actionPanel.add(saveAndOpenButton);

        // Administrative module buttons (new scope)
        Panel modulePanel = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        modulePanel.setBackground(BACKGROUND_COLOR);
        certificationsButton = createStyledButton("📜 Clearance & Certificates", PRIMARY_COLOR);
        complaintButton = createStyledButton("🚨 Complaint Management", ACCENT_COLOR);
        financeButton = createStyledButton("💰 Financial Management", SECONDARY_COLOR);
        announcementButton = createStyledButton("📢 Announcements", new Color(108, 117, 125));
        userMgmtButton = createStyledButton("👥 User Management", new Color(52, 73, 94));

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
        saveButton.addActionListener(e -> saveResidentsToAccess());
        saveAndOpenButton.addActionListener(e -> saveAndOpenAccessDatabase());
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

    private void setupDatabase() {
        try {
            dbManager = new AccessDatabaseManager(databasePath);
            dbManager.initialize();
            updateStatus("✅ Connected to Access DB: " + databasePath, SECONDARY_COLOR);
        } catch (Exception ex) {
            updateStatus("❌ Access DB init failed: " + ex.getMessage(), DANGER_COLOR);
        }
    }

    private void saveResidentsToAccess() {
        if (dbManager == null) {
            showError("Database manager is not initialized.");
            return;
        }

        try {
            dbManager.saveAllResidents(residentListUI.getAllResidents());
            updateStatus("💾 Saved " + residentListUI.getAllResidents().size() + " residents to Access DB", SECONDARY_COLOR);
        } catch (Exception ex) {
            updateStatus("❌ Save to Access failed: " + ex.getMessage(), DANGER_COLOR);
            showError("Unable to save residents to Access: " + ex.getMessage());
        }
    }

    private void saveAndOpenAccessDatabase() {
        saveResidentsToAccess();
        openAccessDatabase();
    }

    private void openAccessDatabase() {
        try {
            File dbFile = new File(databasePath);
            if (!dbFile.exists()) {
                showError("Access file not found: " + databasePath);
                return;
            }
            if (!Desktop.isDesktopSupported()) {
                showError("Desktop open is not supported on this platform.");
                return;
            }
            Desktop.getDesktop().open(dbFile);
            updateStatus("📂 Opened Access DB: " + dbFile.getName(), SECONDARY_COLOR);
        } catch (Exception ex) {
            updateStatus("❌ Could not open Access DB: " + ex.getMessage(), DANGER_COLOR);
            showError("Unable to open Access database: " + ex.getMessage());
        }
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
                emergencyNumberField.getText().trim(),
                Integer.parseInt(ageField.getText().trim()),
                householdIdField.getText().trim()
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

        // Age validation
        String ageText = ageField.getText().trim();
        if (ageText.isEmpty()) {
            showError("Age is required!");
            ageField.requestFocus();
            return false;
        }
        try {
            int age = Integer.parseInt(ageText);
            if (age < 0 || age > 150) {
                showError("Please enter a valid age (0-150)!");
                ageField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Age must be a valid number!");
            ageField.requestFocus();
            return false;
        }

        // Household ID validation
        if (householdIdField.getText().trim().isEmpty()) {
            showError("Household ID is required!");
            householdIdField.requestFocus();
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
        ageField.setText("");
        householdIdField.setText("");
        genderChoice.select(0);
        civilStatusChoice.select(0);

        updateStatus("🧹 Form cleared - Ready for new entry", ACCENT_COLOR);
        firstNameField.requestFocus();
    }

    // --- New modules for feature scope ---
    private void openCertificateModule() {
        Frame frame = new Frame("📜 Certificate & Clearance Processing - " + currentUser.getName());
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(BACKGROUND_COLOR);

        // Title Panel
        Panel titlePanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(PRIMARY_COLOR);
        Label title = new Label("Certificate & Clearance Processing System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);

        // Certificate List
        Panel listPanel = new Panel(new BorderLayout());
        listPanel.setBackground(CARD_COLOR);
        Label listTitle = new Label("📋 Certificate Records");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        listTitle.setForeground(PRIMARY_COLOR);

        java.awt.List certList = new java.awt.List();
        certList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        certList.setBackground(Color.WHITE);

        refreshCertificateList(certList);

        listPanel.add(listTitle, BorderLayout.NORTH);
        listPanel.add(certList, BorderLayout.CENTER);

        // Control Panel
        Panel controlPanel = new Panel(new GridLayout(2, 3, 8, 8));
        controlPanel.setBackground(BACKGROUND_COLOR);

        Button addCert = createStyledButton("➕ New Certificate", SECONDARY_COLOR);
        Button issueCert = createStyledButton("✅ Issue Certificate", ACCENT_COLOR);
        Button rejectCert = createStyledButton("❌ Reject Certificate", DANGER_COLOR);
        Button viewDetails = createStyledButton("👁️ View Details", PRIMARY_COLOR);
        Button generateReport = createStyledButton("📊 Generate Report", new Color(52, 73, 94));
        Button refreshList = createStyledButton("🔄 Refresh", new Color(108, 117, 125));

        controlPanel.add(addCert);
        controlPanel.add(issueCert);
        controlPanel.add(rejectCert);
        controlPanel.add(viewDetails);
        controlPanel.add(generateReport);
        controlPanel.add(refreshList);

        // Event Handlers
        addCert.addActionListener(e -> showAddCertificateDialog(frame, certList));

        issueCert.addActionListener(e -> issueSelectedCertificate(certList));

        rejectCert.addActionListener(e -> rejectSelectedCertificate(certList));

        viewDetails.addActionListener(e -> viewCertificateDetails(certList));

        generateReport.addActionListener(e -> generateCertificateReport());

        refreshList.addActionListener(e -> refreshCertificateList(certList));

        // Layout
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(listPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){ frame.dispose(); }
        });
        frame.setVisible(true);
    }

    private void showAddCertificateDialog(Frame parent, java.awt.List certList) {
        // Create dialog for certificate details
        Frame dialog = new Frame("Add New Certificate");
        dialog.setSize(500, 400);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Find resident by ID or name
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(createStyledLabel("Resident ID/Name:"), gbc);
        gbc.gridx = 1;
        TextField residentField = createStyledTextField(20);
        dialog.add(residentField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(createStyledLabel("Certificate Type:"), gbc);
        gbc.gridx = 1;
        Choice typeChoice = createStyledChoice();
        typeChoice.add("Barangay Clearance");
        typeChoice.add("Business Permit");
        typeChoice.add("Certificate of Indigency");
        typeChoice.add("Certificate of Residency");
        typeChoice.add("Death Certificate");
        typeChoice.add("Birth Certificate");
        typeChoice.add("Marriage Certificate");
        dialog.add(typeChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(createStyledLabel("Purpose:"), gbc);
        gbc.gridx = 1;
        TextField purposeField = createStyledTextField(20);
        dialog.add(purposeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(createStyledLabel("Fee (₱):"), gbc);
        gbc.gridx = 1;
        TextField feeField = createStyledTextField(10);
        dialog.add(feeField, gbc);

        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        Button saveBtn = createStyledButton("💾 Save Certificate", SECONDARY_COLOR);
        Button cancelBtn = createStyledButton("❌ Cancel", DANGER_COLOR);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String residentInfo = residentField.getText().trim();
                String type = typeChoice.getSelectedItem();
                String purpose = purposeField.getText().trim();
                double fee = Double.parseDouble(feeField.getText().trim());

                if (residentInfo.isEmpty() || purpose.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!");
                    return;
                }

                // Find resident ID from residents list
                String residentId = findResidentId(residentInfo);
                String residentName = residentInfo; // Default to input if not found

                Certificate cert = new Certificate(residentName, residentId, type, purpose,
                    new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date()), fee);
                cert.setIssuedBy(currentUser.getName());

                certificates.add(cert);
                refreshCertificateList(certList);
                updateStatus("📜 Certificate created for " + residentName, SECONDARY_COLOR);

                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid fee amount!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error creating certificate: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){ dialog.dispose(); }
        });
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private String findResidentId(String residentInfo) {
        for (Resident r : residentListUI.getAllResidents()) {
            if (r.getFullName().equalsIgnoreCase(residentInfo) ||
                String.valueOf(r.getId()).equals(residentInfo)) {
                return String.valueOf(r.getId());
            }
        }
        return "Unknown";
    }

    private void issueSelectedCertificate(java.awt.List certList) {
        int idx = certList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a certificate to issue!");
            return;
        }

        Certificate cert = certificates.get(idx);
        if (!cert.getStatus().equals("Pending")) {
            JOptionPane.showMessageDialog(null, "Certificate is already " + cert.getStatus().toLowerCase() + "!");
            return;
        }

        cert.setStatus("Issued");
        cert.setIssuedBy(currentUser.getName());
        refreshCertificateList(certList);
        updateStatus("✅ Certificate " + cert.getControlNumber() + " issued", ACCENT_COLOR);
    }

    private void rejectSelectedCertificate(java.awt.List certList) {
        int idx = certList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a certificate to reject!");
            return;
        }

        Certificate cert = certificates.get(idx);
        if (!cert.getStatus().equals("Pending")) {
            JOptionPane.showMessageDialog(null, "Certificate is already " + cert.getStatus().toLowerCase() + "!");
            return;
        }

        String reason = JOptionPane.showInputDialog(null, "Reason for rejection:");
        if (reason != null && !reason.trim().isEmpty()) {
            cert.setStatus("Rejected");
            cert.setNotes("Rejected: " + reason);
            refreshCertificateList(certList);
            updateStatus("❌ Certificate " + cert.getControlNumber() + " rejected", DANGER_COLOR);
        }
    }

    private void viewCertificateDetails(java.awt.List certList) {
        int idx = certList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a certificate to view!");
            return;
        }

        Certificate cert = certificates.get(idx);
        String details = String.format(
            "Certificate Details:\n\n" +
            "Control Number: %s\n" +
            "Resident: %s (ID: %s)\n" +
            "Type: %s\n" +
            "Purpose: %s\n" +
            "Issue Date: %s\n" +
            "Expiry Date: %s\n" +
            "Status: %s\n" +
            "Fee: ₱%.2f\n" +
            "Issued By: %s\n" +
            "Notes: %s",
            cert.getControlNumber(), cert.getResidentName(), cert.getResidentId(),
            cert.getType(), cert.getPurpose(), cert.getIssueDate(), cert.getExpiryDate(),
            cert.getStatus(), cert.getFee(), cert.getIssuedBy(), cert.getNotes()
        );

        JOptionPane.showMessageDialog(null, details, "Certificate Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateCertificateReport() {
        StringBuilder report = new StringBuilder();
        report.append("CERTIFICATE PROCESSING REPORT\n");
        report.append("Generated: ").append(new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date())).append("\n");
        report.append("Generated by: ").append(currentUser.getName()).append("\n\n");

        int total = certificates.size();
        int pending = 0, issued = 0, rejected = 0;
        double totalFees = 0;

        for (Certificate cert : certificates) {
            switch (cert.getStatus().toLowerCase()) {
                case "pending": pending++; break;
                case "issued": issued++; totalFees += cert.getFee(); break;
                case "rejected": rejected++; break;
            }
        }

        report.append("Summary:\n");
        report.append("Total Certificates: ").append(total).append("\n");
        report.append("Pending: ").append(pending).append("\n");
        report.append("Issued: ").append(issued).append("\n");
        report.append("Rejected: ").append(rejected).append("\n");
        report.append("Total Revenue: ₱").append(String.format("%.2f", totalFees)).append("\n\n");

        report.append("Recent Certificates:\n");
        for (int i = Math.max(0, certificates.size() - 10); i < certificates.size(); i++) {
            Certificate cert = certificates.get(i);
            report.append("- ").append(cert.getControlNumber()).append(" | ")
                  .append(cert.getResidentName()).append(" | ").append(cert.getType())
                  .append(" | ").append(cert.getStatus()).append("\n");
        }

        JOptionPane.showMessageDialog(null, report.toString(), "Certificate Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshCertificateList(java.awt.List certList) {
        certList.removeAll();
        for (Certificate cert : certificates) {
            certList.add(cert.toString());
        }
    }

    private void openComplaintModule() {
        Frame frame = new Frame("🚨 Complaint Management & Tracking - " + currentUser.getName());
        frame.setSize(900, 650);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(BACKGROUND_COLOR);

        // Title Panel
        Panel titlePanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(PRIMARY_COLOR);
        Label title = new Label("Complaint Management & Tracking System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);

        // Complaint List with filtering
        Panel listPanel = new Panel(new BorderLayout());
        listPanel.setBackground(CARD_COLOR);

        Panel filterPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(CARD_COLOR);
        Label filterLabel = createStyledLabel("Filter by Status:");
        Choice statusFilter = createStyledChoice();
        statusFilter.add("All");
        statusFilter.add("New");
        statusFilter.add("In Progress");
        statusFilter.add("Resolved");
        statusFilter.add("Closed");
        Button applyFilter = createStyledButton("🔍 Apply Filter", PRIMARY_COLOR);

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(applyFilter);

        Label listTitle = new Label("📋 Complaint Records");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        listTitle.setForeground(PRIMARY_COLOR);

        java.awt.List complaintList = new java.awt.List();
        complaintList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        complaintList.setBackground(Color.WHITE);

        refreshComplaintList(complaintList);

        listPanel.add(filterPanel, BorderLayout.NORTH);
        Panel centerPanel = new Panel(new BorderLayout());
        centerPanel.add(listTitle, BorderLayout.NORTH);
        centerPanel.add(complaintList, BorderLayout.CENTER);
        listPanel.add(centerPanel, BorderLayout.CENTER);

        // Control Panel
        Panel controlPanel = new Panel(new GridLayout(2, 4, 8, 8));
        controlPanel.setBackground(BACKGROUND_COLOR);

        Button addComplaint = createStyledButton("➕ New Complaint", SECONDARY_COLOR);
        Button updateStatus = createStyledButton("🛠️ Update Status", ACCENT_COLOR);
        Button assignTo = createStyledButton("👤 Assign To", PRIMARY_COLOR);
        Button addUpdate = createStyledButton("📝 Add Update", new Color(52, 73, 94));
        Button viewDetails = createStyledButton("👁️ View Details", new Color(108, 117, 125));
        Button generateReport = createStyledButton("📊 Generate Report", new Color(23, 162, 184));
        Button escalate = createStyledButton("⚠️ Escalate", DANGER_COLOR);
        Button refreshList = createStyledButton("🔄 Refresh", new Color(108, 117, 125));

        controlPanel.add(addComplaint);
        controlPanel.add(updateStatus);
        controlPanel.add(assignTo);
        controlPanel.add(addUpdate);
        controlPanel.add(viewDetails);
        controlPanel.add(generateReport);
        controlPanel.add(escalate);
        controlPanel.add(refreshList);

        // Event Handlers
        addComplaint.addActionListener(e -> showAddComplaintDialog(frame, complaintList));

        updateStatus.addActionListener(e -> updateComplaintStatus(complaintList));

        assignTo.addActionListener(e -> assignComplaintTo(complaintList));

        addUpdate.addActionListener(e -> addComplaintUpdate(complaintList));

        viewDetails.addActionListener(e -> viewComplaintDetails(complaintList));

        generateReport.addActionListener(e -> generateComplaintReport());

        escalate.addActionListener(e -> escalateComplaint(complaintList));

        refreshList.addActionListener(e -> refreshComplaintList(complaintList));

        applyFilter.addActionListener(e -> applyComplaintFilter(complaintList, statusFilter.getSelectedItem()));

        // Layout
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(listPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){ frame.dispose(); }
        });
        frame.setVisible(true);
    }

    private void showAddComplaintDialog(Frame parent, java.awt.List complaintList) {
        Frame dialog = new Frame("File New Complaint");
        dialog.setSize(600, 500);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Resident Information
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(createStyledLabel("Resident ID/Name:"), gbc);
        gbc.gridx = 1;
        TextField residentField = createStyledTextField(20);
        dialog.add(residentField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(createStyledLabel("Contact Info:"), gbc);
        gbc.gridx = 1;
        TextField contactField = createStyledTextField(20);
        dialog.add(contactField, gbc);

        // Complaint Details
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(createStyledLabel("Category:"), gbc);
        gbc.gridx = 1;
        Choice categoryChoice = createStyledChoice();
        categoryChoice.add("Infrastructure");
        categoryChoice.add("Public Safety");
        categoryChoice.add("Environmental");
        categoryChoice.add("Health & Sanitation");
        categoryChoice.add("Noise Complaint");
        categoryChoice.add("Illegal Activity");
        categoryChoice.add("Service Request");
        categoryChoice.add("Other");
        dialog.add(categoryChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(createStyledLabel("Priority:"), gbc);
        gbc.gridx = 1;
        Choice priorityChoice = createStyledChoice();
        priorityChoice.add("Low");
        priorityChoice.add("Medium");
        priorityChoice.add("High");
        priorityChoice.add("Urgent");
        dialog.add(priorityChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(createStyledLabel("Subject:"), gbc);
        gbc.gridx = 1;
        TextField subjectField = createStyledTextField(25);
        dialog.add(subjectField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(createStyledLabel("Details:"), gbc);
        gbc.gridx = 1;
        TextArea detailsArea = new TextArea(4, 25);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dialog.add(detailsArea, gbc);

        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        Button saveBtn = createStyledButton("💾 File Complaint", SECONDARY_COLOR);
        Button cancelBtn = createStyledButton("❌ Cancel", DANGER_COLOR);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String residentInfo = residentField.getText().trim();
                String contact = contactField.getText().trim();
                String category = categoryChoice.getSelectedItem();
                String priority = priorityChoice.getSelectedItem();
                String subject = subjectField.getText().trim();
                String details = detailsArea.getText().trim();

                if (residentInfo.isEmpty() || subject.isEmpty() || details.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!");
                    return;
                }

                String residentId = findResidentId(residentInfo);
                String residentName = residentInfo;

                Complaint complaint = new Complaint(residentName, residentId, subject, details,
                    category, priority, contact);

                complaints.add(complaint);
                refreshComplaintList(complaintList);
                updateStatus("🚨 Complaint filed by " + residentName, DANGER_COLOR);

                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error filing complaint: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){ dialog.dispose(); }
        });
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void updateComplaintStatus(java.awt.List complaintList) {
        int idx = complaintList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a complaint to update!");
            return;
        }

        Complaint complaint = complaints.get(idx);
        String[] options = {"New", "In Progress", "Resolved", "Closed"};

        String newStatus = (String) JOptionPane.showInputDialog(null,
            "Update status for complaint " + complaint.getId() + ":",
            "Update Complaint Status",
            JOptionPane.QUESTION_MESSAGE, null, options, complaint.getStatus());

        if (newStatus != null && !newStatus.equals(complaint.getStatus())) {
            complaint.setStatus(newStatus);
            complaint.addUpdate("Status changed to: " + newStatus + " by " + currentUser.getName());
            refreshComplaintList(complaintList);
            updateStatus("🔄 Complaint " + complaint.getId() + " status updated to " + newStatus, ACCENT_COLOR);
        }
    }

    private void assignComplaintTo(java.awt.List complaintList) {
        int idx = complaintList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a complaint to assign!");
            return;
        }

        Complaint complaint = complaints.get(idx);
        String assignee = JOptionPane.showInputDialog(null,
            "Assign complaint " + complaint.getId() + " to:",
            currentUser.getName());

        if (assignee != null && !assignee.trim().isEmpty()) {
            complaint.setAssignedTo(assignee.trim());
            complaint.addUpdate("Assigned to: " + assignee + " by " + currentUser.getName());
            refreshComplaintList(complaintList);
            updateStatus("👤 Complaint " + complaint.getId() + " assigned to " + assignee, PRIMARY_COLOR);
        }
    }

    private void addComplaintUpdate(java.awt.List complaintList) {
        int idx = complaintList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a complaint to update!");
            return;
        }

        Complaint complaint = complaints.get(idx);
        String update = JOptionPane.showInputDialog(null,
            "Add update to complaint " + complaint.getId() + ":");

        if (update != null && !update.trim().isEmpty()) {
            complaint.addUpdate(update.trim() + " (by " + currentUser.getName() + ")");
            refreshComplaintList(complaintList);
            updateStatus("📝 Update added to complaint " + complaint.getId(), SECONDARY_COLOR);
        }
    }

    private void viewComplaintDetails(java.awt.List complaintList) {
        int idx = complaintList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a complaint to view!");
            return;
        }

        Complaint complaint = complaints.get(idx);
        StringBuilder details = new StringBuilder();
        details.append("Complaint Details:\n\n");
        details.append("ID: ").append(complaint.getId()).append("\n");
        details.append("Resident: ").append(complaint.getResidentName()).append(" (ID: ").append(complaint.getResidentId()).append(")\n");
        details.append("Subject: ").append(complaint.getSubject()).append("\n");
        details.append("Category: ").append(complaint.getCategory()).append("\n");
        details.append("Priority: ").append(complaint.getPriority()).append("\n");
        details.append("Status: ").append(complaint.getStatus()).append("\n");
        details.append("Filed Date: ").append(complaint.getFiledDate()).append("\n");
        details.append("Resolved Date: ").append(complaint.getResolvedDate()).append("\n");
        details.append("Assigned To: ").append(complaint.getAssignedTo()).append("\n");
        details.append("Contact Info: ").append(complaint.getContactInfo()).append("\n\n");
        details.append("Details:\n").append(complaint.getDetails()).append("\n\n");
        details.append("Resolution:\n").append(complaint.getResolution()).append("\n\n");
        details.append("Update History:\n");

        for (String update : complaint.getUpdates()) {
            details.append("- ").append(update).append("\n");
        }

        JOptionPane.showMessageDialog(null, details.toString(), "Complaint Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateComplaintReport() {
        StringBuilder report = new StringBuilder();
        report.append("COMPLAINT MANAGEMENT REPORT\n");
        report.append("Generated: ").append(new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date())).append("\n");
        report.append("Generated by: ").append(currentUser.getName()).append("\n\n");

        int total = complaints.size();
        int newComplaints = 0, inProgress = 0, resolved = 0, closed = 0;

        java.util.Map<String, Integer> categoryCount = new java.util.HashMap<>();
        java.util.Map<String, Integer> priorityCount = new java.util.HashMap<>();

        for (Complaint complaint : complaints) {
            switch (complaint.getStatus().toLowerCase()) {
                case "new": newComplaints++; break;
                case "in progress": inProgress++; break;
                case "resolved": resolved++; break;
                case "closed": closed++; break;
            }

            categoryCount.put(complaint.getCategory(),
                categoryCount.getOrDefault(complaint.getCategory(), 0) + 1);
            priorityCount.put(complaint.getPriority(),
                priorityCount.getOrDefault(complaint.getPriority(), 0) + 1);
        }

        report.append("Summary:\n");
        report.append("Total Complaints: ").append(total).append("\n");
        report.append("New: ").append(newComplaints).append("\n");
        report.append("In Progress: ").append(inProgress).append("\n");
        report.append("Resolved: ").append(resolved).append("\n");
        report.append("Closed: ").append(closed).append("\n\n");

        report.append("By Category:\n");
        for (java.util.Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            report.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        report.append("\n");

        report.append("By Priority:\n");
        for (java.util.Map.Entry<String, Integer> entry : priorityCount.entrySet()) {
            report.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        report.append("\n");

        report.append("Recent Complaints:\n");
        for (int i = Math.max(0, complaints.size() - 10); i < complaints.size(); i++) {
            Complaint complaint = complaints.get(i);
            report.append("- ").append(complaint.getId()).append(" | ")
                  .append(complaint.getSubject()).append(" | ").append(complaint.getCategory())
                  .append(" | ").append(complaint.getStatus()).append("\n");
        }

        JOptionPane.showMessageDialog(null, report.toString(), "Complaint Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void escalateComplaint(java.awt.List complaintList) {
        int idx = complaintList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a complaint to escalate!");
            return;
        }

        Complaint complaint = complaints.get(idx);
        if (complaint.getPriority().equals("Urgent")) {
            JOptionPane.showMessageDialog(null, "Complaint is already at urgent priority!");
            return;
        }

        String reason = JOptionPane.showInputDialog(null, "Reason for escalation:");
        if (reason != null && !reason.trim().isEmpty()) {
            // Update priority to Urgent
            complaint.addUpdate("ESCALATED - " + reason + " (by " + currentUser.getName() + ")");
            updateStatus("⚠️ Complaint " + complaint.getId() + " escalated to urgent priority", DANGER_COLOR);
            refreshComplaintList(complaintList);
        }
    }

    private void applyComplaintFilter(java.awt.List complaintList, String filter) {
        complaintList.removeAll();

        for (Complaint complaint : complaints) {
            if (filter.equals("All") || complaint.getStatus().equalsIgnoreCase(filter)) {
                complaintList.add(complaint.toString());
            }
        }

        updateStatus("🔍 Filtered complaints by status: " + filter, PRIMARY_COLOR);
    }

    private void refreshComplaintList(java.awt.List complaintList) {
        complaintList.removeAll();
        for (Complaint complaint : complaints) {
            complaintList.add(complaint.toString());
        }
    }

    private void openFinancialModule() {
        Frame frame = new Frame("💰 Financial Management - " + currentUser.getName());
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(BACKGROUND_COLOR);

        // Title Panel
        Panel titlePanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(PRIMARY_COLOR);
        Label title = new Label("Financial Management & Reporting System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);

        // Financial Overview Panel
        Panel overviewPanel = new Panel(new GridLayout(1, 4, 10, 0));
        overviewPanel.setBackground(CARD_COLOR);

        // Calculate financial metrics
        double totalIncome = 0, totalExpenses = 0, netIncome = 0;
        int totalTransactions = payments.size();

        for (PaymentRecord payment : payments) {
            if (payment.getCategory().toLowerCase().contains("income") ||
                payment.getCategory().toLowerCase().contains("fee") ||
                payment.getCategory().toLowerCase().contains("service")) {
                totalIncome += payment.getNetAmount();
            } else {
                totalExpenses += payment.getNetAmount();
            }
        }
        netIncome = totalIncome - totalExpenses;

        // Overview Cards
        overviewPanel.add(createFinancialCard("💰 Total Income", String.format("₱%.2f", totalIncome), new Color(40, 167, 69)));
        overviewPanel.add(createFinancialCard("💸 Total Expenses", String.format("₱%.2f", totalExpenses), new Color(220, 53, 69)));
        overviewPanel.add(createFinancialCard("📊 Net Income", String.format("₱%.2f", netIncome), netIncome >= 0 ? new Color(23, 162, 184) : DANGER_COLOR));
        overviewPanel.add(createFinancialCard("📋 Transactions", String.valueOf(totalTransactions), PRIMARY_COLOR));

        // Main Content Panel
        Panel contentPanel = new Panel(new BorderLayout());

        // Filter Panel
        Panel filterPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(CARD_COLOR);
        Label filterLabel = createStyledLabel("Filter by Category:");
        Choice categoryFilter = createStyledChoice();
        categoryFilter.add("All");
        categoryFilter.add("Certificate Fees");
        categoryFilter.add("Clearance Fees");
        categoryFilter.add("Business Permit");
        categoryFilter.add("Service Fees");
        categoryFilter.add("Other Income");
        categoryFilter.add("Expenses");
        Button applyFilter = createStyledButton("🔍 Apply Filter", PRIMARY_COLOR);

        filterPanel.add(filterLabel);
        filterPanel.add(categoryFilter);
        filterPanel.add(applyFilter);

        // Payment Records List
        Panel listPanel = new Panel(new BorderLayout());
        listPanel.setBackground(CARD_COLOR);
        Label listTitle = new Label("📋 Payment Records");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        listTitle.setForeground(PRIMARY_COLOR);

        java.awt.List paymentList = new java.awt.List();
        paymentList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        paymentList.setBackground(Color.WHITE);

        refreshPaymentList(paymentList);

        listPanel.add(listTitle, BorderLayout.NORTH);
        listPanel.add(paymentList, BorderLayout.CENTER);

        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(listPanel, BorderLayout.CENTER);

        // Control Panel
        Panel controlPanel = new Panel(new GridLayout(2, 4, 8, 8));
        controlPanel.setBackground(BACKGROUND_COLOR);

        Button recordPayment = createStyledButton("➕ Record Payment", SECONDARY_COLOR);
        Button editPayment = createStyledButton("✏️ Edit Payment", ACCENT_COLOR);
        Button voidPayment = createStyledButton("🚫 Void Payment", DANGER_COLOR);
        Button viewReceipt = createStyledButton("🧾 View Receipt", PRIMARY_COLOR);
        Button monthlyReport = createStyledButton("📊 Monthly Report", new Color(52, 73, 94));
        Button annualReport = createStyledButton("📈 Annual Report", new Color(23, 162, 184));
        Button exportData = createStyledButton("💾 Export Data", new Color(108, 117, 125));
        Button refreshList = createStyledButton("🔄 Refresh", new Color(108, 117, 125));

        controlPanel.add(recordPayment);
        controlPanel.add(editPayment);
        controlPanel.add(voidPayment);
        controlPanel.add(viewReceipt);
        controlPanel.add(monthlyReport);
        controlPanel.add(annualReport);
        controlPanel.add(exportData);
        controlPanel.add(refreshList);

        // Event Handlers
        recordPayment.addActionListener(e -> showRecordPaymentDialog(frame, paymentList));

        editPayment.addActionListener(e -> editSelectedPayment(paymentList));

        voidPayment.addActionListener(e -> voidSelectedPayment(paymentList));

        viewReceipt.addActionListener(e -> viewPaymentReceipt(paymentList));

        monthlyReport.addActionListener(e -> generateMonthlyReport());

        annualReport.addActionListener(e -> generateAnnualReport());

        exportData.addActionListener(e -> exportFinancialData());

        refreshList.addActionListener(e -> refreshPaymentList(paymentList));

        applyFilter.addActionListener(e -> applyPaymentFilter(paymentList, categoryFilter.getSelectedItem()));

        // Layout
        Panel mainPanel = new Panel(new BorderLayout());
        mainPanel.add(overviewPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){ frame.dispose(); }
        });
        frame.setVisible(true);
    }

    private Panel createFinancialCard(String title, String value, Color color) {
        Panel card = new Panel(new BorderLayout());
        card.setBackground(color);
        card.setPreferredSize(new Dimension(200, 60));

        Label titleLabel = new Label(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignment(Label.CENTER);

        Label valueLabel = new Label(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignment(Label.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void showRecordPaymentDialog(Frame parent, java.awt.List paymentList) {
        Frame dialog = new Frame("Record New Payment");
        dialog.setSize(500, 450);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Payer Information
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(createStyledLabel("Payer ID/Name:"), gbc);
        gbc.gridx = 1;
        TextField payerField = createStyledTextField(20);
        dialog.add(payerField, gbc);

        // Payment Details
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(createStyledLabel("Category:"), gbc);
        gbc.gridx = 1;
        Choice categoryChoice = createStyledChoice();
        categoryChoice.add("Certificate Fees");
        categoryChoice.add("Clearance Fees");
        categoryChoice.add("Business Permit");
        categoryChoice.add("Service Fees");
        categoryChoice.add("Other Income");
        categoryChoice.add("Expenses");
        dialog.add(categoryChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(createStyledLabel("Purpose:"), gbc);
        gbc.gridx = 1;
        TextField purposeField = createStyledTextField(20);
        dialog.add(purposeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(createStyledLabel("Amount (₱):"), gbc);
        gbc.gridx = 1;
        TextField amountField = createStyledTextField(10);
        dialog.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(createStyledLabel("Discount (₱):"), gbc);
        gbc.gridx = 1;
        TextField discountField = createStyledTextField(10);
        discountField.setText("0.00");
        dialog.add(discountField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(createStyledLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        Choice methodChoice = createStyledChoice();
        methodChoice.add("Cash");
        methodChoice.add("Check");
        methodChoice.add("Online Transfer");
        methodChoice.add("Credit Card");
        methodChoice.add("Other");
        dialog.add(methodChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        dialog.add(createStyledLabel("Notes:"), gbc);
        gbc.gridx = 1;
        TextField notesField = createStyledTextField(25);
        dialog.add(notesField, gbc);

        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        Button saveBtn = createStyledButton("💾 Record Payment", SECONDARY_COLOR);
        Button cancelBtn = createStyledButton("❌ Cancel", DANGER_COLOR);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String payerInfo = payerField.getText().trim();
                String category = categoryChoice.getSelectedItem();
                String purpose = purposeField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                double discount = Double.parseDouble(discountField.getText().trim());
                String method = methodChoice.getSelectedItem();
                String notes = notesField.getText().trim();

                if (payerInfo.isEmpty() || purpose.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!");
                    return;
                }

                String payerId = findResidentId(payerInfo);
                String payerName = payerInfo;

                PaymentRecord payment = new PaymentRecord(payerName, payerId, purpose, category,
                    amount, discount, method, currentUser.getName());
                payment.setNotes(notes);

                payments.add(payment);
                refreshPaymentList(paymentList);
                updateStatus("💰 Payment recorded: ₱" + String.format("%.2f", payment.getNetAmount()) + " from " + payerName, SECONDARY_COLOR);

                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numeric amounts!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error recording payment: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){ dialog.dispose(); }
        });
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void editSelectedPayment(java.awt.List paymentList) {
        int idx = paymentList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a payment to edit!");
            return;
        }

        PaymentRecord payment = payments.get(idx);
        if (!payment.getStatus().equals("Completed")) {
            JOptionPane.showMessageDialog(null, "Cannot edit a voided payment!");
            return;
        }

        // Simple edit dialog - in a real system, this would be more comprehensive
        String newNotes = JOptionPane.showInputDialog(null,
            "Edit notes for payment " + payment.getReferenceNumber() + ":",
            payment.getNotes());

        if (newNotes != null) {
            payment.setNotes(newNotes);
            refreshPaymentList(paymentList);
            updateStatus("✏️ Payment notes updated", ACCENT_COLOR);
        }
    }

    private void voidSelectedPayment(java.awt.List paymentList) {
        int idx = paymentList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a payment to void!");
            return;
        }

        PaymentRecord payment = payments.get(idx);
        if (!payment.getStatus().equals("Completed")) {
            JOptionPane.showMessageDialog(null, "Payment is already voided!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to void payment " + payment.getReferenceNumber() + "?\n" +
            "Amount: ₱" + String.format("%.2f", payment.getNetAmount()) + "\n" +
            "Payer: " + payment.getPayer(),
            "Confirm Void Payment",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            payment.setStatus("Voided");
            payment.setNotes(payment.getNotes() + " [VOIDED by " + currentUser.getName() + " on " +
                new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date()) + "]");
            refreshPaymentList(paymentList);
            updateStatus("🚫 Payment " + payment.getReferenceNumber() + " voided", DANGER_COLOR);
        }
    }

    private void viewPaymentReceipt(java.awt.List paymentList) {
        int idx = paymentList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(null, "Please select a payment to view receipt!");
            return;
        }

        PaymentRecord payment = payments.get(idx);
        String receipt = String.format(
            "PAYMENT RECEIPT\n" +
            "================\n\n" +
            "Reference Number: %s\n" +
            "Date: %s\n" +
            "Payer: %s (ID: %s)\n" +
            "Category: %s\n" +
            "Purpose: %s\n" +
            "Amount: ₱%.2f\n" +
            "Discount: ₱%.2f\n" +
            "Net Amount: ₱%.2f\n" +
            "Payment Method: %s\n" +
            "Status: %s\n" +
            "Recorded by: %s\n" +
            "Notes: %s\n\n" +
            "Thank you for your payment!",
            payment.getReferenceNumber(), payment.getDate(), payment.getPayer(),
            payment.getPayerId(), payment.getCategory(), payment.getPurpose(),
            payment.getAmount(), payment.getDiscount(), payment.getNetAmount(),
            payment.getPaymentMethod(), payment.getStatus(), payment.getRecordedBy(),
            payment.getNotes()
        );

        JOptionPane.showMessageDialog(null, receipt, "Payment Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateMonthlyReport() {
        // Get current month
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int currentMonth = cal.get(java.util.Calendar.MONTH) + 1;
        int currentYear = cal.get(java.util.Calendar.YEAR);

        double monthlyIncome = 0, monthlyExpenses = 0;
        int transactionCount = 0;

        for (PaymentRecord payment : payments) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                java.util.Date paymentDate = sdf.parse(payment.getDate());
                java.util.Calendar paymentCal = java.util.Calendar.getInstance();
                paymentCal.setTime(paymentDate);

                if (paymentCal.get(java.util.Calendar.MONTH) + 1 == currentMonth &&
                    paymentCal.get(java.util.Calendar.YEAR) == currentYear) {

                    transactionCount++;
                    if (payment.getCategory().toLowerCase().contains("income") ||
                        payment.getCategory().toLowerCase().contains("fee") ||
                        payment.getCategory().toLowerCase().contains("service")) {
                        monthlyIncome += payment.getNetAmount();
                    } else {
                        monthlyExpenses += payment.getNetAmount();
                    }
                }
            } catch (Exception e) {
                // Skip invalid dates
            }
        }

        String report = String.format(
            "MONTHLY FINANCIAL REPORT\n" +
            "Period: %02d/%d\n" +
            "Generated: %s\n\n" +
            "INCOME: ₱%.2f\n" +
            "EXPENSES: ₱%.2f\n" +
            "NET INCOME: ₱%.2f\n" +
            "TRANSACTIONS: %d\n\n" +
            "Generated by: %s",
            currentMonth, currentYear,
            new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date()),
            monthlyIncome, monthlyExpenses, (monthlyIncome - monthlyExpenses), transactionCount,
            currentUser.getName()
        );

        JOptionPane.showMessageDialog(null, report, "Monthly Financial Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateAnnualReport() {
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        double annualIncome = 0, annualExpenses = 0;
        int transactionCount = 0;
        java.util.Map<String, Double> monthlyIncome = new java.util.HashMap<>();
        java.util.Map<String, Double> monthlyExpenses = new java.util.HashMap<>();

        for (PaymentRecord payment : payments) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                java.util.Date paymentDate = sdf.parse(payment.getDate());
                java.util.Calendar paymentCal = java.util.Calendar.getInstance();
                paymentCal.setTime(paymentDate);

                if (paymentCal.get(java.util.Calendar.YEAR) == currentYear) {
                    transactionCount++;
                    String monthKey = String.format("%02d/%d",
                        paymentCal.get(java.util.Calendar.MONTH) + 1, currentYear);

                    if (payment.getCategory().toLowerCase().contains("income") ||
                        payment.getCategory().toLowerCase().contains("fee") ||
                        payment.getCategory().toLowerCase().contains("service")) {
                        annualIncome += payment.getNetAmount();
                        monthlyIncome.put(monthKey,
                            monthlyIncome.getOrDefault(monthKey, 0.0) + payment.getNetAmount());
                    } else {
                        annualExpenses += payment.getNetAmount();
                        monthlyExpenses.put(monthKey,
                            monthlyExpenses.getOrDefault(monthKey, 0.0) + payment.getNetAmount());
                    }
                }
            } catch (Exception e) {
                // Skip invalid dates
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("ANNUAL FINANCIAL REPORT\n");
        report.append("Year: ").append(currentYear).append("\n");
        report.append("Generated: ").append(new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date())).append("\n");
        report.append("Generated by: ").append(currentUser.getName()).append("\n\n");

        report.append("SUMMARY:\n");
        report.append("Total Income: ₱").append(String.format("%.2f", annualIncome)).append("\n");
        report.append("Total Expenses: ₱").append(String.format("%.2f", annualExpenses)).append("\n");
        report.append("Net Income: ₱").append(String.format("%.2f", (annualIncome - annualExpenses))).append("\n");
        report.append("Total Transactions: ").append(transactionCount).append("\n\n");

        report.append("MONTHLY BREAKDOWN:\n");
        for (int month = 1; month <= 12; month++) {
            String monthKey = String.format("%02d/%d", month, currentYear);
            double income = monthlyIncome.getOrDefault(monthKey, 0.0);
            double expenses = monthlyExpenses.getOrDefault(monthKey, 0.0);
            report.append(String.format("%02d/%d: Income ₱%.2f, Expenses ₱%.2f, Net ₱%.2f\n",
                month, currentYear, income, expenses, (income - expenses)));
        }

        JOptionPane.showMessageDialog(null, report.toString(), "Annual Financial Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportFinancialData() {
        StringBuilder csv = new StringBuilder();
        csv.append("Reference Number,Date,Payer,Payer ID,Category,Purpose,Amount,Discount,Net Amount,Payment Method,Status,Recorded By,Notes\n");

        for (PaymentRecord payment : payments) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%.2f,%.2f,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                payment.getReferenceNumber(), payment.getDate(), payment.getPayer(),
                payment.getPayerId(), payment.getCategory(), payment.getPurpose(),
                payment.getAmount(), payment.getDiscount(), payment.getNetAmount(),
                payment.getPaymentMethod(), payment.getStatus(), payment.getRecordedBy(),
                payment.getNotes()));
        }

        JOptionPane.showMessageDialog(null,
            "Financial data exported to CSV format:\n\n" + csv.toString().substring(0, Math.min(500, csv.length())) + "...",
            "Financial Data Export",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void applyPaymentFilter(java.awt.List paymentList, String filter) {
        paymentList.removeAll();

        for (PaymentRecord payment : payments) {
            boolean matches = filter.equals("All") ||
                (filter.equals("Certificate Fees") && payment.getCategory().contains("Certificate")) ||
                (filter.equals("Clearance Fees") && payment.getCategory().contains("Clearance")) ||
                (filter.equals("Business Permit") && payment.getCategory().contains("Business")) ||
                (filter.equals("Service Fees") && payment.getCategory().contains("Service")) ||
                (filter.equals("Other Income") && payment.getCategory().contains("Other")) ||
                (filter.equals("Expenses") && !payment.getCategory().toLowerCase().contains("fee") &&
                 !payment.getCategory().toLowerCase().contains("income") &&
                 !payment.getCategory().toLowerCase().contains("service"));

            if (matches) {
                paymentList.add(payment.toString());
            }
        }

        updateStatus("🔍 Filtered payments by category: " + filter, PRIMARY_COLOR);
    }

    private void refreshPaymentList(java.awt.List paymentList) {
        paymentList.removeAll();
        for (PaymentRecord payment : payments) {
            paymentList.add(payment.toString());
        }
    }

    private void openAnnouncementModule() {
        if (!hasPermission("ANNOUNCEMENT_MANAGEMENT")) {
            showError("Access denied. Insufficient permissions for announcement management.");
            return;
        }

        Frame frame = new Frame("Online Announcement Posting & Management");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

        // Create main panels
        Panel topPanel = new Panel(new BorderLayout());
        Panel listPanel = new Panel(new BorderLayout());
        Panel controlPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Header with title and stats
        Label headerLabel = new Label("📢 Announcement Management System", Label.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(PRIMARY_COLOR);

        Panel statsPanel = new Panel(new GridLayout(1, 4, 5, 5));
        Label totalLabel = new Label("Total: " + announcements.size());
        Label activeLabel = new Label("Active: " + announcements.stream().filter(a -> "Active".equals(a.getStatus())).count());
        Label urgentLabel = new Label("Urgent: " + announcements.stream().filter(a -> "Urgent".equals(a.getPriority())).count());
        Label viewsLabel = new Label("Total Views: " + announcements.stream().mapToInt(Announcement::getViewCount).sum());

        statsPanel.add(totalLabel);
        statsPanel.add(activeLabel);
        statsPanel.add(urgentLabel);
        statsPanel.add(viewsLabel);

        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // Announcement list with details
        java.awt.List announcementList = new java.awt.List();
        updateAnnouncementList(announcementList);

        // Control buttons
        Button postBtn = createStyledButton("📝 Post New Announcement", PRIMARY_COLOR);
        Button editBtn = createStyledButton("✏️ Edit Selected", SECONDARY_COLOR);
        Button archiveBtn = createStyledButton("📁 Archive Selected", ACCENT_COLOR);
        Button deleteBtn = createStyledButton("🗑️ Delete Selected", DANGER_COLOR);
        Button refreshBtn = createStyledButton("🔄 Refresh", SUCCESS_COLOR);
        Button viewBtn = createStyledButton("👁️ View Details", INFO_COLOR);

        controlPanel.add(postBtn);
        controlPanel.add(editBtn);
        controlPanel.add(archiveBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(refreshBtn);
        controlPanel.add(viewBtn);

        // Button actions
        postBtn.addActionListener(e -> postNewAnnouncement(frame, announcementList));
        editBtn.addActionListener(e -> editSelectedAnnouncement(frame, announcementList));
        archiveBtn.addActionListener(e -> archiveSelectedAnnouncement(announcementList));
        deleteBtn.addActionListener(e -> deleteSelectedAnnouncement(announcementList));
        refreshBtn.addActionListener(e -> updateAnnouncementList(announcementList));
        viewBtn.addActionListener(e -> viewAnnouncementDetails(frame, announcementList));

        // Layout assembly
        listPanel.add(new Label("Announcements:"), BorderLayout.NORTH);
        listPanel.add(announcementList, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(listPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { frame.dispose(); }
        });
        frame.setVisible(true);
    }

    private void postNewAnnouncement(Frame parent, java.awt.List list) {
        Dialog dialog = new Dialog(parent, "Post New Announcement", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        TextField titleField = new TextField(30);
        TextArea messageArea = new TextArea(5, 30);
        Choice categoryChoice = new Choice();
        categoryChoice.add("General");
        categoryChoice.add("Emergency");
        categoryChoice.add("Event");
        categoryChoice.add("Health");
        categoryChoice.add("Services");
        categoryChoice.add("Community");

        Choice priorityChoice = new Choice();
        priorityChoice.add("Normal");
        priorityChoice.add("Important");
        priorityChoice.add("Urgent");

        CheckboxGroup audienceGroup = new CheckboxGroup();
        Checkbox allResidents = new Checkbox("All Residents", audienceGroup, true);
        Checkbox barangayOfficials = new Checkbox("Barangay Officials", audienceGroup, false);
        Checkbox seniorCitizens = new Checkbox("Senior Citizens", audienceGroup, false);
        Checkbox youth = new Checkbox("Youth", audienceGroup, false);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new Label("Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHEAST;
        dialog.add(new Label("Message:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        dialog.add(messageArea, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        dialog.add(new Label("Category:"), gbc);
        gbc.gridx = 1;
        dialog.add(categoryChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new Label("Priority:"), gbc);
        gbc.gridx = 1;
        dialog.add(priorityChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new Label("Target Audience:"), gbc);
        Panel audiencePanel = new Panel(new GridLayout(2, 2));
        audiencePanel.add(allResidents);
        audiencePanel.add(barangayOfficials);
        audiencePanel.add(seniorCitizens);
        audiencePanel.add(youth);
        gbc.gridx = 1;
        dialog.add(audiencePanel, gbc);

        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout());
        Button postButton = createStyledButton("📢 Post Announcement", PRIMARY_COLOR);
        Button cancelButton = createStyledButton("❌ Cancel", DANGER_COLOR);
        buttonPanel.add(postButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(buttonPanel, gbc);

        // Button actions
        postButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String message = messageArea.getText().trim();
            String category = categoryChoice.getSelectedItem();
            String priority = priorityChoice.getSelectedItem();

            if (title.isEmpty() || message.isEmpty()) {
                showError("Title and message are required.");
                return;
            }

            // Create announcement with full details
            Announcement announcement = new Announcement(title, message, category, priority, currentUser.getName());

            // Set target audience
            announcement.getTargetAudience().clear();
            if (allResidents.getState()) announcement.addTargetAudience("All Residents");
            if (barangayOfficials.getState()) announcement.addTargetAudience("Barangay Officials");
            if (seniorCitizens.getState()) announcement.addTargetAudience("Senior Citizens");
            if (youth.getState()) announcement.addTargetAudience("Youth");

            announcements.add(announcement);
            updateAnnouncementList(list);
            updateStatus("📢 Announcement posted successfully", SUCCESS_COLOR);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }

    private void editSelectedAnnouncement(Frame parent, java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= announcements.size()) {
            showError("Please select an announcement to edit.");
            return;
        }

        Announcement announcement = announcements.get(idx);
        // Implementation for editing would be similar to posting but pre-filled
        showError("Edit functionality coming soon. Use delete and repost for now.");
    }

    private void archiveSelectedAnnouncement(java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= announcements.size()) {
            showError("Please select an announcement to archive.");
            return;
        }

        announcements.get(idx).setStatus("Archived");
        updateAnnouncementList(list);
        updateStatus("📁 Announcement archived", ACCENT_COLOR);
    }

    private void deleteSelectedAnnouncement(java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= announcements.size()) {
            showError("Please select an announcement to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to permanently delete this announcement?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            announcements.remove(idx);
            updateAnnouncementList(list);
            updateStatus("🗑️ Announcement deleted", DANGER_COLOR);
        }
    }

    private void viewAnnouncementDetails(Frame parent, java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= announcements.size()) {
            showError("Please select an announcement to view.");
            return;
        }

        Announcement announcement = announcements.get(idx);

        Dialog dialog = new Dialog(parent, "Announcement Details", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());

        TextArea details = new TextArea();
        details.setEditable(false);
        details.setText(String.format(
            "ID: %d\n" +
            "Title: %s\n" +
            "Category: %s\n" +
            "Priority: %s\n" +
            "Posted By: %s\n" +
            "Post Date: %s\n" +
            "Expiry Date: %s\n" +
            "Status: %s\n" +
            "Views: %d\n" +
            "Target Audience: %s\n\n" +
            "Message:\n%s",
            announcement.getId(),
            announcement.getTitle(),
            announcement.getCategory(),
            announcement.getPriority(),
            announcement.getPostedBy(),
            announcement.getPostDate(),
            announcement.getExpiryDate(),
            announcement.getStatus(),
            announcement.getViewCount(),
            String.join(", ", announcement.getTargetAudience()),
            announcement.getMessage()
        ));

        Button closeBtn = createStyledButton("Close", PRIMARY_COLOR);
        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.add(details, BorderLayout.CENTER);
        dialog.add(closeBtn, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }

    private void updateAnnouncementList(java.awt.List list) {
        list.removeAll();
        for (Announcement a : announcements) {
            String statusIcon = "Active".equals(a.getStatus()) ? "📢" : "📁";
            String priorityIcon = "Urgent".equals(a.getPriority()) ? "🚨" :
                                 "Important".equals(a.getPriority()) ? "⚠️" : "ℹ️";
            list.add(String.format("%s %s %s - %s (%s) - %s",
                statusIcon, priorityIcon, a.getTitle(), a.getCategory(),
                a.getPriority(), a.getPostDate()));
        }
    }

    private void openUserManagementModule() {
        if (!hasPermission("MANAGE_USERS")) {
            showError("Access denied. Insufficient permissions for user management.");
            return;
        }

        Frame frame = new Frame("User Management & Security Control");
        frame.setSize(900, 650);
        frame.setLayout(new BorderLayout(10, 10));

        // Create main panels
        Panel topPanel = new Panel(new BorderLayout());
        Panel listPanel = new Panel(new BorderLayout());
        Panel controlPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Header with title and stats
        Label headerLabel = new Label("👥 User Management & Security Control", Label.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(PRIMARY_COLOR);

        Panel statsPanel = new Panel(new GridLayout(1, 5, 5, 5));
        Label totalUsersLabel = new Label("Total Users: " + systemUsers.size());
        Label activeUsersLabel = new Label("Active: " + systemUsers.stream().filter(SystemUser::isActive).count());
        Label adminsLabel = new Label("Admins: " + systemUsers.stream().filter(u -> "Administrator".equals(u.getRole())).count());
        Label staffLabel = new Label("Staff: " + systemUsers.stream().filter(u -> "Staff".equals(u.getRole())).count());
        Label viewersLabel = new Label("Viewers: " + systemUsers.stream().filter(u -> "Viewer".equals(u.getRole())).count());

        statsPanel.add(totalUsersLabel);
        statsPanel.add(activeUsersLabel);
        statsPanel.add(adminsLabel);
        statsPanel.add(staffLabel);
        statsPanel.add(viewersLabel);

        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // User list with details
        java.awt.List userList = new java.awt.List();
        updateUserList(userList);

        // Control buttons
        Button addUserBtn = createStyledButton("👤 Add New User", PRIMARY_COLOR);
        Button editUserBtn = createStyledButton("✏️ Edit User", SECONDARY_COLOR);
        Button toggleStatusBtn = createStyledButton("🔄 Toggle Status", ACCENT_COLOR);
        Button managePermissionsBtn = createStyledButton("🔐 Manage Permissions", INFO_COLOR);
        Button deleteUserBtn = createStyledButton("🗑️ Delete User", DANGER_COLOR);
        Button refreshBtn = createStyledButton("🔄 Refresh", SUCCESS_COLOR);
        Button viewDetailsBtn = createStyledButton("👁️ View Details", INFO_COLOR);

        controlPanel.add(addUserBtn);
        controlPanel.add(editUserBtn);
        controlPanel.add(toggleStatusBtn);
        controlPanel.add(managePermissionsBtn);
        controlPanel.add(deleteUserBtn);
        controlPanel.add(refreshBtn);
        controlPanel.add(viewDetailsBtn);

        // Button actions
        addUserBtn.addActionListener(e -> addNewUser(frame, userList));
        editUserBtn.addActionListener(e -> editSelectedUser(frame, userList));
        toggleStatusBtn.addActionListener(e -> toggleUserStatus(userList));
        managePermissionsBtn.addActionListener(e -> manageUserPermissions(frame, userList));
        deleteUserBtn.addActionListener(e -> deleteSelectedUser(userList));
        refreshBtn.addActionListener(e -> updateUserList(userList));
        viewDetailsBtn.addActionListener(e -> viewUserDetails(frame, userList));

        // Layout assembly
        listPanel.add(new Label("System Users:"), BorderLayout.NORTH);
        listPanel.add(userList, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(listPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { frame.dispose(); }
        });
        frame.setVisible(true);
    }

    private void addNewUser(Frame parent, java.awt.List list) {
        Dialog dialog = new Dialog(parent, "Add New User", true);
        dialog.setSize(500, 450);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        TextField nameField = new TextField(30);
        TextField emailField = new TextField(30);
        TextField barcodeField = new TextField(30);
        Choice roleChoice = new Choice();
        roleChoice.add("Administrator");
        roleChoice.add("Staff");
        roleChoice.add("Viewer");

        Choice departmentChoice = new Choice();
        departmentChoice.add("General");
        departmentChoice.add("Administration");
        departmentChoice.add("Finance");
        departmentChoice.add("Services");
        departmentChoice.add("Security");

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new Label("Full Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        dialog.add(new Label("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        dialog.add(new Label("Barcode ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(barcodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new Label("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new Label("Department:"), gbc);
        gbc.gridx = 1;
        dialog.add(departmentChoice, gbc);

        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout());
        Button createButton = createStyledButton("✅ Create User", PRIMARY_COLOR);
        Button cancelButton = createStyledButton("❌ Cancel", DANGER_COLOR);
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(buttonPanel, gbc);

        // Button actions
        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String barcode = barcodeField.getText().trim();
            String role = roleChoice.getSelectedItem();
            String department = departmentChoice.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || barcode.isEmpty()) {
                showError("Name, email, and barcode ID are required.");
                return;
            }

            // Check for duplicate barcode
            boolean duplicateBarcode = systemUsers.stream()
                .anyMatch(u -> barcode.equals(u.getBarcodeId()));
            if (duplicateBarcode) {
                showError("Barcode ID already exists. Please use a unique barcode.");
                return;
            }

            SystemUser newUser = new SystemUser(name, email, role, barcode);
            newUser.setDepartment(department);
            systemUsers.add(newUser);
            updateUserList(list);
            updateStatus("👤 User created successfully: " + name, SUCCESS_COLOR);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }

    private void editSelectedUser(Frame parent, java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= systemUsers.size()) {
            showError("Please select a user to edit.");
            return;
        }

        SystemUser user = systemUsers.get(idx);

        Dialog dialog = new Dialog(parent, "Edit User", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Pre-fill form fields
        TextField nameField = new TextField(user.getName(), 30);
        TextField emailField = new TextField(user.getEmail(), 30);
        TextField barcodeField = new TextField(user.getBarcodeId(), 30);
        Choice roleChoice = new Choice();
        roleChoice.add("Administrator");
        roleChoice.add("Staff");
        roleChoice.add("Viewer");
        roleChoice.select(user.getRole());

        Choice departmentChoice = new Choice();
        departmentChoice.add("General");
        departmentChoice.add("Administration");
        departmentChoice.add("Finance");
        departmentChoice.add("Services");
        departmentChoice.add("Security");
        departmentChoice.select(user.getDepartment());

        // Layout components (similar to add user)
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new Label("Full Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        dialog.add(new Label("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        dialog.add(new Label("Barcode ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(barcodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new Label("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new Label("Department:"), gbc);
        gbc.gridx = 1;
        dialog.add(departmentChoice, gbc);

        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout());
        Button updateButton = createStyledButton("💾 Update User", PRIMARY_COLOR);
        Button cancelButton = createStyledButton("❌ Cancel", DANGER_COLOR);
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(buttonPanel, gbc);

        // Button actions
        updateButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String barcode = barcodeField.getText().trim();
            String role = roleChoice.getSelectedItem();
            String department = departmentChoice.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || barcode.isEmpty()) {
                showError("Name, email, and barcode ID are required.");
                return;
            }

            // Check for duplicate barcode (excluding current user)
            final String finalBarcode = barcode;
            final SystemUser currentUser = user;
            boolean duplicateBarcode = systemUsers.stream()
                .filter(u -> u != currentUser)
                .anyMatch(u -> finalBarcode.equals(u.getBarcodeId()));
            if (duplicateBarcode) {
                showError("Barcode ID already exists. Please use a unique barcode.");
                return;
            }

            SystemUser updatedUser = new SystemUser(name, email, role, barcode);
            updatedUser.setDepartment(department);
            updatedUser.setActive(systemUsers.get(idx).isActive());
            systemUsers.set(idx, updatedUser);
            updateUserList(list);
            updateStatus("✏️ User updated successfully: " + name, SUCCESS_COLOR);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }

    private void toggleUserStatus(java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= systemUsers.size()) {
            showError("Please select a user to toggle status.");
            return;
        }

        SystemUser user = systemUsers.get(idx);
        user.setActive(!user.isActive());
        updateUserList(list);
        String status = user.isActive() ? "activated" : "deactivated";
        updateStatus("🔄 User " + status + ": " + user.getName(), ACCENT_COLOR);
    }

    private void manageUserPermissions(Frame parent, java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= systemUsers.size()) {
            showError("Please select a user to manage permissions.");
            return;
        }

        SystemUser user = systemUsers.get(idx);

        Dialog dialog = new Dialog(parent, "Manage Permissions - " + user.getName(), true);
        dialog.setSize(600, 500);
        dialog.setLayout(new BorderLayout());

        Panel permissionPanel = new Panel(new GridLayout(0, 2, 10, 5));
        java.util.List<Checkbox> checkboxes = new java.util.ArrayList<>();

        String[] allPermissions = {
            "READ_RESIDENTS", "WRITE_RESIDENTS", "DELETE_RESIDENTS",
            "READ_CERTIFICATES", "WRITE_CERTIFICATES", "APPROVE_CERTIFICATES",
            "READ_COMPLAINTS", "WRITE_COMPLAINTS", "RESOLVE_COMPLAINTS",
            "READ_FINANCE", "WRITE_FINANCE", "GENERATE_REPORTS",
            "READ_ANNOUNCEMENTS", "WRITE_ANNOUNCEMENTS", "MANAGE_ANNOUNCEMENTS",
            "MANAGE_USERS", "SYSTEM_SETTINGS", "BACKUP_DATA"
        };

        for (String perm : allPermissions) {
            Checkbox cb = new Checkbox(perm, user.hasPermission(perm));
            permissionPanel.add(cb);
            checkboxes.add(cb);
        }

        Panel buttonPanel = new Panel(new FlowLayout());
        Button saveButton = createStyledButton("💾 Save Permissions", PRIMARY_COLOR);
        Button cancelButton = createStyledButton("❌ Cancel", DANGER_COLOR);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            user.getPermissions().clear();
            for (Checkbox cb : checkboxes) {
                if (cb.getState()) {
                    user.getPermissions().add(cb.getLabel());
                }
            }
            updateStatus("🔐 Permissions updated for: " + user.getName(), SUCCESS_COLOR);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(new Label("Select Permissions for " + user.getName() + " (" + user.getRole() + "):", Label.CENTER), BorderLayout.NORTH);
        dialog.add(permissionPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }

    private void deleteSelectedUser(java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= systemUsers.size()) {
            showError("Please select a user to delete.");
            return;
        }

        SystemUser user = systemUsers.get(idx);

        // Prevent deleting the current user
        if (user == currentUser) {
            showError("Cannot delete your own account.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to permanently delete user '" + user.getName() + "'?\nThis action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            systemUsers.remove(idx);
            updateUserList(list);
            updateStatus("🗑️ User deleted: " + user.getName(), DANGER_COLOR);
        }
    }

    private void viewUserDetails(Frame parent, java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= systemUsers.size()) {
            showError("Please select a user to view details.");
            return;
        }

        SystemUser user = systemUsers.get(idx);

        Dialog dialog = new Dialog(parent, "User Details - " + user.getName(), true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());

        TextArea details = new TextArea();
        details.setEditable(false);
        details.setText(String.format(
            "ID: %d\n" +
            "Name: %s\n" +
            "Email: %s\n" +
            "Role: %s\n" +
            "Department: %s\n" +
            "Barcode ID: %s\n" +
            "Status: %s\n" +
            "Created: %s\n" +
            "Last Login: %s\n\n" +
            "Permissions (%d):\n%s",
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getDepartment(),
            user.getBarcodeId(),
            user.isActive() ? "Active" : "Inactive",
            user.getCreatedDate(),
            user.getLastLogin().isEmpty() ? "Never" : user.getLastLogin(),
            user.getPermissions().size(),
            String.join("\n", user.getPermissions())
        ));

        Button closeBtn = createStyledButton("Close", PRIMARY_COLOR);
        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.add(details, BorderLayout.CENTER);
        dialog.add(closeBtn, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }

    private void updateUserList(java.awt.List list) {
        list.removeAll();
        for (SystemUser user : systemUsers) {
            String statusIcon = user.isActive() ? "🟢" : "🔴";
            String roleIcon = "Administrator".equals(user.getRole()) ? "👑" :
                             "Staff".equals(user.getRole()) ? "👤" : "👁️";
            list.add(String.format("%s %s %s (%s) - %s - %s",
                statusIcon, roleIcon, user.getName(), user.getEmail(),
                user.getRole(), user.getDepartment()));
        }
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

    /**
     * BARCODE LOGIN SYSTEM
     *
     * This system supports barcode authentication for secure access to the Barangay Information System.
     *
     * BARCODE ID FORMAT:
     * - Format: [ROLE][YEAR][SEQUENTIAL] (e.g., ADM2024001, STF2024002)
     * - ADM = Administrator, STF = Staff, VWR = Viewer
     * - 2024 = Year, 001-999 = Sequential ID
     *
     * AUTHENTICATION METHODS:
     * 1. Barcode ID scanning (primary method)
     * 2. Email address entry
     * 3. Username entry (lowercase, no spaces)
     *
     * BARCODE SCANNER INTEGRATION:
     * - Most barcode scanners act as keyboard wedges
     * - Scanned data appears as text input
     * - System automatically detects and processes barcode input
     *
     * SECURITY FEATURES:
     * - Case-insensitive authentication
     * - Guest access for visitors
     * - Role-based access control
     * - Session tracking with user information
     */
    private static void showLoginScreen() {
        Frame loginFrame = new Frame("🏷️ Barangay Information System - Barcode Login");
        loginFrame.setSize(500, 300);
        loginFrame.setLayout(new GridBagLayout());
        loginFrame.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        Label titleLabel = new Label("🔐 Secure Login Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignment(Label.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        loginFrame.add(titleLabel, gbc);

        // Instructions
        Label instruction = new Label("Scan your barcode ID or enter your credentials:");
        instruction.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instruction.setAlignment(Label.CENTER);
        gbc.gridy = 1;
        loginFrame.add(instruction, gbc);

        // Barcode input section
        Panel inputPanel = new Panel(new BorderLayout(5, 5));
        inputPanel.setBackground(new Color(255, 255, 255));

        Label barcodeIcon = new Label("📱");
        barcodeIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        inputPanel.add(barcodeIcon, BorderLayout.WEST);

        TextField barcodeField = new TextField(25);
        barcodeField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        barcodeField.setBackground(new Color(255, 255, 255));
        barcodeField.setForeground(new Color(33, 37, 41));
        inputPanel.add(barcodeField, BorderLayout.CENTER);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginFrame.add(inputPanel, gbc);

        // Scan button
        Button scanButton = new Button("📷 Scan Barcode");
        scanButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        scanButton.setBackground(new Color(0, 123, 255));
        scanButton.setForeground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        loginFrame.add(scanButton, gbc);

        // Status feedback
        Label feedback = new Label("Ready to scan...");
        feedback.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        feedback.setForeground(new Color(0, 123, 255));
        feedback.setAlignment(Label.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginFrame.add(feedback, gbc);

        // User info display
        Label userInfo = new Label(" ");
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        userInfo.setForeground(new Color(108, 117, 125));
        userInfo.setAlignment(Label.CENTER);
        gbc.gridy = 4;
        loginFrame.add(userInfo, gbc);

        // Buttons panel
        Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        Button loginButton = new Button("🔓 Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginButton.setBackground(new Color(40, 167, 69));
        loginButton.setForeground(Color.WHITE);

        Button guestButton = new Button("👤 Continue as Guest");
        guestButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        guestButton.setBackground(new Color(108, 117, 125));
        guestButton.setForeground(Color.WHITE);

        Button exitButton = new Button("❌ Exit");
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        exitButton.setBackground(new Color(220, 53, 69));
        exitButton.setForeground(Color.WHITE);

        buttons.add(loginButton);
        buttons.add(guestButton);
        buttons.add(exitButton);

        gbc.gridy = 5;
        loginFrame.add(buttons, gbc);

        // Enhanced login logic with better feedback
        ActionListener doLogin = e -> {
            String code = barcodeField.getText().trim();
            SystemUser loggedInUser;

            if (code.isEmpty()) {
                feedback.setText("Please enter a barcode ID or email address");
                feedback.setForeground(new Color(220, 53, 69));
                barcodeField.requestFocus();
                return;
            }

            feedback.setText("Authenticating...");
            feedback.setForeground(new Color(255, 193, 7));

            loggedInUser = authenticateBarcode(code);
            if (loggedInUser == null) {
                loggedInUser = createUserFromInput(code);
                feedback.setText("New user created: " + loggedInUser.getName());
                feedback.setForeground(new Color(40, 167, 69));
                userInfo.setText("Welcome, " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");
            } else {
                feedback.setText("Authentication successful!");
                feedback.setForeground(new Color(40, 167, 69));
                userInfo.setText("Welcome back, " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");
            }

            // Brief delay to show feedback
            final SystemUser finalLoggedInUser = loggedInUser;
            Timer timer = new Timer(1000, evt -> {
                loginFrame.dispose();
                new BarangayInfoSys(finalLoggedInUser);
            });
            timer.setRepeats(false);
            timer.start();
        };

        ActionListener doGuestLogin = e -> {
            SystemUser guestUser = new SystemUser("Guest", "guest@barangay.local", "Guest", "GUEST");
            feedback.setText("Continuing as guest...");
            feedback.setForeground(new Color(108, 117, 125));
            userInfo.setText("Welcome, Guest User");

            Timer timer = new Timer(800, evt -> {
                loginFrame.dispose();
                new BarangayInfoSys(guestUser);
            });
            timer.setRepeats(false);
            timer.start();
        };

        // Scan button functionality (simulates barcode scanner)
        scanButton.addActionListener(e -> {
            feedback.setText("Scanner activated - Please scan barcode...");
            feedback.setForeground(new Color(0, 123, 255));
            barcodeField.setText("");
            barcodeField.requestFocus();

            // Simulate scanning after a brief delay
            Timer scanTimer = new Timer(1500, evt -> {
                simulateBarcodeScan(barcodeField, feedback);
            });
            scanTimer.setRepeats(false);
            scanTimer.start();
        });

        barcodeField.addActionListener(doLogin);
        loginButton.addActionListener(doLogin);
        guestButton.addActionListener(doGuestLogin);
        exitButton.addActionListener(e -> System.exit(0));

        loginFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);

        // Auto-focus on barcode field
        loginFrame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                barcodeField.requestFocus();
            }
        });
    }

    /**
     * Simulates barcode scanning for testing purposes.
     * In a real implementation, this would interface with actual barcode scanner hardware.
     */
    private static void simulateBarcodeScan(TextField barcodeField, Label feedback) {
        String[] sampleBarcodes = {
            "ADM2024001", "STF2024002", "STF2024003",
            "VWR2024004", "VWR2024005", "GUEST001"
        };

        // Randomly select a barcode for demonstration
        String randomBarcode = sampleBarcodes[(int)(Math.random() * sampleBarcodes.length)];

        feedback.setText("Barcode scanned: " + randomBarcode);
        feedback.setForeground(new Color(40, 167, 69));
        barcodeField.setText(randomBarcode);

        // Auto-trigger login after scan
        Timer autoLoginTimer = new Timer(500, e -> {
            barcodeField.dispatchEvent(new ActionEvent(barcodeField, ActionEvent.ACTION_PERFORMED, ""));
        });
        autoLoginTimer.setRepeats(false);
        autoLoginTimer.start();
    }

    private static SystemUser authenticateBarcode(String code) {
        for (SystemUser user : getLoginUsers()) {
            if (code.equalsIgnoreCase(user.getBarcodeId()) ||
                code.equalsIgnoreCase(user.getEmail()) ||
                code.equals(user.getName().toLowerCase().replace(" ", ""))) {
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

        // Predefined system users with barcode IDs
        // Barcode IDs are designed to be easily scannable (alphanumeric, fixed length)
        users.add(new SystemUser("Administrator", "admin@barangay.local", "Administrator", "ADM2024001"));
        users.add(new SystemUser("Maria Santos", "maria.santos@barangay.local", "Staff", "STF2024002"));
        users.add(new SystemUser("Juan dela Cruz", "juan.delacruz@barangay.local", "Staff", "STF2024003"));
        users.add(new SystemUser("Ana Reyes", "ana.reyes@barangay.local", "Viewer", "VWR2024004"));
        users.add(new SystemUser("Pedro Garcia", "pedro.garcia@barangay.local", "Viewer", "VWR2024005"));

        return users;
    }

    public static void main(String[] args) {
        showLoginScreen();
    }
}