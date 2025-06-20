package org.example.backend.exception;

public class BadRequestException extends RuntimeException{
    private String field;
    public BadRequestException(String field, String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
