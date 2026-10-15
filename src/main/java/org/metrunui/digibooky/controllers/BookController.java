package org.metrunui.digibooky.controllers;

import org.metrunui.digibooky.controllers.dtos.BookResponseDTO;
import org.metrunui.digibooky.controllers.mapper.BookMapper;
import org.metrunui.digibooky.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public List<BookResponseDTO> getAllBooks() {
        log.trace("Get all books called");
        return bookMapper.toBookResponseDTO(bookService.getAllBooks()).stream().toList();
    }
}
