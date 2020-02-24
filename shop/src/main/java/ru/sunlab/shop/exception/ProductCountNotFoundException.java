package ru.sunlab.shop.exception;

public class ProductCountNotFoundException extends RuntimeException {
    public ProductCountNotFoundException(String message) {
        super(message);
    }
}
