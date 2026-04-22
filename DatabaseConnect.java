import java.sql.*;

/**
 * Database Connection Manager for Barangay Information System
 * Supports: MS Access (via UCanAccess), MySQL, and SQLite
 * 
 * @author Barangay San Lorenza
 * @version 2.0
 */
public class DatabaseConnect {
    
    // ========== CONFIGURATION ==========
    // Change DB_TYPE to switch between databases: "msaccess", "mysql", or "sqlite"
    private static final String DB_TYPE = "msaccess";
    
    // MS Access Configuration
    private static final String ACCESS_DB_PATH = "BarangayDB.accdb";
    
    // MySQL Configuration
    private static final String MYSQL_HOST = "localhost";
    private static final String MYSQL_PORT = "3306";
    private static final String MYSQL_DATABASE = "BarangayDB";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";
    
    // SQLite Configuration
    private static final String SQLITE_DB_PATH = "BarangayInfoSys.db";
    
    // Connection pool settings
    private static Connection sharedConnection = null;
    private static boolean useConnectionPool = false;
    
    /**
     * Get database connection based on configured DB_TYPE
     * 
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        // Return pooled connection if enabled
        if (useConnectionPool && sharedConnection != null) {
            try {
                if (!sharedConnection.isClosed()) {
                    return sharedConnection;
                }
            } catch (SQLException e) {
                System.err.println("⚠️ Pooled connection is invalid, creating new connection...");
            }
        }
        
        try {
            Connection conn = null;
            
            switch(DB_TYPE.toLowerCase()) {
                case "msaccess":
                case "access":
                    conn = getAccessConnection();
                    break;
                    
                case "mysql":
                    conn = getMySQLConnection();
                    break;
                    
                case "sqlite":
                    conn = getSQLiteConnection();
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unsupported database type: " + DB_TYPE);
            }
            
            // Store in pool if enabled
            if (useConnectionPool && conn != null) {
                sharedConnection = conn;
            }
            
            return conn;
            
        } catch (Exception e) {
            System.err.println("❌ Database Connection Failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * MS Access connection using UCanAccess JDBC driver
     * Requires: ucanaccess-5.0.1.jar and dependencies
     */
    private static Connection getAccessConnection() throws SQLException {
        try {
            // Build connection URL with optimizations
            StringBuilder url = new StringBuilder("jdbc:ucanaccess://");
            url.append(ACCESS_DB_PATH);
            
            // Performance optimizations
            url.append(";memory=true"); // Use memory mode for faster queries
            url.append(";lobScale=0");  // Optimize for large objects
            
            // Optional: Uncomment if database is password-protected
            // url.append(";password=yourpassword");
            
            Connection conn = DriverManager.getConnection(url.toString());
            
            // Verify connection
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ MS Access Database Connected Successfully!");
                System.out.println("📁 Database: " + ACCESS_DB_PATH);
                return conn;
            }
            
            throw new SQLException("Connection established but appears to be closed");
            
        } catch (SQLException e) {
            System.err.println("❌ MS Access Connection Error!");
            System.err.println("Ensure:");
            System.err.println("  1. UCanAccess JAR files are in classpath");
            System.err.println("  2. Database file exists: " + ACCESS_DB_PATH);
            System.err.println("  3. Database is not open in MS Access");
            throw e;
        }
    }
    
    /**
     * MySQL connection using MySQL Connector/J
     */
    private static Connection getMySQLConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Build connection URL
            String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
                                      MYSQL_HOST, MYSQL_PORT, MYSQL_DATABASE);
            
            Connection conn = DriverManager.getConnection(url, MYSQL_USER, MYSQL_PASSWORD);
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ MySQL Database Connected Successfully!");
                System.out.println("🌐 Server: " + MYSQL_HOST + ":" + MYSQL_PORT);
                System.out.println("📁 Database: " + MYSQL_DATABASE);
                return conn;
            }
            
            throw new SQLException("Connection established but appears to be closed");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found! Add mysql-connector-java.jar to classpath");
        } catch (SQLException e) {
            System.err.println("❌ MySQL Connection Error!");
            System.err.println("Ensure:");
            System.err.println("  1. MySQL server is running");
            System.err.println("  2. Database '" + MYSQL_DATABASE + "' exists");
            System.err.println("  3. Username and password are correct");
            throw e;
        }
    }
    
    /**
     * SQLite connection using SQLite JDBC driver
     */
    private static Connection getSQLiteConnection() throws SQLException {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            String url = "jdbc:sqlite:" + SQLITE_DB_PATH;
            Connection conn = DriverManager.getConnection(url);
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ SQLite Database Connected Successfully!");
                System.out.println("📁 Database: " + SQLITE_DB_PATH);
                return conn;
            }
            
            throw new SQLException("Connection established but appears to be closed");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found! Add sqlite-jdbc.jar to classpath");
        }
    }
    
    /**
     * Test database connection without keeping it open
     * 
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                // Test with a simple query
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                boolean hasResult = rs.next();
                rs.close();
                stmt.close();
                return hasResult;
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Connection test failed: " + e.getMessage());
            return false;
        } finally {
            closeConnection(conn);
        }
    }
    
    /**
     * Get connection with automatic retry mechanism
     * 
     * @param maxRetries Maximum number of retry attempts
     * @return Connection object or null if all retries fail
     */
    public static Connection getConnectionWithRetry(int maxRetries) {
        Connection conn = null;
        int attempts = 0;
        
        while (conn == null && attempts < maxRetries) {
            attempts++;
            conn = getConnection();
            
            if (conn == null && attempts < maxRetries) {
                System.err.println("⚠️ Connection attempt " + attempts + " of " + maxRetries + " failed. Retrying...");
                try {
                    Thread.sleep(1000); // Wait 1 second before retry
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        if (conn == null) {
            System.err.println("❌ All connection attempts failed after " + attempts + " tries.");
        }
        
        return conn;
    }
    
    /**
     * Safely close database connection
     * 
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("🔒 Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("⚠️ Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Close all database resources safely
     */
    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { /* ignore */ }
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { /* ignore */ }
        }
        if (conn != null && !useConnectionPool) {
            closeConnection(conn);
        }
    }
    
    /**
     * Enable connection pooling (reuse single connection)
     */
    public static void enableConnectionPooling() {
        useConnectionPool = true;
        System.out.println("🔄 Connection pooling enabled");
    }
    
    /**
     * Disable connection pooling
     */
    public static void disableConnectionPooling() {
        useConnectionPool = false;
        closeConnection(sharedConnection);
        sharedConnection = null;
        System.out.println("🔄 Connection pooling disabled");
    }
    
    /**
     * Get current database type
     */
    public static String getDatabaseType() {
        return DB_TYPE;
    }
    
    /**
     * Get database information
     */
    public static void printDatabaseInfo() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("\n========== DATABASE INFO ==========");
                System.out.println("Database Type: " + DB_TYPE.toUpperCase());
                System.out.println("Product Name: " + meta.getDatabaseProductName());
                System.out.println("Product Version: " + meta.getDatabaseProductVersion());
                System.out.println("Driver Name: " + meta.getDriverName());
                System.out.println("Driver Version: " + meta.getDriverVersion());
                System.out.println("===================================\n");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting database info: " + e.getMessage());
        } finally {
            if (!useConnectionPool) {
                closeConnection(conn);
            }
        }
    }
    
    /**
     * Main method for testing connection
     */
    public static void main(String[] args) {
        System.out.println("🔍 Testing Barangay Database Connection...\n");
        
        // Test connection
        if (testConnection()) {
            System.out.println("\n✅ SUCCESS! Database is ready to use.");
            printDatabaseInfo();
        } else {
            System.out.println("\n❌ FAILED! Please check your database configuration.");
            System.exit(1);
        }
    }
}
