package com.library.bms.controller;

import com.library.bms.entity.History;
import com.library.bms.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class HistoryController {
    private final LoanService loanService;
    
    public HistoryController(LoanService loanService) {
        this.loanService = loanService;
    }
    
    @GetMapping
    public ResponseEntity<List<History>> getAllHistory(
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String issn) {
        if (studentNo != null) {
            return ResponseEntity.ok(loanService.getHistoryByStudentNo(studentNo));
        }
        if (issn != null) {
            return ResponseEntity.ok(loanService.getHistoryByIssn(issn));
        }
        return ResponseEntity.ok(loanService.getAllHistory());
    }
    
    @GetMapping("/student/{studentNo}")
    public ResponseEntity<List<History>> getHistoryByStudentNo(@PathVariable String studentNo) {
        return ResponseEntity.ok(loanService.getHistoryByStudentNo(studentNo));
    }
    
    @GetMapping("/book/{issn}")
    public ResponseEntity<List<History>> getHistoryByIssn(@PathVariable String issn) {
        return ResponseEntity.ok(loanService.getHistoryByIssn(issn));
    }
}
