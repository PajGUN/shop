package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BasketUnitDelDto {
    @NotNull
    private Long clientId;

    @NotNull
    private Long productId;

}
