package ch.bzz;

import ch.bzz.data.BookRepository;
import ch.bzz.data.JdbcBookRepository;
import io.javalin.Javalin;

import java.util.List;

public class JavalinMain {

    public static Javalin setup() {
        BookRepository bookRepository = new JdbcBookRepository();

        // Javalin-App erstellen
        Javalin app = Javalin.create();

        app.get("/books", ctx -> {
            String limitParam = ctx.queryParam("limit");
            List<Book> books;

            if (limitParam != null) {
                try {
                    int limit = Integer.parseInt(limitParam);
                    if (limit > 0) {
                        books = bookRepository.findAll(limit);
                        ctx.json(books);
                    } else {
                        ctx.status(400).result("Limit must be a positive number.");
                    }
                } catch (NumberFormatException e) {
                    ctx.status(400).result("Invalid limit format. Must be a number.");
                }
            } else {
                books = bookRepository.findAll();
                ctx.json(books);
            }
        });

        return app;
    }

    public static void main(String[] args) {
        Javalin app = setup();
        app.start(7070);
    }
}
