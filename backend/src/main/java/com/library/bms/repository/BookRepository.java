package com.library.bms.repository;

import com.library.bms.entity.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class BookRepository {
    private final Map<String, Book> books = new ConcurrentHashMap<>();
    
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }
    
    public Optional<Book> findByIssn(String issn) {
        return Optional.ofNullable(books.get(issn));
    }
    
    public Book save(Book book) {
        books.put(book.getIssn(), book);
        return book;
    }
    
    public boolean existsByIssn(String issn) {
        return books.containsKey(issn);
    }
    
    public void deleteByIssn(String issn) {
        books.remove(issn);
    }
    
    public List<Book> findByAvailable(boolean available) {
        return books.values().stream()
                .filter(book -> book.isAvailable() == available)
                .collect(Collectors.toList());
    }
    
    public void deleteAll() {
        books.clear();
    }
}
