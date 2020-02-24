package ru.sunlab.shop.service.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sunlab.shop.configuration.RabbitConfig;
import ru.sunlab.shop.dto.rabbit.ProductsRmqDto;
import ru.sunlab.shop.service.ProductCountService;

@Service
public class ProductCountListener {

    private final ProductCountService productCountService;
    @Autowired
    public ProductCountListener(ProductCountService productCountService) {
        this.productCountService = productCountService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_PRODUCTS)
    public void processingOrderItems(ProductsRmqDto products, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        productCountService.processingOrderItems(products, channel ,tag);
    }

}
