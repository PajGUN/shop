package ru.sunlab.shop.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductRmqDto {

    private Long productId;

    private Integer quantity;

    private Long storeId;

    private Long orderId;

    public ProductRmqDto(@JsonProperty("productId") Long productId,
                         @JsonProperty("quantity")Integer quantity,
                         @JsonProperty("storeId")Long storeId,
                         @JsonProperty("orderId")Long orderId) {
        this.productId = productId;
        this.quantity = quantity;
        this.storeId = storeId;
        this.orderId = orderId;
    }
}
