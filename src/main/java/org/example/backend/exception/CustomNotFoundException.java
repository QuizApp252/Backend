package org.example.backend.exception;

import lombok.Getter;

@Getter
public class CustomNotFoundException extends RuntimeException{
    private final String field;
    public CustomNotFoundException(String field, String message){
        super(message);
        this.field=field;
    }
}
