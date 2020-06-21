package com.boroday.dependencyinjection.exception;

public class BeanInstantiationException extends RuntimeException {

    public BeanInstantiationException(String message, Exception e) {
        super(message, e);
    }

}
