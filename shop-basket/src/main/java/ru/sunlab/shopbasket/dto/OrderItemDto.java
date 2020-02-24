package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDto {

    private String productName;

    private Integer quantity;

    private BigDecimal price;
}
