package ru.sunlab.shopbank.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.sunlab.shopbank.configuration.RabbitConfig;
import ru.sunlab.shopbank.dto.rabbit.CardOrderAnswerRmqDto;
import ru.sunlab.shopbank.dto.rabbit.CardOrderRmqDto;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class RmqListener {

    // if true then always confirms orders
    public boolean confirm = true;

    private final RabbitTemplate rabbitTemplate;
    @Autowired
    public RmqListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_BANK)
    public void listener(CardOrderRmqDto cardOrder, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag){
        log.info("Bank get message. OrderId - " + cardOrder.getOrderId());
        boolean before = cardOrder.getOrderCreatedTime().minusMinutes(20).isBefore(LocalDateTime.now());
        log.info("Time of receiving the message before closing the order? - " + before);
        CardOrderAnswerRmqDto cardOrderAnswer = new CardOrderAnswerRmqDto();
        cardOrderAnswer.setOrderId(cardOrder.getOrderId());
        if (confirm && before) {
            log.info("Result transaction - approve" );
            cardOrderAnswer.setResult(true);
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_DIRECT,
                    RabbitConfig.QUEUE_BANK_ANSWER, cardOrderAnswer);
            try {
                channel.basicAck(tag, false);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            log.info("Result transaction - reject" );
            log.info("Cause - not enough money" );
            cardOrderAnswer.setResult(false);
            cardOrderAnswer.setErrorReason("Not enough money!");
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_DIRECT,
                    RabbitConfig.QUEUE_BANK_ANSWER, cardOrderAnswer);
            try {
                channel.basicAck(tag, false);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
