package com.library.bms.repository;

import com.library.bms.entity.Student;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StudentRepository {
    private final Map<String, Student> students = new ConcurrentHashMap<>();
    
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }
    
    public Optional<Student> findByNo(String no) {
        return Optional.ofNullable(students.get(no));
    }
    
    public Student save(Student student) {
        students.put(student.getNo(), student);
        return student;
    }
    
    public boolean existsByNo(String no) {
        return students.containsKey(no);
    }
    
    public void deleteByNo(String no) {
        students.remove(no);
    }
    
    public void deleteAll() {
        students.clear();
    }
}
