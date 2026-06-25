package com.student.exception;

public class DuplicateStudentException extends Exception {
    public DuplicateStudentException(String message){
        super(message);
    }
}
