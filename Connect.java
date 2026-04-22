import java.sql.*;

public class Connect {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=Master;encrypt=true;trustServerCertificate=true;";
        Connection conn = DriverManager.getConnection(url, "user", "pass");
        System.out.println("Connected!");
    }
}