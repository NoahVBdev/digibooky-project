package org.metrunui.digibooky.controller.mapper;

import org.metrunui.digibooky.controller.dto.BookDetailResponseDTO;
import org.metrunui.digibooky.controller.dto.BookResponseDTO;
import org.metrunui.digibooky.controller.dto.CreateBookDTO;
import org.metrunui.digibooky.domain.Book;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class BookMapper {

    public BookResponseDTO toBookResponseDTO(Book book) {
        return new BookResponseDTO(book.getBookId(), book.getIsbn(), book.getTitle(), book.getAuthorLastName(), book.getAuthorFirstName());
    }

//    public BookDetailDTO toBookDetailDTO(Book book) {
//        return new BookDetailDTO(book.getBookId(), book.getIsbn(), book.getTitle(), book.getSummary(), book.getAuthorLastName(), book.getAuthorFirstName());
//    }

    public Collection<BookResponseDTO> toBookResponseDTO(Collection<Book> books) {
        return books.stream().map(this::toBookResponseDTO).toList();
    }

    public Book toBook(CreateBookDTO createBookDTO) {
        return new Book(createBookDTO.isbn(), createBookDTO.title(), createBookDTO.summary(), createBookDTO.authorLastName(), createBookDTO.authorFirstName());
    }

    public BookDetailResponseDTO toBookDetailResponseDTO(Book book) {
        return new BookDetailResponseDTO(book.getBookId(), book.getIsbn(), book.getTitle(), book.getSummary(), book.getAuthorLastName(), book.getAuthorFirstName());
    }

//    public Collection<BookDTO> toSimpleDTO(Collection<Book> allBooks) {
//    }
}
