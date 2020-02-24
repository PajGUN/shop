package ru.sunlab.shopbank.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardOrderAnswerRmqDto {

    private Long orderId;
    private Boolean result;
    private String errorReason;

    public CardOrderAnswerRmqDto(@JsonProperty("orderId") Long orderId,
                                 @JsonProperty("result") Boolean result,
                                 @JsonProperty("errorReason") String errorReason) {
        this.orderId = orderId;
        this.result = result;
        this.errorReason = errorReason;
    }
}
