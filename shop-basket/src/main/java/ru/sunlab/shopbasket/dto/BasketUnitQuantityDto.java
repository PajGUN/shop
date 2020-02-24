package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BasketUnitQuantityDto {
    @NotNull
    private Long clientId;

    @NotNull
    private Long productId;

    //если false то вычитаем, если true то прибавляем
    @NotNull
    private Boolean action;
}
