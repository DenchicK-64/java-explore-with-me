package ru.practicum.main.exceptions;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}