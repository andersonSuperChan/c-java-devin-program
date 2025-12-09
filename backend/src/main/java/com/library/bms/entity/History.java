package com.library.bms.entity;

import java.time.LocalDate;

public class History {
    private Long id;
    private String studentNo;
    private String issn;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private double penalty;
    
    public History() {}
    
    public History(Long id, String studentNo, String issn, LocalDate borrowDate) {
        this.id = id;
        this.studentNo = studentNo;
        this.issn = issn;
        this.borrowDate = borrowDate;
        this.returnDate = null;
        this.penalty = 0;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStudentNo() {
        return studentNo;
    }
    
    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }
    
    public String getIssn() {
        return issn;
    }
    
    public void setIssn(String issn) {
        this.issn = issn;
    }
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public double getPenalty() {
        return penalty;
    }
    
    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }
    
    public boolean isReturned() {
        return returnDate != null;
    }
}
