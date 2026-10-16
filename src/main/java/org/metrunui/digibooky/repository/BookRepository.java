package org.metrunui.digibooky.repository;

import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.util.BookSearchCriteria;
import org.metrunui.digibooky.util.WildCardMatcher;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * In-memory repository for {@link Book} objects.
 * <p>
 * Books are stored in a map with their UUID as the key. The repository starts
 * with a few hardcoded books so the application has data to work with.
 * </p>
 */
@Repository
public class BookRepository {
    Map<UUID, Book> books;

    /**
     * Creates the repository and fills it with some hardcoded books.
     */
    public BookRepository() {
        this.books = new ConcurrentHashMap<>();
        Book book1 = new Book("9784563458615", "BEst Book ever: Read this", "Small summary", "Beck", "Kent");
        Book book2 = new Book("9784563123615", "No, this is the best Book ever: Read this instead", "Smaller summary", "Beck", "Kent");
        Book book3 = new Book("9784563458612", "Worst book ever: don't read this", "No summary because you shouldn't even read the summary.", "Beck", "Kent");
        books.put(book1.getBookId(), book1);
        books.put(book2.getBookId(), book2);
        books.put(book3.getBookId(), book3);
    }

    /**
     * Builds a filter rule that checks one field of a {@link Book} against a wildcard expression.
     * <p>
     * The returned predicate does not filter anything by itself; it is meant to be combined
     * with other predicates (using {@link Predicate#and}) and applied to a stream of books.
     * <p>
     * Matching follows the rules of {@link WildCardMatcher}: {@code *} matches zero or more
     * characters, all other characters are matched literally, and matching is case-insensitive.
     * For example, {@code "Tolk*"} matches {@code "Tolkien"}, and {@code "Tolk"} does not.
     * <p>
     * If {@code expression} is {@code null} or blank, the field is not filtered: the returned
     * predicate accepts every book. If the extracted field value is {@code null}, the book
     * does not match.
     *
     * @param expression the wildcard expression to match against, for example {@code "The*"};
     *                   may be {@code null} or blank to disable filtering on this field
     * @param extractor  selects the field to check, for example {@code Book::getTitle};
     *                   must not throw for any book in the data set
     * @return a predicate that returns {@code true} for books whose field matches the expression
     */
    private static Predicate<Book> fieldMatches(String expression, Function<Book, String> extractor) {
        if (expression == null || expression.isBlank()) return book -> true;
        WildCardMatcher matcher = WildCardMatcher.of(expression);
        return book -> matcher.matches(extractor.apply(book));
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
     * @param isbn the ISBN to search for
     * @return {@link Optional}  containing the matching book, or {@link Optional#empty()} if the ISBN is blank or no matching book exists
     */
    public Optional<Book> findByISBN(String isbn) {
        if (isbn.isBlank()) {
            return Optional.empty();
        }
        return books.values().stream().filter(book -> book.getIsbn().equals(isbn)).findFirst();
    }

    /**
     * Get book details by the specified id.
     *
     * @param id the id to search for
     * @return {@link Optional}  containing the matching book, or {@link Optional#empty()} if the id is null or no matching book exists
     */
    public Optional<Book> getBook(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(books.get(id));
    }

    /**
     * Get all books in the repository.
     *
     * @return a collection with all books, or an empty collection if there are no books
     */
    public Collection<Book> getAllBooks() {
        return books.values();
    }

    /**
     * Finds all books that match the given search criteria.
     * <p>
     * Every field in the criteria can contain a {@code *} wildcard, for example
     * {@code "978*"} or {@code "*Development"}. Fields that are {@code null} or blank
     * are ignored. When more than one field is filled in, a book must match all of them.
     * If all fields are empty, all books are returned.
     * </p>
     *
     * @param criteria the search criteria to filter on
     * @return a list with the matching books, or an empty list if no book matches
     */
    public Collection<Book> findBooks(BookSearchCriteria criteria) {
        Predicate<Book> filter = fieldMatches(criteria.isbn(), Book::getIsbn)
                .and(fieldMatches(criteria.title(), Book::getTitle))
                .and(fieldMatches(criteria.authorLastName(), Book::getAuthorLastName))
                .and(fieldMatches(criteria.authorFirstName(), Book::getAuthorFirstName));

        return books.values().stream().filter(filter).toList();
    }

    /**
     * Removes all books from the repository.
     * <p>
     * This is mainly used in tests to start with an empty repository.
     * </p>
     */
    public void clear() {
        books.clear();
    }
}
