import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// 1. Resident Data Model
class Resident {
    private String firstName, lastName, middleName, address, contactNumber, birthDate, gender;
    private long id;

    public Resident(String firstName, String lastName, String middleName,
                   String address, String contactNumber, String birthDate, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.birthDate = birthDate;
        this.gender = gender;
        this.id = System.currentTimeMillis();
    }

    public String getFullName() { return firstName + " " + middleName + " " + lastName; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s", getFullName(), address, contactNumber, gender);
    }
}

// 2. Resident Table Component
class ResidentTable extends java.awt.List {
    private List<Resident> residents = new ArrayList<>();

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
    private TextField firstNameField, lastNameField, middleNameField, addressField, contactField, birthDateField, searchField;
    private Choice genderChoice;
    private Button addButton, removeButton, searchButton, refreshButton;
    private Label statusLabel;

    public BarangayInfoSys() {
        setTitle("Barangay Information System");
        setSize(900, 650);
        setLayout(new BorderLayout(10, 10));

        // --- NORTH PANEL: Input Form ---
        Panel inputPanel = new Panel(new GridLayout(4, 4, 10, 10));
        inputPanel.add(new Label("First Name:")); firstNameField = new TextField(); inputPanel.add(firstNameField);
        inputPanel.add(new Label("Middle Name:")); middleNameField = new TextField(); inputPanel.add(middleNameField);
        inputPanel.add(new Label("Last Name:")); lastNameField = new TextField(); inputPanel.add(lastNameField);
        inputPanel.add(new Label("Gender:")); 
        genderChoice = new Choice(); genderChoice.add("Male"); genderChoice.add("Female"); inputPanel.add(genderChoice);
        inputPanel.add(new Label("Address:")); addressField = new TextField(); inputPanel.add(addressField);
        inputPanel.add(new Label("Contact #:")); contactField = new TextField(); inputPanel.add(contactField);
        inputPanel.add(new Label("Birth Date:")); birthDateField = new TextField(); inputPanel.add(birthDateField);
        
        // --- CENTER PANEL: List ---
        residentListUI = new ResidentTable();

        // --- SOUTH PANEL: Controls and Status ---
        Panel southPanel = new Panel(new GridLayout(2, 1));
        
        Panel buttonPanel = new Panel(new FlowLayout());
        searchField = new TextField(15);
        searchButton = new Button("Search");
        addButton = new Button("Add Resident");
        removeButton = new Button("Delete Selected");
        refreshButton = new Button("Show All");

        buttonPanel.add(new Label("Search Name:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        statusLabel = new Label("System Ready", Label.CENTER);
        statusLabel.setBackground(new Color(230, 230, 230));

        southPanel.add(buttonPanel);
        southPanel.add(statusLabel);

        // Add main panels to Frame
        add(inputPanel, BorderLayout.NORTH);
        add(residentListUI, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Event Handlers
        addButton.addActionListener(e -> addResident());
        removeButton.addActionListener(e -> removeSelected());
        searchButton.addActionListener(e -> searchResidents());
        refreshButton.addActionListener(e -> {
            residentListUI.updateDisplayList(residentListUI.getAllResidents());
            statusLabel.setText("Showing all records.");
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });

        setVisible(true);
    }

    private void addResident() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            statusLabel.setText("Error: First and Last names are required!");
            return;
        }
        
        Resident res = new Resident(
            firstNameField.getText(), lastNameField.getText(), middleNameField.getText(),
            addressField.getText(), contactField.getText(), birthDateField.getText(),
            genderChoice.getSelectedItem()
        );
        
        residentListUI.addResident(res);
        clearFields();
        statusLabel.setText("Added: " + res.getFullName());
    }

    private void removeSelected() {
        int idx = residentListUI.getSelectedIndex();
        if (idx != -1) {
            residentListUI.removeResident(idx);
            statusLabel.setText("Resident record deleted.");
        } else {
            statusLabel.setText("Error: Select a resident first.");
        }
    }

    private void searchResidents() {
        String query = searchField.getText().toLowerCase().trim();
        List<Resident> results = new ArrayList<>();
        for (Resident r : residentListUI.getAllResidents()) {
            if (r.getFullName().toLowerCase().contains(query)) {
                results.add(r);
            }
        }
        residentListUI.updateDisplayList(results);
        statusLabel.setText("Found " + results.size() + " matches.");
    }

    private void clearFields() {
        firstNameField.setText(""); middleNameField.setText(""); lastNameField.setText("");
        addressField.setText(""); contactField.setText(""); birthDateField.setText("");
    }

    public static void main(String[] args) {
        new BarangayInfoSys();
    }
}