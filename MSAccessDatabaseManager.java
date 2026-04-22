import java.sql.*;
import java.util.*;
import java.io.File;

public class MSAccessDatabaseManager {
    private final String dbFilePath;
    private final String connectionUrl;

    public MSAccessDatabaseManager(String dbFilePath) {
        this.dbFilePath = dbFilePath;
        this.connectionUrl = "jdbc:ucanaccess://" + dbFilePath;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("UCanAccess JDBC driver not found. Add the driver JAR to the classpath.");
        }
        initializeDatabase();
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    private void initializeDatabase() {
        File dbFile = new File(dbFilePath);
        try (Connection connection = openConnection()) {
            if (!tableExists(connection, "SystemUsers")) {
                createSystemUsersTable(connection);
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize MS Access database: " + e.getMessage());
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName.toUpperCase(), new String[] {"TABLE"})) {
            return rs.next();
        }
    }

    private void createSystemUsersTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE SystemUsers (" +
                     "id LONG PRIMARY KEY, " +
                     "name TEXT(255), " +
                     "email TEXT(255), " +
                     "role TEXT(255), " +
                     "barcodeId TEXT(255), " +
                     "department TEXT(255), " +
                     "isActive YESNO, " +
                     "lastLogin TEXT(255), " +
                     "createdDate TEXT(255), " +
                     "passwordHash TEXT(255), " +
                     "permissions MEMO)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public List<SystemUser> loadSystemUsers() {
        List<SystemUser> users = new ArrayList<>();
        String sql = "SELECT * FROM SystemUsers ORDER BY id";
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                users.add(createSystemUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load users from MS Access: " + e.getMessage());
        }
        return users;
    }

    private SystemUser createSystemUserFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String role = rs.getString("role");
        String barcodeId = rs.getString("barcodeId");
        String department = rs.getString("department");
        boolean isActive = rs.getBoolean("isActive");
        String lastLogin = rs.getString("lastLogin");
        String createdDate = rs.getString("createdDate");
        String passwordHash = rs.getString("passwordHash");
        String permissionsCsv = rs.getString("permissions");
        List<String> permissions = parsePermissions(permissionsCsv);
        return new SystemUser(id, name, email, role, barcodeId, department, isActive, lastLogin, createdDate, passwordHash, permissions);
    }

    public void saveSystemUser(SystemUser user) {
        if (user == null) {
            return;
        }
        try (Connection connection = openConnection()) {
            if (userExists(connection, user.getId())) {
                updateSystemUser(connection, user);
            } else {
                insertSystemUser(connection, user);
            }
        } catch (SQLException e) {
            System.err.println("Failed to save user to MS Access: " + e.getMessage());
        }
    }

    public void deleteSystemUser(long id) {
        String sql = "DELETE FROM SystemUsers WHERE id = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete user from MS Access: " + e.getMessage());
        }
    }

    private boolean userExists(Connection connection, long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SystemUsers WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void insertSystemUser(Connection connection, SystemUser user) throws SQLException {
        String sql = "INSERT INTO SystemUsers (id, name, email, role, barcodeId, department, isActive, lastLogin, createdDate, passwordHash, permissions) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            fillUserStatement(statement, user, true);
            statement.executeUpdate();
        }
    }

    private void updateSystemUser(Connection connection, SystemUser user) throws SQLException {
        String sql = "UPDATE SystemUsers SET name = ?, email = ?, role = ?, barcodeId = ?, department = ?, isActive = ?, lastLogin = ?, createdDate = ?, passwordHash = ?, permissions = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            fillUserStatement(statement, user, false);
            statement.setLong(11, user.getId());
            statement.executeUpdate();
        }
    }

    private void fillUserStatement(PreparedStatement statement, SystemUser user, boolean includeId) throws SQLException {
        if (includeId) {
            statement.setLong(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getRole());
            statement.setString(5, user.getBarcodeId());
            statement.setString(6, user.getDepartment());
            statement.setBoolean(7, user.isActive());
            statement.setString(8, user.getLastLogin());
            statement.setString(9, user.getCreatedDate());
            statement.setString(10, user.getPasswordHash());
            statement.setString(11, formatPermissions(user.getPermissions()));
        } else {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getRole());
            statement.setString(4, user.getBarcodeId());
            statement.setString(5, user.getDepartment());
            statement.setBoolean(6, user.isActive());
            statement.setString(7, user.getLastLogin());
            statement.setString(8, user.getCreatedDate());
            statement.setString(9, user.getPasswordHash());
            statement.setString(10, formatPermissions(user.getPermissions()));
        }
    }

    private String formatPermissions(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "";
        }
        return String.join(",", permissions);
    }

    private List<String> parsePermissions(String permissionsCsv) {
        List<String> permissions = new ArrayList<>();
        if (permissionsCsv == null || permissionsCsv.trim().isEmpty()) {
            return permissions;
        }
        for (String value : permissionsCsv.split(",")) {
            String trimmed = value.trim();
            if (!trimmed.isEmpty()) {
                permissions.add(trimmed);
            }
        }
        return permissions;
    }
}
