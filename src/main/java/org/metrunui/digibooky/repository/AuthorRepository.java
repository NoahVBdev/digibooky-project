package org.metrunui.digibooky.repository;

import org.metrunui.digibooky.domain.Author;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorRepository {
    Map<String, Author> authors;

    public AuthorRepository() {
        this.authors = new ConcurrentHashMap<>();
    }
}
