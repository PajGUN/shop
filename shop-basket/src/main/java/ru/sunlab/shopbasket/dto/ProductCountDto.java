package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductCountDto {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    @NotNull
    private Long storeId;
}
