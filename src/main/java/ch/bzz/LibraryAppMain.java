package ch.bzz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibraryAppMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("LibraryApp gestartet. Tippe 'help' für Befehle.");

        boolean running = true;
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine();

            switch (input) {
                case "quit":
                    running = false;
                    break;
                case "help":
                    System.out.println("Verfügbare Befehle: quit, help, listBooks");
                    break;
                case "listBooks":
                    listBooks();
                    break;
                default:
                    System.out.println("Unbekannter Befehl: " + input);
                    break;
            }
        }
        scanner.close();
    }

    private static void listBooks() {
        List<Book> books = new ArrayList<>();

        String url = Config.get("DB_URL");
        String user = Config.get("DB_USER");
        String password = Config.get("DB_PASSWORD");

        String sql = "SELECT id, isbn, title, author, publication_year FROM books";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("publication_year")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Book book : books) {
            System.out.println(book.getId() + ": " + book.getTitle());
        }
    }
}
