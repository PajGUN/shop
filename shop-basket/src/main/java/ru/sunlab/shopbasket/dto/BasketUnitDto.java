package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BasketUnitDto {

    @NotNull
    private Long clientId;

    @NotNull
    private Long productId;

    @NotEmpty
    @Size(min = 2, max = 200,message = "Не более 200 символов")
    private String productName;

    @NotNull
    private BigDecimal productPrice;

    @NotNull
    private List<ProductCountDto> productCountDtos;


}
