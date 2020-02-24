package ru.sunlab.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDto {
    private Long id;

    private Long articleNumber;

    private String type;

    private String name;

    private String description;

    private BigDecimal price;
}
