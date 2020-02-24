package ru.sunlab.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCountDto {

    private Long id;

    private Long productId;

    private Integer quantity;

    private Long storeId;
}
