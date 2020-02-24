package ru.sunlab.shopbasket.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ProductOutOfAllStoreException extends RuntimeException {
    public ProductOutOfAllStoreException(String message, Map<Long,Integer> map) {
        super(message);
        this.map = map;
    }
    private Map<Long,Integer> map;
}
