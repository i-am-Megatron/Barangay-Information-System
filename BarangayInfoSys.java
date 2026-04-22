import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.Timer;
// 0. Barcode Generator (Code 128 subset B)
class BarcodeGenerator {
    // Code 128B encoding: each value maps to 11-bit bar pattern
    private static final int[] CODE128B = {
        0b11011001100, 0b11001101100, 0b11001100110, 0b10010011000, 0b10010001100,
        0b10001001100, 0b10011001000, 0b10011000100, 0b10001100100, 0b11001001000,
        0b11001000100, 0b11000100100, 0b10110011100, 0b10011011100, 0b10011001110,
        0b10111001100, 0b10011101100, 0b10011100110, 0b11001110010, 0b11001011100,
        0b11001001110, 0b11011100100, 0b11001110100, 0b11101101110, 0b11101001100,
        0b11100101100, 0b11100100110, 0b11101100100, 0b11100110100, 0b11100110010,
        0b11011011000, 0b11011000110, 0b11000110110, 0b10100011000, 0b10001011000,
        0b10001000110, 0b10110001000, 0b10001101000, 0b10001100010, 0b11010001000,
        0b11000101000, 0b11000100010, 0b10110111000, 0b10110001110, 0b10001101110,
        0b10111011000, 0b10111000110, 0b10001110110, 0b11101110110, 0b11010001110,
        0b11000101110, 0b11011101000, 0b11011100010, 0b11011101110, 0b11101011000,
        0b11101000110, 0b11100010110, 0b11101101000, 0b11101100010, 0b11100011010,
        0b11101111010, 0b11001000010, 0b11110001010, 0b10100110000, 0b10100001100,
        0b10010110000, 0b10010000110, 0b10000101100, 0b10000100110, 0b10110010000,
        0b10110000100, 0b10011010000, 0b10011000010, 0b10000110100, 0b10000110010,
        0b11000010010, 0b11001010000, 0b11110111010, 0b11000010100, 0b10001111010,
        0b10100111100, 0b10010111100, 0b10010011110, 0b10111100100, 0b10011110100,
        0b10011110010, 0b11110100100, 0b11110010100, 0b11110010010, 0b11011011110,
        0b11011110110, 0b11110110110, 0b10101111000, 0b10100011110, 0b10001011110,
        0b10111101000, 0b10111100010, 0b11110101000, 0b11110100010, 0b10111011110,
        0b10111101110, 0b11101011110, 0b11110101110, 0b11010000100, 0b11010010000,
        0b11010011100, 0b11000111010
    };
    private static final int START_B = 104;
    private static final int STOP   = 106;
    private static final int STOP_PATTERN = 0b11000111010;
    private static final int STOP_TERM    = 0b11;

    public static BufferedImage generate(String text, int barWidth, int height) {
        int[] values = encode(text);
        // count total modules
        int modules = 0;
        for (int v : values) modules += 11;
        modules += 2; // stop terminator
        int imgWidth = modules * barWidth + 20;
        BufferedImage img = new BufferedImage(imgWidth, height + 30, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imgWidth, height + 30);
        g.setColor(Color.BLACK);
        int x = 10;
        for (int vi = 0; vi < values.length; vi++) {
            int pattern = (vi < values.length - 1) ? CODE128B[values[vi]] : STOP_PATTERN;
            for (int bit = 10; bit >= 0; bit--) {
                if (((pattern >> bit) & 1) == 1) g.fillRect(x, 5, barWidth, height);
                x += barWidth;
            }
        }
        // stop terminator (2 modules)
        for (int bit = 1; bit >= 0; bit--) {
            if (((STOP_TERM >> bit) & 1) == 1) g.fillRect(x, 5, barWidth, height);
            x += barWidth;
        }
        // draw text below
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();
        int tx = (imgWidth - fm.stringWidth(text)) / 2;
        g.drawString(text, tx, height + 22);
        g.dispose();
        return img;
    }

    private static int[] encode(String text) {
        if (text == null || text.isEmpty()) text = "EMPTY";
        int[] vals = new int[2 + text.length() + 1];
        vals[0] = START_B;
        int checksum = START_B;
        for (int i = 0; i < text.length(); i++) {
            int v = Math.max(0, Math.min(95, text.charAt(i) - 32));
            vals[1 + i] = v;
            checksum += v * (i + 1);
        }
        vals[1 + text.length()] = checksum % 103;
        vals[2 + text.length()] = STOP;
        return vals;
    }

    public static void showBarcodeWindow(java.awt.Component parent, String data, String label) {
        BufferedImage img = generate(data, 2, 80);
        Frame f = new Frame("Barcode: " + label);
        f.setLayout(new BorderLayout(10, 10));
        f.setBackground(Color.WHITE);

        Canvas canvas = new Canvas() {
            public void paint(Graphics g) {
                g.drawImage(img, 0, 0, this);
            }
            public Dimension getPreferredSize() {
                return new Dimension(img.getWidth(), img.getHeight());
            }
        };

        Label infoLabel = new Label(label, Label.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        Button printBtn = new Button("🖨️ Print");
        Button closeBtn = new Button("Close");
        btnPanel.add(printBtn);
        btnPanel.add(closeBtn);

        printBtn.addActionListener(e -> {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                graphics.drawImage(img, (int) pageFormat.getImageableX(),
                    (int) pageFormat.getImageableY(), null);
                return Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) {
                try { job.print(); } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(f, "Print error: " + ex.getMessage());
                }
            }
        });
        closeBtn.addActionListener(e -> f.dispose());

        f.add(infoLabel, BorderLayout.NORTH);
        f.add(canvas, BorderLayout.CENTER);
        f.add(btnPanel, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(parent);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { f.dispose(); }
        });
        f.setVisible(true);
    }
}

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
    private int reportCount;
    private java.util.List<String> reportReasons;
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
        this.reportCount = 0;
        this.reportReasons = new java.util.ArrayList<>();
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
    public void addReport(String reportReason) {
        reportCount++;
        reportReasons.add(reportReason);
        if ("Active" .equals(status) || "Flagged".equals(status)) {
            this.status = "Flagged";
        }
    }
    public int getReportCount() { return reportCount; }
    public java.util.List<String> getReportReasons() { return reportReasons; }

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

    public SystemUser(long id, String name, String email, String role, String barcodeId,
                      String department, boolean isActive, String lastLogin, String createdDate,
                      String passwordHash, java.util.List<String> permissions) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.barcodeId = barcodeId;
        this.department = department != null ? department : "General";
        this.isActive = isActive;
        this.lastLogin = lastLogin != null ? lastLogin : "";
        this.createdDate = createdDate != null ? createdDate : new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        this.passwordHash = passwordHash != null ? passwordHash : "";
        this.permissions = permissions != null ? new java.util.ArrayList<>(permissions) : new java.util.ArrayList<>();
        if (this.permissions.isEmpty()) {
            initializePermissions();
        }
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
    public String getPasswordHash() { return passwordHash; }
    public java.util.List<String> getPermissions() { return permissions; }

    public void setDepartment(String department) { this.department = department; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; initializePermissions(); }
    public void setBarcodeId(String barcodeId) { this.barcodeId = barcodeId; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setPermissions(java.util.List<String> permissions) {
        this.permissions = new java.util.ArrayList<>();
        if (permissions != null) {
            this.permissions.addAll(permissions);
        }
    }

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
        setBackground(new Color(245, 252, 245)); // FORM_BACKGROUND
        setForeground(new Color(25, 111, 61));   // TEXT_COLOR
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
    private Button addButton, removeButton, searchButton, refreshButton, clearButton, exportExcelButton, barcodeButton;
    private Button certificationsButton, complaintButton, financeButton, announcementButton, userMgmtButton, reportButton;
    private Label statusLabel, titleLabel;
    private Panel mainPanel;

    // Data stores for additional modules
    private java.util.List<Certificate> certificates = new ArrayList<>();
    private java.util.List<Complaint> complaints = new ArrayList<>();
    private java.util.List<PaymentRecord> payments = new ArrayList<>();
    private java.util.List<Announcement> announcements = new ArrayList<>();
    private java.util.List<SystemUser> systemUsers = new ArrayList<>();
    private SQLiteDatabaseManager dbManager;
    private SystemUser currentUser;

    // Barangay Green Theme - Professional and appropriate for government system
    private final Color PRIMARY_COLOR = new Color(34, 139, 34);      // Forest Green
    private final Color SECONDARY_COLOR = new Color(46, 204, 113);   // Emerald Green
    private final Color ACCENT_COLOR = new Color(25, 111, 61);       // Dark Green
    private final Color DANGER_COLOR = new Color(231, 76, 60);       // Modern Red
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);     // Modern Green
    private final Color INFO_COLOR = new Color(52, 152, 219);        // Modern Blue
    private final Color BACKGROUND_COLOR = new Color(240, 248, 240); // Light Green Tint
    private final Color CARD_COLOR = new Color(255, 255, 255);       // White
    private final Color FORM_BACKGROUND = new Color(245, 252, 245);  // Very Light Green for forms
    private final Color TEXT_COLOR = new Color(25, 111, 61);         // Dark Green
    private final Color MODERN_BG_START = new Color(240, 248, 240);  // Light Green
    private final Color MODERN_BG_END = new Color(255, 255, 255);    // White

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

    private SystemUser findUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        for (SystemUser user : systemUsers) {
            if (email.equalsIgnoreCase(user.getEmail())) {
                return user;
            }
        }
        return null;
    }

    public BarangayInfoSys() {
        this(new SystemUser("Admin", "admin@barangay.local", "Administrator", "ADMIN12345"));
    }

    public BarangayInfoSys(SystemUser user) {
        this.dbManager = new SQLiteDatabaseManager(new java.io.File("BarangayInfoSys.db").getAbsolutePath());
        this.systemUsers = dbManager.loadSystemUsers();
        this.currentUser = user;

        if (currentUser != null) {
            currentUser.updateLastLogin();
            if (findUserByEmail(currentUser.getEmail()) == null) {
                systemUsers.add(currentUser);
                dbManager.saveSystemUser(currentUser);
            }
        }

        // If there are no users in the database yet, create the default admin user.
        if (systemUsers.isEmpty()) {
            systemUsers.add(currentUser);
            dbManager.saveSystemUser(currentUser);
        }

        // Add sample announcements for demonstration
        initializeSampleAnnouncements();

        initializeUI();
        setupEventHandlers();
        setVisible(true);
    }

    private void initializeSampleAnnouncements() {
        // Add sample announcements for demonstration
        announcements.add(new Announcement(
            "🏘️ Welcome to The Barangay San Lorenza Information System",
            "We are excited to introduce our new digital information system that will help streamline resident services, certificate processing, and community communication. This system will make it easier for residents to access important information and services.",
            "General", "Important", "System Admin"));

        announcements.add(new Announcement(
            "🚨 COVID-19 Vaccination Schedule Update",
            "The barangay health center will be conducting free COVID-19 booster shots this Saturday from 8:00 AM to 5:00 PM. All eligible residents aged 18 and above are encouraged to participate. Please bring your vaccination card and valid ID.",
            "Health", "Urgent", "Health Committee"));

        announcements.add(new Announcement(
            "🎉 Barangay Fiesta Celebration - March 15-17",
            "Join us for three days of celebration! Highlights include the traditional parade, beauty pageant, fireworks display, and community feast. All residents are welcome to participate in the various activities and contests.",
            "Event", "Important", "Events Committee"));

        announcements.add(new Announcement(
            "💧 Water Interruption Notice",
            "Due to scheduled maintenance, water supply will be interrupted in Zones 3, 5, and 7 from 9:00 AM to 2:00 PM tomorrow. We apologize for the inconvenience and ask residents to store water for essential use during this period.",
            "Services", "Urgent", "Barangay Office"));

        announcements.add(new Announcement(
            "📚 Senior Citizens' Monthly Meeting",
            "The monthly meeting for senior citizens will be held this Friday at 2:00 PM in the barangay hall. Topics include health programs, pension updates, and social activities. Light refreshments will be served.",
            "Community", "Normal", "Senior Citizens Club"));
    }

    private void initializeUI() {
        setTitle("🏘️ Barangay San Lorenza Information System");
        setSize(1200, 700); // Increased width to accommodate sidebar
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

        // Announcements Sidebar Panel
        Panel announcementsPanel = createAnnouncementsPanel();

        // Main container with padding
        mainPanel = new ModernBackgroundPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Bottom container
        Panel bottomPanel = new ModernBackgroundPanel(new BorderLayout());
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(listPanel, BorderLayout.CENTER);
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);

        // Main content area (left side)
        Panel contentPanel = new ModernBackgroundPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.NORTH);
        contentPanel.add(bottomPanel, BorderLayout.CENTER);
        contentPanel.add(statusPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
        add(announcementsPanel, BorderLayout.EAST);
    }

    private Panel createTitlePanel() {
        GlossyHeaderPanel panel = new GlossyHeaderPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setPreferredSize(new Dimension(1200, 70));

        titleLabel = new Label("Barangay San Lorenza Resident Information System - User: " + currentUser.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignment(Label.CENTER);

        panel.add(titleLabel);
        panel.setPreferredSize(new Dimension(1200, 70));
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
        private GradientPaint gradient;
        private int lastWidth, lastHeight;

        public GlossyHeaderPanel() {
            setBackground(PRIMARY_COLOR);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Cache gradient for performance
            if (gradient == null || w != lastWidth || h != lastHeight) {
                gradient = new GradientPaint(0, 0, PRIMARY_COLOR.brighter(), 0, h, PRIMARY_COLOR.darker());
                lastWidth = w;
                lastHeight = h;
            }

            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, w, h, 30, 30);

            // Optimized glow effects
            g2.setColor(new Color(255, 255, 255, 90));
            g2.fillOval(w - 180, 8, 140, 50);

            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(15, 12, w / 2, h / 2, 30, 30);

            super.paint(g);
        }

        @Override
        public void update(Graphics g) {
            // Only repaint if necessary
            if (getGraphics() != null) {
                paint(g);
            }
        }
    }

    private static class ModernLoginPanel extends Panel {
        private GradientPaint gradient;
        private int lastWidth, lastHeight;

        public ModernLoginPanel() {
            super();
            setBackground(new Color(34, 139, 34));
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Cache gradient for performance
            if (gradient == null || w != lastWidth || h != lastHeight) {
                gradient = new GradientPaint(0, 0, new Color(34, 139, 34), 0, h, new Color(25, 111, 61));
                lastWidth = w;
                lastHeight = h;
            }

            g2.setPaint(gradient);
            g2.fillRect(0, 0, w, h);

            // Optimized pattern overlay
            g2.setColor(new Color(255, 255, 255, 20));
            for (int i = 0; i < w; i += 40) {  // Reduced frequency
                for (int j = 0; j < h; j += 40) {
                    if ((i + j) % 80 == 0) {
                        g2.fillOval(i, j, 2, 2);
                    }
                }
            }

            // Simplified geometric shapes
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(w - 200, 50, 150, 100, 20, 20);
            g2.fillRoundRect(50, h - 150, 120, 80, 15, 15);

            super.paint(g);
        }

        @Override
        public void update(Graphics g) {
            // Only repaint if necessary
            if (getGraphics() != null) {
                paint(g);
            }
        }
    }

    private static class GlassyButton extends Button {
        private Color baseColor;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public GlassyButton(String text, Color baseColor) {
            super(text);
            this.baseColor = baseColor;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Create glassy effect
            Color buttonColor = baseColor;
            if (isPressed) {
                buttonColor = baseColor.darker().darker();
            } else if (isHovered) {
                buttonColor = baseColor.brighter();
            }

            // Main button background with gradient
            GradientPaint gradient = new GradientPaint(0, 0, buttonColor.brighter(), 0, h, buttonColor.darker());
            g2.setPaint(gradient);
            g2.fillRoundRect(2, 2, w - 4, h - 4, 15, 15);

            // Glassy overlay
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRoundRect(2, 2, w - 4, h / 2, 15, 15);

            // Inner highlight
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(3, 3, w - 6, h - 6, 13, 13);

            // Shadow effect
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillRoundRect(0, h - 2, w, 2, 0, 0);

            super.paint(g);
        }

        @Override
        public void update(Graphics g) {
            paint(g);
        }
    }

    private class ModernBackgroundPanel extends Panel {
        private GradientPaint gradient;
        private int lastWidth, lastHeight;

        public ModernBackgroundPanel() {
            super();
            setBackground(MODERN_BG_START);
        }

        public ModernBackgroundPanel(LayoutManager layout) {
            super(layout);
            setBackground(MODERN_BG_START);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Cache gradient for performance
            if (gradient == null || w != lastWidth || h != lastHeight) {
                gradient = new GradientPaint(0, 0, MODERN_BG_START, 0, h, MODERN_BG_END);
                lastWidth = w;
                lastHeight = h;
            }

            g2.setPaint(gradient);
            g2.fillRect(0, 0, w, h);

            // Optimized pattern overlay - reduce frequency for better performance
            g2.setColor(new Color(255, 255, 255, 30));
            for (int i = 0; i < w; i += 25) {  // Increased step size
                for (int j = 0; j < h; j += 25) {  // Increased step size
                    if ((i + j) % 50 == 0) {  // Adjusted pattern
                        g2.fillOval(i, j, 1, 1);  // Smaller dots
                    }
                }
            }

            // Simplified border
            g2.setColor(new Color(149, 165, 166, 50));
            g2.drawRoundRect(5, 5, w - 10, h - 10, 20, 20);

            super.paint(g);
        }

        @Override
        public void update(Graphics g) {
            // Only repaint if necessary
            if (getGraphics() != null) {
                paint(g);
            }
        }
    }

    private Panel createInputPanel() {
        Panel panel = new Panel(new GridBagLayout());
        panel.setBackground(FORM_BACKGROUND);
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
        panel.setBackground(FORM_BACKGROUND);

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
        exportExcelButton = createStyledButton("📊 Export to Excel", new Color(34, 139, 34));

        barcodeButton = createStyledButton("🔲 Generate Barcode", new Color(75, 0, 130));
        actionPanel.add(addButton);
        actionPanel.add(clearButton);
        actionPanel.add(removeButton);
        actionPanel.add(exportExcelButton);
        actionPanel.add(barcodeButton);

        // Administrative module buttons (new scope)
        Panel modulePanel = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        modulePanel.setBackground(BACKGROUND_COLOR);
        certificationsButton = createStyledButton("📜 Clearance & Certificates", PRIMARY_COLOR);
        complaintButton = createStyledButton("🚨 Complaint Management", ACCENT_COLOR);
        financeButton = createStyledButton("💰 Financial Management", SECONDARY_COLOR);
        announcementButton = createStyledButton("📢 Announcements", new Color(108, 117, 125));
        userMgmtButton = createStyledButton("👥 User Management", new Color(52, 73, 94));
        reportButton = createStyledButton("📰 Report News", new Color(255, 140, 0));
        Button barcodeGenModuleButton = createStyledButton("🔲 Barcode Generator", new Color(75, 0, 130));

        modulePanel.add(certificationsButton);
        modulePanel.add(complaintButton);
        modulePanel.add(financeButton);
        modulePanel.add(announcementButton);
        modulePanel.add(userMgmtButton);
        modulePanel.add(reportButton);
        modulePanel.add(barcodeGenModuleButton);

        barcodeGenModuleButton.addActionListener(e -> openBarcodeGeneratorModule());

        panel.add(searchPanel);
        panel.add(actionPanel);
        panel.add(modulePanel);

        return panel;
    }

    private Panel createAnnouncementsPanel() {
        Panel panel = new ModernBackgroundPanel(new BorderLayout());
        panel.setBackground(FORM_BACKGROUND);
        panel.setPreferredSize(new Dimension(300, 700));

        // Header
        Panel headerPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(PRIMARY_COLOR);
        Label headerLabel = new Label("📢 Announcements");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Scrolling announcements area
        ScrollingAnnouncementsPanel scrollPanel = new ScrollingAnnouncementsPanel();
        scrollPanel.setPreferredSize(new Dimension(280, 600));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPanel, BorderLayout.CENTER);

        return panel;
    }

    private Panel createStatusPanel() {
        Panel panel = new Panel(new BorderLayout());
        panel.setBackground(FORM_BACKGROUND);

        statusLabel = new Label("✅ Logged in as " + currentUser.getName() + " - " + currentUser.getRole(), Label.LEFT);
        statusLabel.setFont(labelFont);
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBackground(FORM_BACKGROUND);

        panel.add(statusLabel, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(1200, 30));

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
        field.setBackground(new Color(250, 250, 250)); // Very light gray-green
        field.setForeground(TEXT_COLOR);
        return field;
    }

    private Choice createStyledChoice() {
        Choice choice = new Choice();
        choice.setFont(labelFont);
        choice.setBackground(new Color(250, 250, 250)); // Very light gray-green
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

            int w = getWidth();
            int h = getHeight();

            // Calculate colors once for better performance
            Color topColor = hovered ? baseColor.brighter() : baseColor;
            Color bottomColor = hovered ? baseColor : baseColor.darker();
            if (pressed) {
                topColor = topColor.darker();
                bottomColor = bottomColor.darker();
            }

            // Main gradient background
            GradientPaint paint = new GradientPaint(0, 0, topColor, 0, h, bottomColor);
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, w, h, 20, 20);

            // Glassy highlight effect
            if (hovered || pressed) {
                g2.setColor(new Color(255, 255, 255, pressed ? 40 : 60));
                g2.fillRoundRect(0, 0, w, h / 2, 20, 20);
            }

            // Subtle inner glow
            g2.setColor(new Color(255, 255, 255, 30));
            g2.drawRoundRect(1, 1, w - 3, h - 3, 18, 18);

            // Glass reflection effect
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillRoundRect(2, 2, w - 4, h / 3, 16, 16);

            // Text with better positioning
            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(getLabel());
            int textHeight = fm.getAscent();
            g2.drawString(getLabel(), (w - textWidth) / 2, (h + textHeight) / 2 - 2);
        }

        @Override
        public void update(Graphics g) {
            // Only repaint if necessary to reduce lag
            if (getGraphics() != null) {
                paint(g);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            return new Dimension(size.width + 20, size.height + 10);
        }
    }

    // Facebook-style scrolling announcements panel
    private class ScrollingAnnouncementsPanel extends Panel implements Runnable {
        private java.util.List<Announcement> activeAnnouncements;
        private int currentIndex = 0;
        private Thread scrollThread;
        private boolean scrolling = true;
        private final int SCROLL_DELAY = 5000; // 5 seconds per announcement

        public ScrollingAnnouncementsPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            activeAnnouncements = new java.util.ArrayList<>();

            // Filter active announcements
            updateActiveAnnouncements();

            // Start scrolling thread
            scrollThread = new Thread(this);
            scrollThread.setDaemon(true);
            scrollThread.start();

            // Add mouse listener to pause/resume scrolling
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    scrolling = !scrolling;
                    if (scrolling && scrollThread != null) {
                        synchronized (scrollThread) {
                            scrollThread.notify();
                        }
                    }
                }
            });
        }

        private void updateActiveAnnouncements() {
            activeAnnouncements.clear();
            for (Announcement ann : announcements) {
                if ("Active".equals(ann.getStatus())) {
                    activeAnnouncements.add(ann);
                }
            }
            if (activeAnnouncements.isEmpty()) {
                // Add a default announcement if none are active
                activeAnnouncements.add(new Announcement(
                    "No Active Announcements",
                    "There are currently no active announcements. Check back later for updates from the barangay office.",
                    "System", "Normal", "System"));
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (scrolling) {
                        updateActiveAnnouncements();
                        if (!activeAnnouncements.isEmpty()) {
                            currentIndex = (currentIndex + 1) % activeAnnouncements.size();
                        }
                        repaint();
                    }

                    synchronized (scrollThread) {
                        scrollThread.wait(SCROLL_DELAY);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (activeAnnouncements.isEmpty()) {
                return;
            }

            Announcement currentAnn = activeAnnouncements.get(currentIndex);

            int width = getWidth();
            int height = getHeight();

            // Draw announcement card background
            g2.setColor(CARD_COLOR);
            g2.fillRoundRect(5, 5, width - 10, height - 10, 15, 15);

            // Draw border
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(5, 5, width - 10, height - 10, 15, 15);

            // Draw priority indicator
            Color priorityColor = getPriorityColor(currentAnn.getPriority());
            g2.setColor(priorityColor);
            g2.fillRect(5, 5, width - 10, 8);

            // Draw title
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String title = currentAnn.getTitle();
            if (fm.stringWidth(title) > width - 20) {
                title = title.substring(0, Math.min(title.length(), 30)) + "...";
            }
            g2.drawString(title, 15, 30);

            // Draw category and priority
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(100, 100, 100));
            String categoryInfo = currentAnn.getCategory() + " • " + currentAnn.getPriority();
            g2.drawString(categoryInfo, 15, 45);

            // Draw message (truncated)
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TEXT_COLOR);
            String message = currentAnn.getMessage();
            if (message.length() > 100) {
                message = message.substring(0, 100) + "...";
            }

            // Word wrap the message
            int y = 65;
            int maxWidth = width - 30;
            java.util.StringTokenizer st = new java.util.StringTokenizer(message, " ");
            StringBuilder line = new StringBuilder();

            while (st.hasMoreTokens()) {
                String word = st.nextToken();
                String testLine = line + (line.length() > 0 ? " " : "") + word;
                if (fm.stringWidth(testLine) > maxWidth && line.length() > 0) {
                    g2.drawString(line.toString(), 15, y);
                    y += 15;
                    line = new StringBuilder(word);
                    if (y > height - 40) break; // Don't overflow
                } else {
                    line = new StringBuilder(testLine);
                }
            }
            if (line.length() > 0 && y <= height - 40) {
                g2.drawString(line.toString(), 15, y);
            }

            // Draw timestamp
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 9));
            g2.setColor(new Color(150, 150, 150));
            String timeInfo = currentAnn.getPostDate().split(" ")[0]; // Just the date
            g2.drawString(timeInfo, 15, height - 20);

            // Draw view count
            String views = "👁️ " + currentAnn.getViewCount();
            int viewsWidth = fm.stringWidth(views);
            g2.drawString(views, width - viewsWidth - 15, height - 20);

            // Draw scroll indicator
            g2.setColor(scrolling ? new Color(100, 150, 255) : new Color(150, 150, 150));
            g2.fillOval(width - 25, 15, 8, 8);

            // Draw announcement counter
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String counter = (currentIndex + 1) + "/" + activeAnnouncements.size();
            int counterWidth = fm.stringWidth(counter);
            g2.drawString(counter, (width - counterWidth) / 2, height - 5);
        }

        private Color getPriorityColor(String priority) {
            switch (priority.toLowerCase()) {
                case "urgent": return new Color(220, 53, 69); // Red
                case "important": return new Color(255, 193, 7); // Yellow
                default: return new Color(40, 167, 69); // Green
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(280, 600);
        }
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> addResident());
        removeButton.addActionListener(e -> removeSelected());
        searchButton.addActionListener(e -> searchResidents());
        refreshButton.addActionListener(e -> refreshList());
        clearButton.addActionListener(e -> clearForm());
        exportExcelButton.addActionListener(e -> exportResidentsToExcel());
        certificationsButton.addActionListener(e -> openCertificateModule());

        complaintButton.addActionListener(e -> openComplaintModule());

        financeButton.addActionListener(e -> openFinancialModule());

        announcementButton.addActionListener(e -> openAnnouncementModule());

        userMgmtButton.addActionListener(e -> openUserManagementModule());

        reportButton.addActionListener(e -> openReportNewsModule());
        barcodeButton.addActionListener(e -> generateResidentBarcode());

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
        Button issueCert = createStyledButton("Issue Certificate", ACCENT_COLOR);
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

                if (residentInfo.isEmpty() || purpose.isEmpty() || feeField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!");
                    return;
                }
                if (fee < 0) {
                    JOptionPane.showMessageDialog(dialog, "Fee cannot be negative!");
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
        if (idx < 0 || idx >= certificates.size()) { JOptionPane.showMessageDialog(null, "Please select a certificate to issue!"); return; }

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
        if (idx < 0 || idx >= certificates.size()) { JOptionPane.showMessageDialog(null, "Please select a certificate to reject!"); return; }

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
        if (idx < 0 || idx >= certificates.size()) { JOptionPane.showMessageDialog(null, "Please select a certificate to view!"); return; }

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
        if (idx < 0 || idx >= complaints.size()) { JOptionPane.showMessageDialog(null, "Please select a complaint to update!"); return; }

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
        if (idx < 0 || idx >= complaints.size()) { JOptionPane.showMessageDialog(null, "Please select a complaint to assign!"); return; }

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
        if (idx < 0 || idx >= complaints.size()) { JOptionPane.showMessageDialog(null, "Please select a complaint to update!"); return; }

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
        if (idx < 0 || idx >= complaints.size()) { JOptionPane.showMessageDialog(null, "Please select a complaint to view!"); return; }

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
        if (idx < 0 || idx >= complaints.size()) { JOptionPane.showMessageDialog(null, "Please select a complaint to escalate!"); return; }

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
            if ("Completed".equals(payment.getStatus())) {
                String cat = payment.getCategory().toLowerCase();
                if (cat.contains("fee") || cat.contains("permit") || cat.contains("income") || cat.contains("service")) {
                    totalIncome += payment.getNetAmount();
                } else if (cat.contains("expense")) {
                    totalExpenses += payment.getNetAmount();
                }
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

                if (payerInfo.isEmpty() || purpose.isEmpty() || amountField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!");
                    return;
                }
                if (amount < 0 || discount < 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount and discount cannot be negative!");
                    return;
                }
                if (discount > amount) {
                    JOptionPane.showMessageDialog(dialog, "Discount cannot exceed the amount!");
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
        if (idx < 0 || idx >= payments.size()) { JOptionPane.showMessageDialog(null, "Please select a payment to edit!"); return; }

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
        if (idx < 0 || idx >= payments.size()) { JOptionPane.showMessageDialog(null, "Please select a payment to void!"); return; }

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
        if (idx < 0 || idx >= payments.size()) { JOptionPane.showMessageDialog(null, "Please select a payment to view receipt!"); return; }

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
                    paymentCal.get(java.util.Calendar.YEAR) == currentYear &&
                    "Completed".equals(payment.getStatus())) {
                    transactionCount++;
                    String cat = payment.getCategory().toLowerCase();
                    if (cat.contains("fee") || cat.contains("permit") || cat.contains("income") || cat.contains("service")) {
                        monthlyIncome += payment.getNetAmount();
                    } else if (cat.contains("expense")) {
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

                if (paymentCal.get(java.util.Calendar.YEAR) == currentYear &&
                    "Completed".equals(payment.getStatus())) {
                    transactionCount++;
                    String monthKey = String.format("%02d/%d",
                        paymentCal.get(java.util.Calendar.MONTH) + 1, currentYear);
                    String cat = payment.getCategory().toLowerCase();
                    if (cat.contains("fee") || cat.contains("permit") || cat.contains("income") || cat.contains("service")) {
                        annualIncome += payment.getNetAmount();
                        monthlyIncome.put(monthKey, monthlyIncome.getOrDefault(monthKey, 0.0) + payment.getNetAmount());
                    } else if (cat.contains("expense")) {
                        annualExpenses += payment.getNetAmount();
                        monthlyExpenses.put(monthKey, monthlyExpenses.getOrDefault(monthKey, 0.0) + payment.getNetAmount());
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

    private void openBarcodeGeneratorModule() {
        Frame frame = new Frame("🔲 Barcode Generator");
        frame.setSize(600, 420);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(Color.WHITE);

        // Header
        Panel headerPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(75, 0, 130));
        Label headerLabel = new Label("🔲 Barcode Generator", Label.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Input panel
        Panel inputPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(245, 245, 255));
        Label inputLabel = new Label("Enter text to encode:");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        TextField inputField = new TextField(30);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        Button generateBtn = createStyledButton("Generate", new Color(75, 0, 130));
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputPanel.add(generateBtn);

        // Barcode canvas
        final BufferedImage[] imgHolder = { BarcodeGenerator.generate("SAMPLE", 2, 80) };
        Canvas canvas = new Canvas() {
            public void paint(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                int x = (getWidth() - imgHolder[0].getWidth()) / 2;
                g.drawImage(imgHolder[0], x, 10, this);
            }
            public Dimension getPreferredSize() { return new Dimension(560, 130); }
        };
        canvas.setBackground(Color.WHITE);

        // Buttons
        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        btnPanel.setBackground(Color.WHITE);
        Button printBtn = createStyledButton("🖨️ Print", new Color(52, 73, 94));
        Button closeBtn = createStyledButton("Close", DANGER_COLOR);
        btnPanel.add(printBtn);
        btnPanel.add(closeBtn);

        // Generate action
        ActionListener doGenerate = e -> {
            String text = inputField.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter text to generate a barcode.");
                return;
            }
            // Code 128B supports ASCII 32-126
            for (char c : text.toCharArray()) {
                if (c < 32 || c > 126) {
                    JOptionPane.showMessageDialog(frame, "Only printable ASCII characters (A-Z, 0-9, symbols) are supported.");
                    return;
                }
            }
            imgHolder[0] = BarcodeGenerator.generate(text, 2, 80);
            canvas.repaint();
        };
        generateBtn.addActionListener(doGenerate);
        inputField.addActionListener(doGenerate);

        printBtn.addActionListener(e -> {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                graphics.drawImage(imgHolder[0],
                    (int) pageFormat.getImageableX(),
                    (int) pageFormat.getImageableY(), null);
                return Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) {
                try { job.print(); } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(frame, "Print error: " + ex.getMessage());
                }
            }
        });
        closeBtn.addActionListener(e -> frame.dispose());

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.CENTER);
        Panel centerPanel = new Panel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(canvas, BorderLayout.CENTER);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);
        frame.add(centerPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { frame.dispose(); }
        });
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    private void generateResidentBarcode() {
        int idx = residentListUI.getSelectedIndex();
        if (idx == -1) {
            showError("Please select a resident to generate a barcode!");
            return;
        }
        Resident r = residentListUI.getResident(idx);
        String barcodeData = "RES" + r.getId();
        BarcodeGenerator.showBarcodeWindow(this, barcodeData,
            r.getFullName() + " | HH: " + r.getHouseholdId());
    }

    private void exportResidentsToExcel() {
        List<Resident> residents = residentListUI.getAllResidents();

        if (residents.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "No resident data to export.",
                "Export Residents",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder csv = new StringBuilder();
        // CSV header
        csv.append("ID,First Name,Last Name,Middle Name,Address,Contact Number,Birth Date,Gender,Civil Status,Occupation,Email,Emergency Contact Name,Emergency Contact Number,Age,Household ID\n");

        // Add resident data
        for (Resident resident : residents) {
            csv.append(String.format("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%s\"\n",
                resident.getId(),
                resident.getFirstName(),
                resident.getLastName(),
                resident.getMiddleName(),
                resident.getAddress(),
                resident.getContactNumber(),
                resident.getBirthDate(),
                resident.getGender(),
                resident.getCivilStatus(),
                resident.getOccupation(),
                resident.getEmail(),
                resident.getEmergencyContactName(),
                resident.getEmergencyContactNumber(),
                resident.getAge(),
                resident.getHouseholdId()));
        }

        // Show export summary
        String summary = String.format(
            "Barangay Resident Registration Data Exported\n\n" +
            "Total Residents: %d\n" +
            "Export Format: CSV (Compatible with Excel)\n\n" +
            "CSV Data Preview:\n%s",
            residents.size(),
            csv.toString().substring(0, Math.min(800, csv.length())) + (csv.length() > 800 ? "\n..." : "")
        );

        JOptionPane.showMessageDialog(null,
            summary,
            "Resident Data Export to Excel",
            JOptionPane.INFORMATION_MESSAGE);

        updateStatus("📊 Exported " + residents.size() + " residents to Excel-compatible CSV format", SUCCESS_COLOR);
    }

    private void applyPaymentFilter(java.awt.List paymentList, String filter) {
        paymentList.removeAll();

        for (PaymentRecord payment : payments) {
            String cat = payment.getCategory();
            boolean matches = filter.equals("All") ||
                (filter.equals("Certificate Fees") && cat.contains("Certificate")) ||
                (filter.equals("Clearance Fees") && cat.contains("Clearance")) ||
                (filter.equals("Business Permit") && cat.contains("Business")) ||
                (filter.equals("Service Fees") && cat.contains("Service")) ||
                (filter.equals("Other Income") && cat.contains("Other")) ||
                (filter.equals("Expenses") && cat.contains("Expenses"));
            if (matches) paymentList.add(payment.toString());
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
        Frame frame = new Frame("Online Announcement Posting & Management");
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout(10, 10));

        // Create main panels
        Panel topPanel = new Panel(new BorderLayout());
        Panel listPanel = new Panel(new BorderLayout());
        Panel controlPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Header with title and stats
        Label headerLabel = new Label("📢 Announcement Management System", Label.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
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
        Button reportBtn = createStyledButton("🚩 Report Selected", new Color(220, 53, 69));
        Button refreshBtn = createStyledButton("🔄 Refresh", SUCCESS_COLOR);
        Button viewBtn = createStyledButton("👁️ View Details", INFO_COLOR);

        controlPanel.add(postBtn);
        controlPanel.add(editBtn);
        controlPanel.add(archiveBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(reportBtn);
        controlPanel.add(refreshBtn);
        controlPanel.add(viewBtn);

        // Button actions
        postBtn.addActionListener(e -> postNewAnnouncement(frame, announcementList));
        editBtn.addActionListener(e -> editSelectedAnnouncement(frame, announcementList));
        archiveBtn.addActionListener(e -> archiveSelectedAnnouncement(announcementList));
        deleteBtn.addActionListener(e -> deleteSelectedAnnouncement(announcementList));
        reportBtn.addActionListener(e -> reportSelectedAnnouncement(frame, announcementList));
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

        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout());
        Button postButton = createStyledButton("📢 Post Announcement", PRIMARY_COLOR);
        Button cancelButton = createStyledButton("❌ Cancel", DANGER_COLOR);
        buttonPanel.add(postButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
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

            // Set default target audience to all residents
            announcement.getTargetAudience().clear();
            announcement.addTargetAudience("All Residents");

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

        Button reportBtn = createStyledButton("🚩 Report", new Color(220, 53, 69));
        reportBtn.addActionListener(e -> reportAnnouncement(dialog, announcement, list));

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(reportBtn);
        buttonPanel.add(closeBtn);

        dialog.add(details, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }

    private void reportSelectedAnnouncement(Frame parent, java.awt.List list) {
        int idx = list.getSelectedIndex();
        if (idx < 0 || idx >= announcements.size()) {
            showError("Please select an announcement to report.");
            return;
        }
        reportAnnouncement(parent, announcements.get(idx), list);
    }

    private void reportAnnouncement(java.awt.Window parent, Announcement announcement, java.awt.List list) {
        Dialog reportDialog = new Dialog(parent, "Report Announcement", Dialog.ModalityType.APPLICATION_MODAL);
        reportDialog.setSize(420, 320);
        reportDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Label reasonLabel = new Label("Reason for report:");
        Choice reasonChoice = new Choice();
        reasonChoice.add("Spam");
        reasonChoice.add("Inappropriate");
        reasonChoice.add("False Information");
        reasonChoice.add("Harassment");
        reasonChoice.add("Other");

        Label noteLabel = new Label("Additional details:");
        TextArea noteArea = new TextArea(5, 30);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        reportDialog.add(reasonLabel, gbc);
        gbc.gridy = 1;
        reportDialog.add(reasonChoice, gbc);

        gbc.gridy = 2;
        reportDialog.add(noteLabel, gbc);
        gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH;
        reportDialog.add(noteArea, gbc);

        Button submitBtn = createStyledButton("Submit", new Color(220, 53, 69));
        Button cancelBtn = createStyledButton("Cancel", DANGER_COLOR);
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        reportDialog.add(buttonPanel, gbc);

        submitBtn.addActionListener(e -> {
            String reason = reasonChoice.getSelectedItem();
            String details = noteArea.getText().trim();
            String finalReason = reason + (details.isEmpty() ? "" : ": " + details);
            announcement.addReport(finalReason);
            updateAnnouncementList(list);
            JOptionPane.showMessageDialog(reportDialog,
                "Thank you. The announcement has been reported and will be reviewed.",
                "Report Submitted", JOptionPane.INFORMATION_MESSAGE);
            reportDialog.dispose();
        });

        cancelBtn.addActionListener(e -> reportDialog.dispose());
        reportDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { reportDialog.dispose(); }
        });
        reportDialog.setVisible(true);
    }

    private void updateAnnouncementList(java.awt.List list) {
        list.removeAll();
        for (Announcement a : announcements) {
            String statusIcon = "Active".equals(a.getStatus()) ? "📢" : "📁";
            String priorityIcon = "Urgent".equals(a.getPriority()) ? "🚨" :
                                 "Important".equals(a.getPriority()) ? "⚠️" : "ℹ️";
            String reportIcon = a.getReportCount() > 0 ? " 🚩" + a.getReportCount() : "";
            list.add(String.format("%s %s %s - %s (%s) - %s%s",
                statusIcon, priorityIcon, a.getTitle(), a.getCategory(),
                a.getPriority(), a.getPostDate(), reportIcon));
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

        Button userBarcodeBtn = createStyledButton("🔲 Show Barcode", new Color(75, 0, 130));
        controlPanel.add(userBarcodeBtn);
        userBarcodeBtn.addActionListener(e -> {
            int idx = userList.getSelectedIndex();
            if (idx < 0 || idx >= systemUsers.size()) {
                showError("Please select a user to show barcode.");
                return;
            }
            SystemUser u = systemUsers.get(idx);
            BarcodeGenerator.showBarcodeWindow(frame, u.getBarcodeId(),
                u.getName() + " | " + u.getRole());
        });

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

    private void openReportNewsModule() {
        Frame frame = new Frame("📰 Report News - " + currentUser.getName());
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout(10, 10));

        // Header
        Panel headerPanel = new Panel(new BorderLayout());
        Label title = new Label("Report News & Community Updates");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(PRIMARY_COLOR);
        headerPanel.add(title, BorderLayout.CENTER);

        // Instructions
        Label instructions = new Label("Use this form to report news, events, or important community updates that should be shared with residents.");
        instructions.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(instructions, BorderLayout.SOUTH);

        // Form panel
        Panel formPanel = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        Label titleLabel = new Label("News Title:");
        TextField newsTitleField = new TextField(40);

        Label categoryLabel = new Label("Category:");
        Choice categoryChoice = new Choice();
        categoryChoice.add("Community Event");
        categoryChoice.add("Public Announcement");
        categoryChoice.add("Emergency Alert");
        categoryChoice.add("Local News");
        categoryChoice.add("Government Update");
        categoryChoice.add("Other");

        Label priorityLabel = new Label("Priority:");
        Choice priorityChoice = new Choice();
        priorityChoice.add("Normal");
        priorityChoice.add("Important");
        priorityChoice.add("Urgent");

        Label descriptionLabel = new Label("Description/Details:");
        TextArea descriptionArea = new TextArea(8, 40);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));

        Label locationLabel = new Label("Location (if applicable):");
        TextField locationField = new TextField(40);

        Label contactLabel = new Label("Contact Information:");
        TextField contactField = new TextField(40);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(titleLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(newsTitleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(categoryChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(priorityLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(priorityChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(descriptionLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(descriptionArea, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(locationLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(contactLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(contactField, gbc);

        // Button panel
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        Button submitButton = createStyledButton("📤 Submit News Report", PRIMARY_COLOR);
        Button clearButton = createStyledButton("🧹 Clear Form", ACCENT_COLOR);
        Button cancelButton = createStyledButton("❌ Cancel", DANGER_COLOR);

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);

        // Button actions
        submitButton.addActionListener(e -> {
            String newsTitle = newsTitleField.getText().trim();
            String category = categoryChoice.getSelectedItem();
            String priority = priorityChoice.getSelectedItem();
            String description = descriptionArea.getText().trim();
            String location = locationField.getText().trim();
            String contact = contactField.getText().trim();

            if (newsTitle.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in at least the title and description.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create news report as an announcement
            String fullMessage = description;
            if (!location.isEmpty()) {
                fullMessage += "\n\nLocation: " + location;
            }
            if (!contact.isEmpty()) {
                fullMessage += "\n\nContact: " + contact;
            }
            fullMessage += "\n\nReported by: " + currentUser.getName() + " (" + currentUser.getRole() + ")";

            Announcement newsReport = new Announcement(
                newsTitle,
                fullMessage,
                category,
                priority,
                currentUser.getName()
            );

            // Add target audience based on category/priority
            if ("Emergency Alert".equals(category) || "Urgent".equals(priority)) {
                newsReport.addTargetAudience("All Residents");
                newsReport.addTargetAudience("Barangay Officials");
            } else {
                newsReport.addTargetAudience("All Residents");
            }

            announcements.add(newsReport);

            JOptionPane.showMessageDialog(frame,
                "News report submitted successfully!\n\nTitle: " + newsTitle + "\nCategory: " + category + "\nPriority: " + priority,
                "News Report Submitted",
                JOptionPane.INFORMATION_MESSAGE);

            // Clear form
            newsTitleField.setText("");
            descriptionArea.setText("");
            locationField.setText("");
            contactField.setText("");
            categoryChoice.select(0);
            priorityChoice.select(0);
        });

        clearButton.addActionListener(e -> {
            newsTitleField.setText("");
            descriptionArea.setText("");
            locationField.setText("");
            contactField.setText("");
            categoryChoice.select(0);
            priorityChoice.select(0);
        });

        cancelButton.addActionListener(e -> frame.dispose());

        // Layout assembly
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(formPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

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
        Button createButton = createStyledButton("Create User", PRIMARY_COLOR);
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
                JOptionPane.showMessageDialog(dialog, "Name, email, and barcode ID are required.");
                return;
            }
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid email address!");
                return;
            }

            // Check for duplicate barcode
            boolean duplicateBarcode = systemUsers.stream()
                .anyMatch(u -> barcode.equals(u.getBarcodeId()));
            if (duplicateBarcode) {
                JOptionPane.showMessageDialog(dialog, "Barcode ID already exists. Please use a unique barcode.");
                return;
            }

            SystemUser newUser = new SystemUser(name, email, role, barcode);
            newUser.setDepartment(department);
            systemUsers.add(newUser);
            dbManager.saveSystemUser(newUser);
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
        try { roleChoice.select(user.getRole()); } catch (Exception ex) { roleChoice.select(0); }

        Choice departmentChoice = new Choice();
        departmentChoice.add("General");
        departmentChoice.add("Administration");
        departmentChoice.add("Finance");
        departmentChoice.add("Services");
        departmentChoice.add("Security");
        try { departmentChoice.select(user.getDepartment()); } catch (Exception ex) { departmentChoice.select(0); }

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
                JOptionPane.showMessageDialog(dialog, "Name, email, and barcode ID are required.");
                return;
            }
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid email address!");
                return;
            }

            // Check for duplicate barcode (excluding current user)
            final String finalBarcode = barcode;
            final SystemUser selectedUser = user;
            boolean duplicateBarcode = systemUsers.stream()
                .filter(u -> u != selectedUser)
                .anyMatch(u -> finalBarcode.equals(u.getBarcodeId()));
            if (duplicateBarcode) {
                JOptionPane.showMessageDialog(dialog, "Barcode ID already exists. Please use a unique barcode.");
                return;
            }

            user.setName(name);
            user.setEmail(email);
            user.setBarcodeId(barcode);
            user.setRole(role);
            user.setDepartment(department);
            dbManager.saveSystemUser(user);
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
        dbManager.saveSystemUser(user);
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

        Panel permissionPanel = new Panel(new GridLayout(0, 3, 10, 5));
        java.util.List<Button> permissionButtons = new java.util.ArrayList<>();
        java.util.List<String> selectedPermissions = new java.util.ArrayList<>(user.getPermissions());

        String[] allPermissions = {
            "READ_RESIDENTS", "WRITE_RESIDENTS", "DELETE_RESIDENTS",
            "READ_CERTIFICATES", "WRITE_CERTIFICATES", "APPROVE_CERTIFICATES",
            "READ_COMPLAINTS", "WRITE_COMPLAINTS", "RESOLVE_COMPLAINTS",
            "READ_FINANCE", "WRITE_FINANCE", "GENERATE_REPORTS",
            "READ_ANNOUNCEMENTS", "WRITE_ANNOUNCEMENTS", "MANAGE_ANNOUNCEMENTS",
            "MANAGE_USERS", "SYSTEM_SETTINGS", "BACKUP_DATA"
        };

        for (String perm : allPermissions) {
            Button btn = createStyledButton(perm, selectedPermissions.contains(perm) ? SUCCESS_COLOR : SECONDARY_COLOR);
            btn.addActionListener(e -> {
                if (selectedPermissions.contains(perm)) {
                    selectedPermissions.remove(perm);
                    btn.setBackground(SECONDARY_COLOR);
                } else {
                    selectedPermissions.add(perm);
                    btn.setBackground(SUCCESS_COLOR);
                }
            });
            permissionPanel.add(btn);
            permissionButtons.add(btn);
        }

        Panel buttonPanel = new Panel(new FlowLayout());
        Button saveButton = createStyledButton("💾 Save Permissions", PRIMARY_COLOR);
        Button cancelButton = createStyledButton("❌ Cancel", DANGER_COLOR);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            user.getPermissions().clear();
            user.getPermissions().addAll(selectedPermissions);
            dbManager.saveSystemUser(user);
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
            dbManager.deleteSystemUser(user.getId());
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
        Frame loginFrame = new Frame("�️ Barangay San Lorenza Information System - Modern Login");
        loginFrame.setSize(600, 400);
        loginFrame.setLayout(new BorderLayout());
        loginFrame.setBackground(new Color(34, 139, 34)); // Forest Green background

        // Modern gradient background panel
        Panel backgroundPanel = new ModernLoginPanel();
        backgroundPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Modern title with icon
        Panel titlePanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(0, 0, 0, 0)); // Transparent

        Label iconLabel = new Label("🏘️");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        titlePanel.add(iconLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        backgroundPanel.add(titlePanel, gbc);

        Label titleLabel = new Label("Barangay San Lorenza");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignment(Label.CENTER);

        gbc.gridy = 1;
        backgroundPanel.add(titleLabel, gbc);

        Label subtitleLabel = new Label("Information Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 255, 200));
        subtitleLabel.setAlignment(Label.CENTER);

        gbc.gridy = 2;
        backgroundPanel.add(subtitleLabel, gbc);

        // Modern input section
        Panel inputContainer = new Panel(new BorderLayout(10, 10));
        inputContainer.setBackground(new Color(255, 255, 255, 220)); // Semi-transparent white
        inputContainer.setPreferredSize(new Dimension(400, 80));

        Label inputIcon = new Label("🔐");
        inputIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        inputContainer.add(inputIcon, BorderLayout.WEST);

        TextField barcodeField = new TextField(30);
        barcodeField.setFont(new Font("Segoe UI", Font.BOLD, 16));
        barcodeField.setBackground(Color.WHITE);
        barcodeField.setForeground(new Color(34, 139, 34));
        barcodeField.setText("Enter barcode ID, email, or scan...");
        barcodeField.setForeground(Color.GRAY);

        // Modern placeholder effect
        barcodeField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (barcodeField.getText().equals("Enter barcode ID, email, or scan...")) {
                    barcodeField.setText("");
                    barcodeField.setForeground(new Color(34, 139, 34));
                }
            }
            public void focusLost(FocusEvent e) {
                if (barcodeField.getText().isEmpty()) {
                    barcodeField.setText("Enter barcode ID, email, or scan...");
                    barcodeField.setForeground(Color.GRAY);
                }
            }
        });

        inputContainer.add(barcodeField, BorderLayout.CENTER);

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        backgroundPanel.add(inputContainer, gbc);

        // Modern status feedback
        Label feedback = new Label("Ready for authentication...");
        feedback.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        feedback.setForeground(new Color(200, 255, 200));
        feedback.setAlignment(Label.CENTER);

        gbc.gridy = 4;
        backgroundPanel.add(feedback, gbc);

        // User info display
        Label userInfo = new Label(" ");
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userInfo.setForeground(new Color(255, 255, 200));
        userInfo.setAlignment(Label.CENTER);

        gbc.gridy = 5;
        backgroundPanel.add(userInfo, gbc);

        // Modern hint text
        Label hintLabel = new Label("💡 Use the buttons below or press Enter to login");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hintLabel.setForeground(new Color(200, 255, 200));
        hintLabel.setAlignment(Label.CENTER);

        gbc.gridy = 6;
        backgroundPanel.add(hintLabel, gbc);

        // Modern Glassy Buttons Panel with labels underneath
        Panel buttonsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        buttonsPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent

        // Create modern glassy buttons in requested order
        GlassyButton scanButton = new GlassyButton("📷 Scan", new Color(52, 152, 219));
        scanButton.setPreferredSize(new Dimension(140, 40));
        Panel scanContainer = new Panel(new GridLayout(2, 1, 0, 5));
        scanContainer.setBackground(new Color(0, 0, 0, 0));
        scanContainer.add(scanButton);
        Label scanLabel = new Label("Scan with Barcode", Label.CENTER);
        scanLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        scanLabel.setForeground(new Color(200, 255, 200));
        scanContainer.add(scanLabel);

        GlassyButton loginButton = new GlassyButton("🔓 Login", new Color(46, 204, 113));
        loginButton.setPreferredSize(new Dimension(120, 40));
        Panel loginContainer = new Panel(new GridLayout(2, 1, 0, 5));
        loginContainer.setBackground(new Color(0, 0, 0, 0));
        loginContainer.add(loginButton);
        Label loginLabel = new Label("Login", Label.CENTER);
        loginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loginLabel.setForeground(new Color(200, 255, 200));
        loginContainer.add(loginLabel);

        GlassyButton guestButton = new GlassyButton("👤 Guest", new Color(108, 117, 125));
        guestButton.setPreferredSize(new Dimension(160, 40));
        Panel guestContainer = new Panel(new GridLayout(2, 1, 0, 5));
        guestContainer.setBackground(new Color(0, 0, 0, 0));
        guestContainer.add(guestButton);
        Label guestLabel = new Label("Continue as Guest", Label.CENTER);
        guestLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        guestLabel.setForeground(new Color(200, 255, 200));
        guestContainer.add(guestLabel);

        GlassyButton barcodeGenButton = new GlassyButton("🔲 Barcode", new Color(75, 0, 130));
        barcodeGenButton.setPreferredSize(new Dimension(140, 40));
        Panel barcodeGenContainer = new Panel(new GridLayout(2, 1, 0, 5));
        barcodeGenContainer.setBackground(new Color(0, 0, 0, 0));
        barcodeGenContainer.add(barcodeGenButton);
        Label barcodeGenLabel = new Label("Barcode Generator", Label.CENTER);
        barcodeGenLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        barcodeGenLabel.setForeground(new Color(200, 255, 200));
        barcodeGenContainer.add(barcodeGenLabel);

        buttonsPanel.add(scanContainer);
        buttonsPanel.add(loginContainer);
        buttonsPanel.add(guestContainer);
        buttonsPanel.add(barcodeGenContainer);

        gbc.gridy = 7;
        backgroundPanel.add(buttonsPanel, gbc);

        barcodeGenButton.addActionListener(e -> openLoginBarcodeTool(loginFrame));

        loginFrame.add(backgroundPanel, BorderLayout.CENTER);

        // Enhanced modern login logic
        ActionListener doLogin = e -> {
            String code = barcodeField.getText().trim();

            // Handle placeholder text
            if (code.equals("Enter barcode ID, email, or scan...") || code.isEmpty()) {
                feedback.setText("Please enter your credentials or press Enter for guest access");
                feedback.setForeground(new Color(255, 200, 200));
                barcodeField.requestFocus();
                return;
            }

            feedback.setText("Authenticating...");
            feedback.setForeground(new Color(255, 255, 200));

            SystemUser loggedInUser = authenticateBarcode(code);
            if (loggedInUser == null) {
                loggedInUser = createUserFromInput(code);
                feedback.setText("Welcome, new user: " + loggedInUser.getName());
                feedback.setForeground(new Color(200, 255, 200));
                userInfo.setText("Role: " + loggedInUser.getRole());
            } else {
                feedback.setText("Authentication successful!");
                feedback.setForeground(new Color(200, 255, 200));
                userInfo.setText("Welcome back, " + loggedInUser.getName());
            }

            // Modern smooth transition
            final SystemUser finalLoggedInUser = loggedInUser;
            Timer timer = new Timer(1200, evt -> {
                loginFrame.dispose();
                new BarangayInfoSys(finalLoggedInUser);
            });
            timer.setRepeats(false);
            timer.start();
        };

        barcodeField.addActionListener(doLogin);
        scanButton.addActionListener(e -> {
            feedback.setText("Scanner activated - Please scan barcode...");
            feedback.setForeground(new Color(100, 200, 255));
            barcodeField.setText("");
            barcodeField.requestFocus();

            // Simulate scanning after a brief delay
            Timer scanTimer = new Timer(1500, evt -> {
                simulateBarcodeScan(barcodeField, feedback);
            });
            scanTimer.setRepeats(false);
            scanTimer.start();
        });
        loginButton.addActionListener(doLogin);
        guestButton.addActionListener(e -> {
            SystemUser guestUser = new SystemUser("Guest", "guest@barangay.local", "Guest", "GUEST");
            feedback.setText("Continuing as guest...");
            feedback.setForeground(new Color(200, 255, 200));
            userInfo.setText("Guest access activated");

            Timer timer = new Timer(800, evt -> {
                loginFrame.dispose();
                new BarangayInfoSys(guestUser);
            });
            timer.setRepeats(false);
            timer.start();
        });

        // Modern window controls
        loginFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);

        // Auto-focus with modern effect
        loginFrame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                Timer focusTimer = new Timer(300, evt -> barcodeField.requestFocus());
                focusTimer.setRepeats(false);
                focusTimer.start();
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
        users.add(new SystemUser("Administrator", "jpleano8@gmail.com", "Administrator", "ADM2024001"));
        return users;
    }

    private static void openLoginBarcodeTool(Frame parent) {
        Frame frame = new Frame("🔲 Barcode Generator");
        frame.setSize(600, 420);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(Color.WHITE);

        Panel headerPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(75, 0, 130));
        Label headerLabel = new Label("🔲 Barcode Generator", Label.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        Panel inputPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(245, 245, 255));
        Label inputLabel = new Label("Enter text to encode:");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        TextField inputField = new TextField(30);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        Button generateBtn = new Button("Generate");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputPanel.add(generateBtn);

        final BufferedImage[] imgHolder = { BarcodeGenerator.generate("SAMPLE", 2, 80) };
        Canvas canvas = new Canvas() {
            public void paint(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                int x = (getWidth() - imgHolder[0].getWidth()) / 2;
                g.drawImage(imgHolder[0], x, 10, this);
            }
            public Dimension getPreferredSize() { return new Dimension(560, 130); }
        };
        canvas.setBackground(Color.WHITE);

        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        btnPanel.setBackground(Color.WHITE);
        Button printBtn = new Button("🖨️ Print");
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        Button closeBtn = new Button("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPanel.add(printBtn);
        btnPanel.add(closeBtn);

        ActionListener doGenerate = e -> {
            String text = inputField.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter text to generate a barcode.");
                return;
            }
            for (char c : text.toCharArray()) {
                if (c < 32 || c > 126) {
                    JOptionPane.showMessageDialog(frame, "Only printable ASCII characters are supported.");
                    return;
                }
            }
            imgHolder[0] = BarcodeGenerator.generate(text, 2, 80);
            canvas.repaint();
        };
        generateBtn.addActionListener(doGenerate);
        inputField.addActionListener(doGenerate);

        printBtn.addActionListener(e -> {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                graphics.drawImage(imgHolder[0],
                    (int) pageFormat.getImageableX(),
                    (int) pageFormat.getImageableY(), null);
                return Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) {
                try { job.print(); } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(frame, "Print error: " + ex.getMessage());
                }
            }
        });
        closeBtn.addActionListener(e -> frame.dispose());

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.CENTER);
        Panel centerPanel = new Panel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(canvas, BorderLayout.CENTER);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);
        frame.add(centerPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { frame.dispose(); }
        });
        frame.setLocationRelativeTo(parent);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        showLoginScreen();
    }
}
