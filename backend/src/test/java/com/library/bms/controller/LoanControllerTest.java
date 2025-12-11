package com.library.bms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bms.entity.History;
import com.library.bms.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void borrowBook_validRequest_returnsCreatedAndStatus201() throws Exception {
        History history = new History(1L, "S001", "B001", LocalDate.of(2025, 1, 1));
        when(loanService.borrowBook(eq("S001"), eq("B001"), any(LocalDate.class))).thenReturn(history);

        Map<String, String> request = new HashMap<>();
        request.put("studentNo", "S001");
        request.put("issn", "B001");
        request.put("borrowDate", "2025-01-01");

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentNo").value("S001"))
                .andExpect(jsonPath("$.issn").value("B001"));
    }

    @Test
    void borrowBook_missingFields_returnsStatus400() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("studentNo", "S001");

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void borrowBook_studentNotFound_returnsStatus400() throws Exception {
        when(loanService.borrowBook(eq("S999"), eq("B001"), any(LocalDate.class)))
                .thenThrow(new IllegalArgumentException("Student not found"));

        Map<String, String> request = new HashMap<>();
        request.put("studentNo", "S999");
        request.put("issn", "B001");
        request.put("borrowDate", "2025-01-01");

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Student not found"));
    }

    @Test
    void borrowBook_bookAlreadyBorrowed_returnsStatus409() throws Exception {
        when(loanService.borrowBook(eq("S001"), eq("B001"), any(LocalDate.class)))
                .thenThrow(new IllegalStateException("Book already borrowed"));

        Map<String, String> request = new HashMap<>();
        request.put("studentNo", "S001");
        request.put("issn", "B001");
        request.put("borrowDate", "2025-01-01");

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Book already borrowed"));
    }

    @Test
    void returnBook_validRequest_returnsStatus200() throws Exception {
        History history = new History(1L, "S001", "B001", LocalDate.of(2025, 1, 1));
        history.setReturnDate(LocalDate.of(2025, 2, 10));
        history.setPenalty(0.0);
        when(loanService.returnBook(eq("S001"), eq("B001"), any(LocalDate.class))).thenReturn(history);

        Map<String, String> request = new HashMap<>();
        request.put("studentNo", "S001");
        request.put("issn", "B001");
        request.put("returnDate", "2025-02-10");

        mockMvc.perform(post("/api/loans/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentNo").value("S001"))
                .andExpect(jsonPath("$.returnDate").value("2025-02-10"))
                .andExpect(jsonPath("$.penalty").value(0.0));
    }

    @Test
    void returnBook_withPenalty_returnsCorrectPenalty() throws Exception {
        History history = new History(1L, "S001", "B001", LocalDate.of(2025, 1, 1));
        history.setReturnDate(LocalDate.of(2025, 3, 15));
        history.setPenalty(1.3);
        when(loanService.returnBook(eq("S001"), eq("B001"), any(LocalDate.class))).thenReturn(history);

        Map<String, String> request = new HashMap<>();
        request.put("studentNo", "S001");
        request.put("issn", "B001");
        request.put("returnDate", "2025-03-15");

        mockMvc.perform(post("/api/loans/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.penalty").value(1.3));
    }

    @Test
    void returnBook_missingFields_returnsStatus400() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("studentNo", "S001");

        mockMvc.perform(post("/api/loans/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void returnBook_noOpenLoan_returnsStatus400() throws Exception {
        when(loanService.returnBook(eq("S001"), eq("B001"), any(LocalDate.class)))
                .thenThrow(new IllegalArgumentException("No open loan found"));

        Map<String, String> request = new HashMap<>();
        request.put("studentNo", "S001");
        request.put("issn", "B001");
        request.put("returnDate", "2025-02-10");

        mockMvc.perform(post("/api/loans/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No open loan found"));
    }

    @Test
    void getOverdueLoans_withDate_returnsOverdueLoans() throws Exception {
        History history = new History(1L, "S001", "B001", LocalDate.of(2025, 1, 1));
        when(loanService.getOverdueLoans(LocalDate.of(2025, 3, 15)))
                .thenReturn(Arrays.asList(history));

        mockMvc.perform(get("/api/loans/overdue")
                        .param("asOfDate", "2025-03-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentNo").value("S001"))
                .andExpect(jsonPath("$[0].issn").value("B001"));
    }

    @Test
    void getOverdueLoans_withoutDate_usesToday() throws Exception {
        when(loanService.getOverdueLoans(any(LocalDate.class)))
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/loans/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getConfig_returnsConfigValues() throws Exception {
        when(loanService.getPenaltyRate()).thenReturn(0.1);
        when(loanService.getMaxBorrowDays()).thenReturn(60);

        mockMvc.perform(get("/api/loans/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.penaltyRate").value(0.1))
                .andExpect(jsonPath("$.maxBorrowDays").value(60));
    }
}
