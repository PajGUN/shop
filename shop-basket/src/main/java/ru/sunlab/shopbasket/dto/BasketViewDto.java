package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BasketViewDto {

    private String productName;

    private BigDecimal productPrice;

    private Integer quantity;
}
