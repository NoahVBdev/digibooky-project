package org.metrunui.digibooky.repository;

import org.metrunui.digibooky.domain.Book;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BookRepository {
    Map<UUID, Book> books;

    public BookRepository() {
        this.books = new ConcurrentHashMap<>();
        Book book1 = new Book("97845634586157", "BEst Book ever: Read this", "Small summary", "Beck", "Kent");
        Book book2 = new Book("97845631236157", "No, this is the best Book ever: Read this instead", "Smaller summary", "Beck", "Kent");
        Book book3 = new Book("97845634586123", "Worst book ever: don't read this", "No summary because you shouldn't even read the summary.", "Beck", "Kent");
        books.put(book1.getBookId(), book1);
        books.put(book2.getBookId(), book2);
        books.put(book3.getBookId(), book3);
    }

    /**
     * Save a book in the repository
     *
     * <p>The book is stored using its UUID as the key. If a book with the same UUID
     * already exists, it is replaced by the provided book.</p>
     *
     * @param book book to save in repository
     * @return saved {@code Book} on success
     */
    public Book save(Book book) {
        books.put(book.getBookId(), book);
        return book;
    }

    /**
     * Finds a book by its ISBN.
     * <p>
     * Returns an {@link Optional} containing the first book whose ISBN matches
     * the provided ISBN, or an empty {@link Optional} if no matching book is found.
     * If the ISBN is blank, an empty {@link Optional} is returned immediately.
     * </p>
     *
     * @param isbn isbn the ISBN to search for
     * @return {@link Optional}  containing the matching book, or {@link Optional#empty()} if the ISBN is blank or no matching book exists
     */
    public Optional<Book> findByISBN(String isbn) {
        if (isbn.isBlank()) {
            return Optional.empty();
        }
        return books.values().stream().filter(book -> book.getIsbn().equals(isbn)).findFirst();
    }

    public Collection<Book> getAllBooks() {
        return books.values();
    }
}
