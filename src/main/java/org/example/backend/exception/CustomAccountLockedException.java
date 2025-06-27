package org.example.backend.exception;

import lombok.Getter;

@Getter
public class CustomAccountLockedException extends RuntimeException{
    private final String field;
    public CustomAccountLockedException(String field, String message){
        super(message);
        this.field=field;
    }
}
