package ch.bzz.data;

import ch.bzz.Book;

import java.util.List;

public interface BookRepository {
    List<Book> findAll();
    void saveAllOverwriteById(List<Book> books);
}
