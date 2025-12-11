package com.library.bms.service;

import com.library.bms.entity.Student;
import com.library.bms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {
    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentRepository = new StudentRepository();
        studentService = new StudentService(studentRepository);
    }

    @Test
    void createStudent_success() {
        Student student = new Student("S001", "John Doe", "Class1", "123456789", "M");
        
        Student created = studentService.createStudent(student);
        
        assertNotNull(created);
        assertEquals("S001", created.getNo());
        assertEquals("John Doe", created.getName());
        assertTrue(studentService.existsByNo("S001"));
    }

    @Test
    void createStudent_duplicateId_throws() {
        Student student1 = new Student("S001", "John Doe", "Class1", "123456789", "M");
        studentService.createStudent(student1);
        
        Student student2 = new Student("S001", "Jane Doe", "Class2", "987654321", "F");
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.createStudent(student2)
        );
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void getStudentByNo_found() {
        Student student = new Student("S001", "John Doe", "Class1", "123456789", "M");
        studentService.createStudent(student);
        
        Optional<Student> found = studentService.getStudentByNo("S001");
        
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void getStudentByNo_notFound() {
        Optional<Student> found = studentService.getStudentByNo("S999");
        
        assertFalse(found.isPresent());
    }

    @Test
    void getAllStudents_returnsAll() {
        studentService.createStudent(new Student("S001", "John Doe", "Class1", "123456789", "M"));
        studentService.createStudent(new Student("S002", "Jane Doe", "Class2", "987654321", "F"));
        
        List<Student> students = studentService.getAllStudents();
        
        assertEquals(2, students.size());
    }

    @Test
    void updateStudent_success() {
        studentService.createStudent(new Student("S001", "John Doe", "Class1", "123456789", "M"));
        
        Student updated = new Student("S001", "John Updated", "Class2", "111111111", "M");
        Student result = studentService.updateStudent("S001", updated);
        
        assertEquals("John Updated", result.getName());
        assertEquals("Class2", result.getClassNo());
        assertEquals("111111111", result.getPhoneNumber());
    }

    @Test
    void updateStudent_notFound_throws() {
        Student student = new Student("S999", "John Doe", "Class1", "123456789", "M");
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.updateStudent("S999", student)
        );
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void deleteStudent_success() {
        studentService.createStudent(new Student("S001", "John Doe", "Class1", "123456789", "M"));
        assertTrue(studentService.existsByNo("S001"));
        
        studentService.deleteStudent("S001");
        
        assertFalse(studentService.existsByNo("S001"));
    }

    @Test
    void deleteStudent_notFound_throws() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.deleteStudent("S999")
        );
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void existsByNo_returnsTrue_whenExists() {
        studentService.createStudent(new Student("S001", "John Doe", "Class1", "123456789", "M"));
        
        assertTrue(studentService.existsByNo("S001"));
    }

    @Test
    void existsByNo_returnsFalse_whenNotExists() {
        assertFalse(studentService.existsByNo("S999"));
    }
}
