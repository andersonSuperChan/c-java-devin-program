package com.library.bms.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class Book {
    @NotBlank(message = "ISSN is required")
    private String issn;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String publisher;
    
    private String author;
    
    @Positive(message = "Price must be positive")
    private double price;
    
    private boolean available = true;
    
    private String borrowerNo;
    
    public Book() {}
    
    public Book(String issn, String title, String publisher, String author, double price) {
        this.issn = issn;
        this.title = title;
        this.publisher = publisher;
        this.author = author;
        this.price = price;
        this.available = true;
        this.borrowerNo = null;
    }
    
    public String getIssn() {
        return issn;
    }
    
    public void setIssn(String issn) {
        this.issn = issn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public String getBorrowerNo() {
        return borrowerNo;
    }
    
    public void setBorrowerNo(String borrowerNo) {
        this.borrowerNo = borrowerNo;
    }
}
