package org.example.backend.exception;

public class CustomConflictException extends RuntimeException{
    private String field;
    public CustomConflictException(String field, String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
