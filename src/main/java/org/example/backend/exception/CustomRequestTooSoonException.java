package org.example.backend.exception;

public class CustomRequestTooSoonException extends RuntimeException{
    private String field;
    public CustomRequestTooSoonException(String field, String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
