package ch.bzz;
import java.util.Scanner;


public class LibraryAppMain {
    private static final Book BOOK_1 = new Book(1, "978-3-8362-9544-4", "Java ist auch eine Insel", "Christian Ullenboom", 2023);
    private static final Book BOOK_2 = new Book(2, "978-3-658-43573-8", "Grundkurs Java", "Dietmar Abts", 2024);

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
        System.out.println(BOOK_1.getTitle());
        System.out.println(BOOK_2.getTitle());
    }
}
