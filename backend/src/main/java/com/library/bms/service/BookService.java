package com.library.bms.service;

import com.library.bms.entity.Book;
import com.library.bms.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> getBookByIssn(String issn) {
        return bookRepository.findByIssn(issn);
    }
    
    public Book createBook(Book book) {
        if (bookRepository.existsByIssn(book.getIssn())) {
            throw new IllegalArgumentException("Book with ISSN " + book.getIssn() + " already exists");
        }
        book.setAvailable(true);
        book.setBorrowerNo(null);
        return bookRepository.save(book);
    }
    
    public Book updateBook(String issn, Book book) {
        Book existingBook = bookRepository.findByIssn(issn)
                .orElseThrow(() -> new IllegalArgumentException("Book with ISSN " + issn + " not found"));
        book.setIssn(issn);
        book.setAvailable(existingBook.isAvailable());
        book.setBorrowerNo(existingBook.getBorrowerNo());
        return bookRepository.save(book);
    }
    
    public void deleteBook(String issn) {
        if (!bookRepository.existsByIssn(issn)) {
            throw new IllegalArgumentException("Book with ISSN " + issn + " not found");
        }
        bookRepository.deleteByIssn(issn);
    }
    
    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailable(true);
    }
    
    public boolean existsByIssn(String issn) {
        return bookRepository.existsByIssn(issn);
    }
}
