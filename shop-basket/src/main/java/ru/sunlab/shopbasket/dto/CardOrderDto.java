package ru.sunlab.shopbasket.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
public class CardOrderDto {

    @NotNull
    private Long orderId;

    private BigDecimal cost;

    @NotNull
    @Digits(integer=16, fraction=0, message = "Не более 16-ти знаков")
    private Long cardNumber;

    @NotNull
    @Digits(integer=2, fraction=0, message = "Не более 2-х знаков")
    private Integer validMonth;

    @NotNull
    @Digits(integer=2, fraction=0, message = "Не более 2-х знаков")
    private Integer validYear;

    @NotEmpty
    @Size(min = 2, max = 22)
    private String cardHolderName;

    @NotNull
    @Digits(integer=3, fraction=0, message = "Не более 3-х знаков")
    private Integer cvc;
}
