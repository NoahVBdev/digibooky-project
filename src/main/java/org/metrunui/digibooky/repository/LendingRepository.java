package org.metrunui.digibooky.repository;

import org.metrunui.digibooky.domain.Lending;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LendingRepository {
    private final Map<UUID, Lending> lendings = new ConcurrentHashMap<>();

    public Lending save(Lending lending) {
        lendings.put(lending.getId(), lending);
        return lending;
    }

    public Optional<Lending> findById(UUID id) {
        return id == null ? Optional.empty() : Optional.ofNullable(lendings.get(id));
    }

    public Optional<Lending> findActiveByBookId(UUID bookId) {
        return lendings.values().stream()
                .filter(lending -> lending.getBookId().equals(bookId) && lending.isActive())
                .findFirst();
    }
}
