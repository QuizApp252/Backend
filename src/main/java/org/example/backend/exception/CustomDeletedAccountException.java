package org.example.backend.exception;

import lombok.Getter;

@Getter
public class CustomDeletedAccountException extends RuntimeException{
    private final String field;
    public CustomDeletedAccountException(String field, String message){
        super(message);
        this.field=field;
    }
}
