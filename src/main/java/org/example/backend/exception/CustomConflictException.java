package org.example.backend.exception;

import lombok.Getter;

@Getter
public class CustomConflictException extends RuntimeException{
    private final String field;
    public CustomConflictException(String field, String message){
        super(message);
        this.field=field;
    }
}
