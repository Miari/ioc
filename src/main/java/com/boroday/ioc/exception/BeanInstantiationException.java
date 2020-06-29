package com.boroday.ioc.exception;

public class BeanInstantiationException extends RuntimeException {

    public BeanInstantiationException(String message) {
        super(message);
    }

    public BeanInstantiationException(String message, Exception e) {
        super(message, e);
    }

}
