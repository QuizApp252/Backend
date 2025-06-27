package org.example.backend.exception;

import lombok.Getter;

@Getter
public class CustomRequestTooSoonException extends RuntimeException{
    private final String field;
    public CustomRequestTooSoonException(String field, String message){
        super(message);
        this.field=field;
    }
}
