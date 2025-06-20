package org.example.backend.exception;

public class RequestTooSoonException extends RuntimeException{
    private String field;
    public RequestTooSoonException(String field,String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
