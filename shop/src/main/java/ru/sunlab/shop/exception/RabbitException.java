package ru.sunlab.shop.exception;

public class RabbitException extends RuntimeException {
    public RabbitException(String message) {
        super(message);
    }
}
