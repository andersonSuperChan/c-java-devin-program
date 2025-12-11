package com.library.bms.service;

import com.library.bms.entity.Student;
import com.library.bms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public Optional<Student> getStudentByNo(String no) {
        return studentRepository.findByNo(no);
    }
    
    public Student createStudent(Student student) {
        if (studentRepository.existsByNo(student.getNo())) {
            throw new IllegalArgumentException("Student with ID " + student.getNo() + " already exists");
        }
        return studentRepository.save(student);
    }
    
    public Student updateStudent(String no, Student student) {
        if (!studentRepository.existsByNo(no)) {
            throw new IllegalArgumentException("Student with ID " + no + " not found");
        }
        student.setNo(no);
        return studentRepository.save(student);
    }
    
    public void deleteStudent(String no) {
        if (!studentRepository.existsByNo(no)) {
            throw new IllegalArgumentException("Student with ID " + no + " not found");
        }
        studentRepository.deleteByNo(no);
    }
    
    public boolean existsByNo(String no) {
        return studentRepository.existsByNo(no);
    }
}
