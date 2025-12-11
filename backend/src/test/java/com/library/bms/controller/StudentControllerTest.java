package com.library.bms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bms.entity.Student;
import com.library.bms.service.StudentService;
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

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllStudents_returnsListAndStatus200() throws Exception {
        Student student1 = new Student("S001", "John Doe", "Class1", "123456789", "M");
        Student student2 = new Student("S002", "Jane Doe", "Class2", "987654321", "F");
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].no").value("S001"))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].no").value("S002"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));
    }

    @Test
    void getStudentByNo_found_returnsStudentAndStatus200() throws Exception {
        Student student = new Student("S001", "John Doe", "Class1", "123456789", "M");
        when(studentService.getStudentByNo("S001")).thenReturn(Optional.of(student));

        mockMvc.perform(get("/api/students/S001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.no").value("S001"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getStudentByNo_notFound_returnsStatus404() throws Exception {
        when(studentService.getStudentByNo("S999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/students/S999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStudent_validRequest_returnsCreatedAndStatus201() throws Exception {
        Student student = new Student("S001", "John Doe", "Class1", "123456789", "M");
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.no").value("S001"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void createStudent_duplicateId_returnsStatus400() throws Exception {
        Student student = new Student("S001", "John Doe", "Class1", "123456789", "M");
        when(studentService.createStudent(any(Student.class)))
                .thenThrow(new IllegalArgumentException("Student already exists"));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStudent_existing_returnsUpdatedAndStatus200() throws Exception {
        Student student = new Student("S001", "John Updated", "Class2", "111111111", "M");
        when(studentService.updateStudent(eq("S001"), any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/api/students/S001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    @Test
    void updateStudent_notFound_returnsStatus404() throws Exception {
        Student student = new Student("S999", "John Doe", "Class1", "123456789", "M");
        when(studentService.updateStudent(eq("S999"), any(Student.class)))
                .thenThrow(new IllegalArgumentException("Student not found"));

        mockMvc.perform(put("/api/students/S999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent_existing_returnsStatus204() throws Exception {
        doNothing().when(studentService).deleteStudent("S001");

        mockMvc.perform(delete("/api/students/S001"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteStudent_notFound_returnsStatus404() throws Exception {
        doThrow(new IllegalArgumentException("Student not found"))
                .when(studentService).deleteStudent("S999");

        mockMvc.perform(delete("/api/students/S999"))
                .andExpect(status().isNotFound());
    }
}
