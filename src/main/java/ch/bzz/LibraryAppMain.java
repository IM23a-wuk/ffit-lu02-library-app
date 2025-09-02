package ch.bzz;

import ch.bzz.data.BookRepository;
import ch.bzz.data.JdbcBookRepository;
import ch.bzz.io.TsvBookReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class LibraryAppMain {

    private static BookRepository bookRepository;

    public static void main(String[] args) {
        bookRepository = new JdbcBookRepository();
        Scanner scanner = new Scanner(System.in);

        System.out.println("LibraryApp gestartet. Tippe 'help' für Befehle.");

        boolean running = true;
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+", 2);
            String command = parts[0];


            switch (command) {
                case "quit":
                    running = false;
                    break;
                case "help":
                    System.out.println("Verfügbare Befehle: quit, help, listBooks, importBooks <FILE_PATH>");
                    break;
                case "listBooks":
                    listBooks();
                    break;
                case "importBooks":
                    if (parts.length < 2) {
                        System.err.println("Error: Missing file path for importBooks command.");
                        System.err.println("Usage: importBooks <FILE_PATH>");
                    } else {
                        importBooks(parts[1]);
                    }
                    break;
                default:
                    System.out.println("Unbekannter Befehl: " + command);
                    break;
            }
        }
        scanner.close();
    }

    private static void importBooks(String filePathStr) {
        try {
            Path projectRoot = findProjectRoot();
            Path path = projectRoot.resolve(filePathStr).normalize();

            if (!filePathStr.toLowerCase().endsWith(".tsv")) {
                System.err.println("Error: Invalid file type. Only .tsv files are supported.");
                return;
            }
            
            if (!Files.exists(path)) {
                System.err.println("Error: File does not exist at path: " + path);
                return;
            }

            if (!Files.isReadable(path)) {
                System.err.println("Error: File is not readable: " + path);
                return;
            }

            TsvBookReader reader = new TsvBookReader();
            List<Book> books = reader.read(path);
            bookRepository.saveAllOverwriteById(books);
            System.out.println("Successfully imported " + books.size() + " books from " + path);

        } catch (InvalidPathException e) {
            System.err.println("Error: Invalid file path format: " + filePathStr);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void listBooks() {
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            System.out.println(book.getId() + ": " + book.getTitle());
        }
    }

    private static Path findProjectRoot() {
        Path currentPath = Paths.get("").toAbsolutePath();
        while (currentPath != null) {
            if (Files.exists(currentPath.resolve("build.gradle"))) {
                return currentPath;
            }
            currentPath = currentPath.getParent();
        }
        throw new RuntimeException("Projekt-Stammverzeichnis nicht gefunden");
    }
}
