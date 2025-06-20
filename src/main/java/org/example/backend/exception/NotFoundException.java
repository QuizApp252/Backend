package org.example.backend.exception;

public class NotFoundException extends RuntimeException{
    private String field;
    public NotFoundException(String field, String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
