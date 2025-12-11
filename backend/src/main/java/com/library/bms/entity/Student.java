package com.library.bms.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Student {
    @NotBlank(message = "Student ID is required")
    private String no;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String classNo;
    
    private String phoneNumber;
    
    @Pattern(regexp = "[MFmf]", message = "Gender must be M or F")
    private String gender;
    
    public Student() {}
    
    public Student(String no, String name, String classNo, String phoneNumber, String gender) {
        this.no = no;
        this.name = name;
        this.classNo = classNo;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }
    
    public String getNo() {
        return no;
    }
    
    public void setNo(String no) {
        this.no = no;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getClassNo() {
        return classNo;
    }
    
    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
}
