package ru.sunlab.shopbasket.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemRmqDto {

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal price;

    private Long storeId;

    public OrderItemRmqDto(@JsonProperty("productId") Long productId,
                           @JsonProperty("productName") String productName,
                           @JsonProperty("quantity")Integer quantity,
                           @JsonProperty("price")BigDecimal price,
                           @JsonProperty("storeId")Long storeId) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.storeId = storeId;
    }
}
