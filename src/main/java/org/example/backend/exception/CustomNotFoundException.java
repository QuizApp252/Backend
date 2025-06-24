package org.example.backend.exception;

public class CustomNotFoundException extends RuntimeException{
    private String field;
    public CustomNotFoundException(String field, String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
