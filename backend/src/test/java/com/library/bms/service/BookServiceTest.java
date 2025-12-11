package com.library.bms.service;

import com.library.bms.entity.Book;
import com.library.bms.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = new BookRepository();
        bookService = new BookService(bookRepository);
    }

    @Test
    void createBook_success_setsAvailableAndBorrower() {
        Book book = new Book("B001", "Test Book", "Publisher", "Author", 29.99);
        
        Book created = bookService.createBook(book);
        
        assertNotNull(created);
        assertEquals("B001", created.getIssn());
        assertTrue(created.isAvailable());
        assertNull(created.getBorrowerNo());
    }

    @Test
    void createBook_duplicateIssn_throws() {
        Book book1 = new Book("B001", "Test Book 1", "Publisher", "Author", 29.99);
        bookService.createBook(book1);
        
        Book book2 = new Book("B001", "Test Book 2", "Publisher", "Author", 39.99);
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.createBook(book2)
        );
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void getBookByIssn_found() {
        Book book = new Book("B001", "Test Book", "Publisher", "Author", 29.99);
        bookService.createBook(book);
        
        Optional<Book> found = bookService.getBookByIssn("B001");
        
        assertTrue(found.isPresent());
        assertEquals("Test Book", found.get().getTitle());
    }

    @Test
    void getBookByIssn_notFound() {
        Optional<Book> found = bookService.getBookByIssn("B999");
        
        assertFalse(found.isPresent());
    }

    @Test
    void getAllBooks_returnsAll() {
        bookService.createBook(new Book("B001", "Book 1", "Publisher", "Author", 29.99));
        bookService.createBook(new Book("B002", "Book 2", "Publisher", "Author", 39.99));
        
        List<Book> books = bookService.getAllBooks();
        
        assertEquals(2, books.size());
    }

    @Test
    void updateBook_success_preservesAvailabilityAndBorrower() {
        Book book = new Book("B001", "Test Book", "Publisher", "Author", 29.99);
        bookService.createBook(book);
        
        Book existingBook = bookService.getBookByIssn("B001").get();
        existingBook.setAvailable(false);
        existingBook.setBorrowerNo("S001");
        bookRepository.save(existingBook);
        
        Book updateData = new Book("B001", "Updated Title", "New Publisher", "New Author", 49.99);
        Book result = bookService.updateBook("B001", updateData);
        
        assertEquals("Updated Title", result.getTitle());
        assertEquals("New Publisher", result.getPublisher());
        assertFalse(result.isAvailable());
        assertEquals("S001", result.getBorrowerNo());
    }

    @Test
    void updateBook_notFound_throws() {
        Book book = new Book("B999", "Test Book", "Publisher", "Author", 29.99);
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.updateBook("B999", book)
        );
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void deleteBook_success() {
        bookService.createBook(new Book("B001", "Test Book", "Publisher", "Author", 29.99));
        assertTrue(bookService.existsByIssn("B001"));
        
        bookService.deleteBook("B001");
        
        assertFalse(bookService.existsByIssn("B001"));
    }

    @Test
    void deleteBook_notFound_throws() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.deleteBook("B999")
        );
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void getAvailableBooks_returnsOnlyAvailable() {
        Book book1 = new Book("B001", "Book 1", "Publisher", "Author", 29.99);
        Book book2 = new Book("B002", "Book 2", "Publisher", "Author", 39.99);
        bookService.createBook(book1);
        bookService.createBook(book2);
        
        Book borrowedBook = bookService.getBookByIssn("B001").get();
        borrowedBook.setAvailable(false);
        borrowedBook.setBorrowerNo("S001");
        bookRepository.save(borrowedBook);
        
        List<Book> availableBooks = bookService.getAvailableBooks();
        
        assertEquals(1, availableBooks.size());
        assertEquals("B002", availableBooks.get(0).getIssn());
    }

    @Test
    void existsByIssn_returnsTrue_whenExists() {
        bookService.createBook(new Book("B001", "Test Book", "Publisher", "Author", 29.99));
        
        assertTrue(bookService.existsByIssn("B001"));
    }

    @Test
    void existsByIssn_returnsFalse_whenNotExists() {
        assertFalse(bookService.existsByIssn("B999"));
    }
}
