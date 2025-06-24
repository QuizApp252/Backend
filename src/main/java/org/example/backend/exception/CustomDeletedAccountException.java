package org.example.backend.exception;

public class CustomDeletedAccountException extends RuntimeException{
    private String field;
    public CustomDeletedAccountException(String field, String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
