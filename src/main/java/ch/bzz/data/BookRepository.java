package ch.bzz.data;

import ch.bzz.Book;

import java.util.List;

public interface BookRepository {
    List<Book> findAll();
    List<Book> findAll(int limit);
    void saveAllOverwriteById(List<Book> books);
}
