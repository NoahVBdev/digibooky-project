package org.metrunui.digibooky.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.repository.BookRepository;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setup() {
        bookRepository = Mockito.mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    void getAllBooks() {
        // GIVEN
        Book book1 = new Book("9784563123615", "No, this is the best Book ever: Read this instead", "Smaller summary", "Beck", "Kent");
        Book book2 = new Book("9794563458615", "BEst Book ever: Read this", "Small summary", "Beck", "Kent");
        List<Book> expectedBooks = List.of(book1, book2);

        // WHEN
        Mockito.when(bookRepository.getAllBooks()).thenReturn(List.of(book1, book2));
        
        // THEN
        assertEquals(expectedBooks, bookService.getAllBooks());
    }
}