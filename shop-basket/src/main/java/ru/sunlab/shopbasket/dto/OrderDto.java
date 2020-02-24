package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto {

    private Long orderNumber;

    private String store;

    private List<OrderItemDto> orderItemDtos;

    private BigDecimal summaryCost;

    //Если необходма доставка из другого магазина.
    private LocalDateTime deliveryTimes;
}
