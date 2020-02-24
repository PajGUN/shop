package ru.sunlab.shopbasket.service.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.sunlab.shopbasket.configuration.RabbitConfig;
import ru.sunlab.shopbasket.dto.rabbit.CardOrderAnswerRmqDto;
import ru.sunlab.shopbasket.service.OrderService;

@Service
public class BankListener {

    private final OrderService orderService;
    @Autowired
    public BankListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_BANK_ANSWER)
    public void bankListener(CardOrderAnswerRmqDto cardOrderAnswerRmqDto, Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        orderService.bankListener(cardOrderAnswerRmqDto,channel,tag);
    }
}
