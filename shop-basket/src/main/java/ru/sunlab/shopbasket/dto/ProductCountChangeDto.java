package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductCountChangeDto {

    //если false то вычитаем, если true то прибавляем
    @NotNull
    private Boolean action;

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    @NotNull
    private Long storeId;
}
