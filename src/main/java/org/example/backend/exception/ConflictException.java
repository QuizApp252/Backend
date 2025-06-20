package org.example.backend.exception;

public class ConflictException extends RuntimeException{
    private String field;
    public ConflictException(String field, String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
