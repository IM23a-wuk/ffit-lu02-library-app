package ch.bzz;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDBConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/localdb";
        String user = "postgres";
        String password = "Xiaomiao1";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Verbindung erfolgreich!");
            System.out.println("Verbindung: " + conn);
            //select * from books;
            String sql = "SELECT * FROM books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getString("title"));
            }
        } catch (SQLException e) {
            System.out.println("Fehler bei der Verbindung:");
            e.printStackTrace();
        }
    }
}
