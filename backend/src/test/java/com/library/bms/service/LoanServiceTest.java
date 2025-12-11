package com.library.bms.service;

import com.library.bms.entity.Book;
import com.library.bms.entity.History;
import com.library.bms.entity.Student;
import com.library.bms.repository.BookRepository;
import com.library.bms.repository.HistoryRepository;
import com.library.bms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {
    private StudentRepository studentRepository;
    private BookRepository bookRepository;
    private HistoryRepository historyRepository;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        studentRepository = new StudentRepository();
        bookRepository = new BookRepository();
        historyRepository = new HistoryRepository();
        loanService = new LoanService(studentRepository, bookRepository, historyRepository);
        
        studentRepository.save(new Student("S001", "John Doe", "Class1", "123456789", "M"));
        studentRepository.save(new Student("S002", "Jane Doe", "Class2", "987654321", "F"));
        
        Book book1 = new Book("B001", "Book 1", "Publisher", "Author", 29.99);
        book1.setAvailable(true);
        bookRepository.save(book1);
        
        Book book2 = new Book("B002", "Book 2", "Publisher", "Author", 39.99);
        book2.setAvailable(true);
        bookRepository.save(book2);
    }

    @Test
    void borrowBook_success() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        
        History history = loanService.borrowBook("S001", "B001", borrowDate);
        
        assertNotNull(history);
        assertNotNull(history.getId());
        assertEquals("S001", history.getStudentNo());
        assertEquals("B001", history.getIssn());
        assertEquals(borrowDate, history.getBorrowDate());
        assertNull(history.getReturnDate());
        assertEquals(0.0, history.getPenalty());
        
        Book book = bookRepository.findByIssn("B001").get();
        assertFalse(book.isAvailable());
        assertEquals("S001", book.getBorrowerNo());
    }

    @Test
    void borrowBook_studentNotFound_throws() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.borrowBook("S999", "B001", borrowDate)
        );
        assertTrue(exception.getMessage().contains("Student"));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void borrowBook_bookNotFound_throws() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.borrowBook("S001", "B999", borrowDate)
        );
        assertTrue(exception.getMessage().contains("Book"));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void borrowBook_bookAlreadyBorrowed_throws() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        loanService.borrowBook("S001", "B001", borrowDate);
        
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> loanService.borrowBook("S002", "B001", borrowDate)
        );
        assertTrue(exception.getMessage().contains("already borrowed"));
    }

    @Test
    void returnBook_success_noPenalty() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        LocalDate returnDate = LocalDate.of(2025, 2, 10);
        loanService.borrowBook("S001", "B001", borrowDate);
        
        History history = loanService.returnBook("S001", "B001", returnDate);
        
        assertNotNull(history);
        assertEquals(returnDate, history.getReturnDate());
        assertEquals(0.0, history.getPenalty());
        
        Book book = bookRepository.findByIssn("B001").get();
        assertTrue(book.isAvailable());
        assertNull(book.getBorrowerNo());
    }

    @Test
    void returnBook_success_withPenalty() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        LocalDate returnDate = LocalDate.of(2025, 3, 15);
        loanService.borrowBook("S001", "B001", borrowDate);
        
        History history = loanService.returnBook("S001", "B001", returnDate);
        
        assertNotNull(history);
        assertEquals(returnDate, history.getReturnDate());
        
        long daysBorrowed = java.time.temporal.ChronoUnit.DAYS.between(borrowDate, returnDate);
        double expectedPenalty = (daysBorrowed - 60) * 0.1;
        assertEquals(expectedPenalty, history.getPenalty(), 0.001);
        
        Book book = bookRepository.findByIssn("B001").get();
        assertTrue(book.isAvailable());
        assertNull(book.getBorrowerNo());
    }

    @Test
    void returnBook_exactlyMaxDays_noPenalty() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        LocalDate returnDate = borrowDate.plusDays(60);
        loanService.borrowBook("S001", "B001", borrowDate);
        
        History history = loanService.returnBook("S001", "B001", returnDate);
        
        assertEquals(0.0, history.getPenalty());
    }

    @Test
    void returnBook_oneDayOverdue_smallPenalty() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        LocalDate returnDate = borrowDate.plusDays(61);
        loanService.borrowBook("S001", "B001", borrowDate);
        
        History history = loanService.returnBook("S001", "B001", returnDate);
        
        assertEquals(0.1, history.getPenalty(), 0.001);
    }

    @Test
    void returnBook_openLoanNotFound_throws() {
        LocalDate returnDate = LocalDate.of(2025, 1, 15);
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.returnBook("S001", "B001", returnDate)
        );
        assertTrue(exception.getMessage().contains("No open loan found"));
    }

    @Test
    void returnBook_returnBeforeBorrow_throws() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 15);
        LocalDate returnDate = LocalDate.of(2025, 1, 10);
        loanService.borrowBook("S001", "B001", borrowDate);
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.returnBook("S001", "B001", returnDate)
        );
        assertTrue(exception.getMessage().contains("Return date cannot be before borrow date"));
    }

    @Test
    void getOverdueLoans_none() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        LocalDate asOfDate = LocalDate.of(2025, 1, 15);
        loanService.borrowBook("S001", "B001", borrowDate);
        
        List<History> overdueLoans = loanService.getOverdueLoans(asOfDate);
        
        assertTrue(overdueLoans.isEmpty());
    }

    @Test
    void getOverdueLoans_someOverdue() {
        LocalDate borrowDate1 = LocalDate.of(2025, 1, 1);
        LocalDate borrowDate2 = LocalDate.of(2025, 3, 1);
        LocalDate asOfDate = LocalDate.of(2025, 3, 15);
        
        loanService.borrowBook("S001", "B001", borrowDate1);
        loanService.borrowBook("S002", "B002", borrowDate2);
        
        List<History> overdueLoans = loanService.getOverdueLoans(asOfDate);
        
        assertEquals(1, overdueLoans.size());
        assertEquals("S001", overdueLoans.get(0).getStudentNo());
        assertEquals("B001", overdueLoans.get(0).getIssn());
    }

    @Test
    void getOverdueLoans_excludesReturnedLoans() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        LocalDate returnDate = LocalDate.of(2025, 3, 10);
        LocalDate asOfDate = LocalDate.of(2025, 3, 15);
        
        loanService.borrowBook("S001", "B001", borrowDate);
        loanService.returnBook("S001", "B001", returnDate);
        
        List<History> overdueLoans = loanService.getOverdueLoans(asOfDate);
        
        assertTrue(overdueLoans.isEmpty());
    }

    @Test
    void getAllHistory_returnsAll() {
        LocalDate borrowDate1 = LocalDate.of(2025, 1, 1);
        LocalDate borrowDate2 = LocalDate.of(2025, 2, 1);
        
        loanService.borrowBook("S001", "B001", borrowDate1);
        loanService.borrowBook("S002", "B002", borrowDate2);
        
        List<History> allHistory = loanService.getAllHistory();
        
        assertEquals(2, allHistory.size());
    }

    @Test
    void getHistoryByStudentNo_returnsOnlyMatching() {
        LocalDate borrowDate1 = LocalDate.of(2025, 1, 1);
        LocalDate borrowDate2 = LocalDate.of(2025, 2, 1);
        
        loanService.borrowBook("S001", "B001", borrowDate1);
        loanService.borrowBook("S002", "B002", borrowDate2);
        
        List<History> studentHistory = loanService.getHistoryByStudentNo("S001");
        
        assertEquals(1, studentHistory.size());
        assertEquals("S001", studentHistory.get(0).getStudentNo());
    }

    @Test
    void getHistoryByIssn_returnsOnlyMatching() {
        LocalDate borrowDate1 = LocalDate.of(2025, 1, 1);
        LocalDate borrowDate2 = LocalDate.of(2025, 2, 1);
        
        loanService.borrowBook("S001", "B001", borrowDate1);
        loanService.borrowBook("S002", "B002", borrowDate2);
        
        List<History> bookHistory = loanService.getHistoryByIssn("B001");
        
        assertEquals(1, bookHistory.size());
        assertEquals("B001", bookHistory.get(0).getIssn());
    }

    @Test
    void getPenaltyRate_returns0Point1() {
        assertEquals(0.1, loanService.getPenaltyRate());
    }

    @Test
    void getMaxBorrowDays_returns60() {
        assertEquals(60, loanService.getMaxBorrowDays());
    }

    @Test
    void borrowAndReturnSameDay_noPenalty() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        loanService.borrowBook("S001", "B001", date);
        
        History history = loanService.returnBook("S001", "B001", date);
        
        assertEquals(0.0, history.getPenalty());
    }

    @Test
    void multipleLoansForSameBook_afterReturn() {
        LocalDate borrowDate1 = LocalDate.of(2025, 1, 1);
        LocalDate returnDate1 = LocalDate.of(2025, 1, 15);
        LocalDate borrowDate2 = LocalDate.of(2025, 2, 1);
        
        loanService.borrowBook("S001", "B001", borrowDate1);
        loanService.returnBook("S001", "B001", returnDate1);
        
        History history2 = loanService.borrowBook("S002", "B001", borrowDate2);
        
        assertNotNull(history2);
        assertEquals("S002", history2.getStudentNo());
        
        Book book = bookRepository.findByIssn("B001").get();
        assertFalse(book.isAvailable());
        assertEquals("S002", book.getBorrowerNo());
    }
}
