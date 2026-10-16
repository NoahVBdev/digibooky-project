package org.metrunui.digibooky.controller;

import org.metrunui.digibooky.controller.dto.BookDetailResponseDTO;
import org.metrunui.digibooky.controller.dto.BookResponseDTO;
import org.metrunui.digibooky.controller.mapper.BookMapper;
import org.metrunui.digibooky.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<BookResponseDTO> getBooks(
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String authorLastName,
            @RequestParam(required = false) String authorFirstName) {
        log.trace("Get books called");
        return bookMapper.toBookResponseDTO(bookService.getBooks(isbn, title, authorLastName, authorFirstName)).stream().toList();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public BookDetailResponseDTO getBook(@PathVariable String id) {
        log.trace("Query for book with id: " + id);
        return bookMapper.toBookDetailResponseDTO(bookService.getBook(id));
    }
}
