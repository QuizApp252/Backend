package org.example.backend.exception;

public class CustomBadRequestException extends RuntimeException{
    private String field;
    public CustomBadRequestException(String field, String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
