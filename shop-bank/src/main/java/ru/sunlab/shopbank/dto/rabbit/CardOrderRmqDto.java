package ru.sunlab.shopbank.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CardOrderRmqDto {

    private Long orderId;

    private LocalDateTime orderCreatedTime;

    private BigDecimal summaryCost;

    private Long cardNumber;

    private Integer validMonth;

    private Integer validYear;

    private String cardHolderName;

    private Integer cvc;

    public CardOrderRmqDto(@JsonProperty("orderId") Long orderId,
                           @JsonProperty("orderCreatedTime") LocalDateTime orderCreatedTime,
                           @JsonProperty("summaryCost") BigDecimal summaryCost,
                           @JsonProperty("cardNumber") Long cardNumber,
                           @JsonProperty("validMonth") Integer validMonth,
                           @JsonProperty("validYear") Integer validYear,
                           @JsonProperty("cardHolderName") String cardHolderName,
                           @JsonProperty("cvc") Integer cvc) {
        this.orderId = orderId;
        this.orderCreatedTime = orderCreatedTime;
        this.summaryCost = summaryCost;
        this.cardNumber = cardNumber;
        this.validMonth = validMonth;
        this.validYear = validYear;
        this.cardHolderName = cardHolderName;
        this.cvc = cvc;
    }
}
