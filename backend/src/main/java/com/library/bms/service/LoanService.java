package com.library.bms.service;

import com.library.bms.entity.Book;
import com.library.bms.entity.History;
import com.library.bms.entity.Student;
import com.library.bms.repository.BookRepository;
import com.library.bms.repository.HistoryRepository;
import com.library.bms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    private static final double PENALTY_RATE = 0.1;
    private static final int MAX_BORROW_DAYS = 60;
    
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;
    private final HistoryRepository historyRepository;
    
    public LoanService(StudentRepository studentRepository, 
                       BookRepository bookRepository, 
                       HistoryRepository historyRepository) {
        this.studentRepository = studentRepository;
        this.bookRepository = bookRepository;
        this.historyRepository = historyRepository;
    }
    
    public History borrowBook(String studentNo, String issn, LocalDate borrowDate) {
        Student student = studentRepository.findByNo(studentNo)
                .orElseThrow(() -> new IllegalArgumentException("Student with ID " + studentNo + " not found"));
        
        Book book = bookRepository.findByIssn(issn)
                .orElseThrow(() -> new IllegalArgumentException("Book with ISSN " + issn + " not found"));
        
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book with ISSN " + issn + " is already borrowed by " + book.getBorrowerNo());
        }
        
        book.setAvailable(false);
        book.setBorrowerNo(studentNo);
        bookRepository.save(book);
        
        History history = new History(null, studentNo, issn, borrowDate);
        return historyRepository.save(history);
    }
    
    public History returnBook(String studentNo, String issn, LocalDate returnDate) {
        History history = historyRepository.findOpenLoan(studentNo, issn)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No open loan found for student " + studentNo + " and book " + issn));
        
        Book book = bookRepository.findByIssn(issn)
                .orElseThrow(() -> new IllegalArgumentException("Book with ISSN " + issn + " not found"));
        
        if (returnDate.isBefore(history.getBorrowDate())) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }
        
        long daysBorrowed = ChronoUnit.DAYS.between(history.getBorrowDate(), returnDate);
        double penalty = 0;
        if (daysBorrowed > MAX_BORROW_DAYS) {
            penalty = (daysBorrowed - MAX_BORROW_DAYS) * PENALTY_RATE;
        }
        
        history.setReturnDate(returnDate);
        history.setPenalty(penalty);
        historyRepository.save(history);
        
        book.setAvailable(true);
        book.setBorrowerNo(null);
        bookRepository.save(book);
        
        return history;
    }
    
    public List<History> getOverdueLoans(LocalDate asOfDate) {
        return historyRepository.findOpenLoans().stream()
                .filter(h -> {
                    long daysBorrowed = ChronoUnit.DAYS.between(h.getBorrowDate(), asOfDate);
                    return daysBorrowed > MAX_BORROW_DAYS;
                })
                .collect(Collectors.toList());
    }
    
    public List<History> getAllHistory() {
        return historyRepository.findAll();
    }
    
    public List<History> getHistoryByStudentNo(String studentNo) {
        return historyRepository.findByStudentNo(studentNo);
    }
    
    public List<History> getHistoryByIssn(String issn) {
        return historyRepository.findByIssn(issn);
    }
    
    public double getPenaltyRate() {
        return PENALTY_RATE;
    }
    
    public int getMaxBorrowDays() {
        return MAX_BORROW_DAYS;
    }
}
