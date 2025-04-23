package com.thiago.demo_park_api.exception;

public class CodigoUniqueViolateException extends RuntimeException {
    public CodigoUniqueViolateException(String message) {
        super(message);
    }
}
