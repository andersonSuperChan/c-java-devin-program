package com.library.bms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bms.entity.Book;
import com.library.bms.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllBooks_returnsListAndStatus200() throws Exception {
        Book book1 = new Book("B001", "Book 1", "Publisher", "Author", 29.99);
        Book book2 = new Book("B002", "Book 2", "Publisher", "Author", 39.99);
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(book1, book2));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].issn").value("B001"))
                .andExpect(jsonPath("$[0].title").value("Book 1"))
                .andExpect(jsonPath("$[1].issn").value("B002"));
    }

    @Test
    void getBookByIssn_found_returnsBookAndStatus200() throws Exception {
        Book book = new Book("B001", "Test Book", "Publisher", "Author", 29.99);
        when(bookService.getBookByIssn("B001")).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/B001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.issn").value("B001"))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBookByIssn_notFound_returnsStatus404() throws Exception {
        when(bookService.getBookByIssn("B999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/B999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAvailableBooks_returnsOnlyAvailable() throws Exception {
        Book book1 = new Book("B001", "Book 1", "Publisher", "Author", 29.99);
        book1.setAvailable(true);
        when(bookService.getAvailableBooks()).thenReturn(Arrays.asList(book1));

        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].issn").value("B001"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void createBook_validRequest_returnsCreatedAndStatus201() throws Exception {
        Book book = new Book("B001", "Test Book", "Publisher", "Author", 29.99);
        book.setAvailable(true);
        when(bookService.createBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.issn").value("B001"))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void createBook_duplicateIssn_returnsStatus400() throws Exception {
        Book book = new Book("B001", "Test Book", "Publisher", "Author", 29.99);
        when(bookService.createBook(any(Book.class)))
                .thenThrow(new IllegalArgumentException("Book already exists"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBook_existing_returnsUpdatedAndStatus200() throws Exception {
        Book book = new Book("B001", "Updated Title", "New Publisher", "New Author", 49.99);
        when(bookService.updateBook(eq("B001"), any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/api/books/B001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void updateBook_notFound_returnsStatus404() throws Exception {
        Book book = new Book("B999", "Test Book", "Publisher", "Author", 29.99);
        when(bookService.updateBook(eq("B999"), any(Book.class)))
                .thenThrow(new IllegalArgumentException("Book not found"));

        mockMvc.perform(put("/api/books/B999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_existing_returnsStatus204() throws Exception {
        doNothing().when(bookService).deleteBook("B001");

        mockMvc.perform(delete("/api/books/B001"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_notFound_returnsStatus404() throws Exception {
        doThrow(new IllegalArgumentException("Book not found"))
                .when(bookService).deleteBook("B999");

        mockMvc.perform(delete("/api/books/B999"))
                .andExpect(status().isNotFound());
    }
}
