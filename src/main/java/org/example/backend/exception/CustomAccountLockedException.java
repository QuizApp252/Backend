package org.example.backend.exception;

public class CustomAccountLockedException extends RuntimeException{
    private String field;
    public CustomAccountLockedException(String field, String message){
        super(message);
        this.field=field;
    }
    public String getField() {
        return field;
    }
}
