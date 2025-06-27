package org.example.backend.exception;

import lombok.Getter;

@Getter
public class CustomBadRequestException extends RuntimeException{
    private final String field;
    public CustomBadRequestException(String field, String message){
        super(message);
        this.field=field;
    }
}
