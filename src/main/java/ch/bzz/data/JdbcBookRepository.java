package ch.bzz.data;

import ch.bzz.Config;
import ch.bzz.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcBookRepository implements BookRepository {

    private final String url;
    private final String user;
    private final String password;

    public JdbcBookRepository() {
        this.url = Config.get("DB_URL");
        this.user = Config.get("DB_USER");
        this.password = Config.get("DB_PASSWORD");
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, isbn, title, author, publication_year FROM books";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getLong("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("publication_year")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Database error while finding all books: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> findAll(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, isbn, title, author, publication_year FROM books LIMIT ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getLong("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("publication_year")
                );
                books.add(book);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Database error while finding all books with limit: " + e.getMessage());
        }
        return books;
    }

    @Override
    public void saveAllOverwriteById(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO books (id, isbn, title, author, publication_year) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET " +
                     "isbn = EXCLUDED.isbn, " +
                     "title = EXCLUDED.title, " +
                     "author = EXCLUDED.author, " +
                     "publication_year = EXCLUDED.publication_year";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Book book : books) {
                pstmt.setLong(1, book.getId());
                pstmt.setString(2, book.getIsbn());
                pstmt.setString(3, book.getTitle());
                pstmt.setString(4, book.getAuthor());
                pstmt.setInt(5, book.getYear());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            // In a real application, you'd use a logging framework
            System.err.println("Database error while saving books: " + e.getMessage());
            // Optionally rethrow as a custom exception
            // throw new DataAccessException("Failed to save books", e);
        }
    }
}
