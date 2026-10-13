package org.metrunui.digibooky.repository;

import org.metrunui.digibooky.domain.Book;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookRepository {
    Map<String, Book> books;

    public BookRepository() {
        this.books = new ConcurrentHashMap<>();
    }
}
