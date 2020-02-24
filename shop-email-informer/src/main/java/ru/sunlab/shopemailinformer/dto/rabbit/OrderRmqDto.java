package ru.sunlab.shopemailinformer.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderRmqDto{

    private Long orderId;

    private Long clientId;

    private String firstName;

    private String middleName;

    private String email;

    private String phoneNumber;

    private Long storeId;

    private String storeName;

    private LocalDateTime created;

    private BigDecimal summaryCost;

    //Если необходма доставка из другого магазина.
    private LocalDateTime deliveryTimes;

    private List<OrderItemRmqDto> orderItemRmqDtos;

    public OrderRmqDto(@JsonProperty("orderId") Long orderId,
                       @JsonProperty("clientId")Long clientId,
                       @JsonProperty("firstName")String firstName,
                       @JsonProperty("middleName") String middleName,
                       @JsonProperty("email") String email,
                       @JsonProperty("phoneNumber") String phoneNumber,
                       @JsonProperty("storeId")Long storeId,
                       @JsonProperty("storeName")String storeName,
                       @JsonProperty("created")LocalDateTime created,
                       @JsonProperty("summaryCost")BigDecimal summaryCost,
                       @JsonProperty("deliveryTimes")LocalDateTime deliveryTimes,
                       @JsonProperty("orderItemRmqDtos")List<OrderItemRmqDto> orderItemRmqDtos){
        this.orderId = orderId;
        this.clientId = clientId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.storeId = storeId;
        this.storeName = storeName;
        this.created = created;
        this.summaryCost = summaryCost;
        this.deliveryTimes = deliveryTimes;
        this.orderItemRmqDtos = orderItemRmqDtos;
    }
}
