package com.library.bms.controller;

import com.library.bms.entity.History;
import com.library.bms.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {
    private final LoanService loanService;
    
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }
    
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody Map<String, String> request) {
        try {
            String studentNo = request.get("studentNo");
            String issn = request.get("issn");
            String borrowDateStr = request.get("borrowDate");
            
            if (studentNo == null || issn == null || borrowDateStr == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "studentNo, issn, and borrowDate are required"));
            }
            
            LocalDate borrowDate = LocalDate.parse(borrowDateStr);
            History history = loanService.borrowBook(studentNo, issn, borrowDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody Map<String, String> request) {
        try {
            String studentNo = request.get("studentNo");
            String issn = request.get("issn");
            String returnDateStr = request.get("returnDate");
            
            if (studentNo == null || issn == null || returnDateStr == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "studentNo, issn, and returnDate are required"));
            }
            
            LocalDate returnDate = LocalDate.parse(returnDateStr);
            History history = loanService.returnBook(studentNo, issn, returnDate);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<History>> getOverdueLoans(
            @RequestParam(required = false) String asOfDate) {
        LocalDate date = asOfDate != null ? LocalDate.parse(asOfDate) : LocalDate.now();
        return ResponseEntity.ok(loanService.getOverdueLoans(date));
    }
    
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(Map.of(
                "penaltyRate", loanService.getPenaltyRate(),
                "maxBorrowDays", loanService.getMaxBorrowDays()
        ));
    }
}
