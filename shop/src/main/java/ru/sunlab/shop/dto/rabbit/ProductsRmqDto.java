package ru.sunlab.shop.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductsRmqDto {

    //если false то вычитаем, если true то прибавляем
    private Boolean action;

    private List<ProductRmqDto> productRmqDtos;

    public ProductsRmqDto(@JsonProperty("action") Boolean action,
                          @JsonProperty("productRmqDtos") List<ProductRmqDto> productRmqDtos) {
        this.action = action;
        this.productRmqDtos = productRmqDtos;
    }
}
