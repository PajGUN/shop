package ru.sunlab.shopbasket.service.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sunlab.shopbasket.configuration.RabbitConfig;
import ru.sunlab.shopbasket.dto.ProductCountChangeDto;
import ru.sunlab.shopbasket.exception.RabbitException;
import ru.sunlab.shopbasket.model.ProductCount;
import ru.sunlab.shopbasket.repository.ProductCountRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductListener {

    private final ProductCountRepository productCountRepository;
    @Autowired
    public ProductListener(ProductCountRepository productCountRepository) {
        this.productCountRepository = productCountRepository;
    }

    /*
    * Здесь обрабатывается изменения о количестве товара пришедшие со склада,
    * чтобы покупатель случайно не купил отсутствующий товар.
    * Если же при стечении обстоятельств такое произошло то данный случай
    * отработает на стороне склада!
    */
    @RabbitListener(queues = RabbitConfig.QUEUE_BASKET)
    @Transactional
    public void bankListener(List<ProductCountChangeDto> counts, Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        List<ProductCount> errorProdCounts = new ArrayList<>();
        List<ProductCount> forSave = new ArrayList<>(counts.size());

        for (ProductCountChangeDto count : counts) {
            ProductCount productCount = productCountRepository.
                    findByProductIdAndStoreId(count.getProductId(), count.getStoreId());
            if (productCount == null) {continue;}

            if (count.getAction()){
                productCount.setQuantity(productCount.getQuantity() + count.getQuantity());
            } else {
                Integer quantity = productCount.getQuantity() - count.getQuantity();
                if (quantity < 0) {
                    productCount.setQuantity(quantity);
                    errorProdCounts.add(productCount);
                    continue;
                }
            }
            forSave.add(productCount);
        }
        productCountRepository.saveAll(forSave);
        if (!errorProdCounts.isEmpty()){
            for (ProductCount errorPC : errorProdCounts) {
                log.error("Product id - "+ errorPC.getProductId() + " in Store - "+
                        errorPC.getStoreId() + " has negative quantity - "+errorPC.getQuantity());
            }
            /*
            * Так же можно отправить email, или отправить в брокер очередей или ещё как-то
            * проинформировать менеджеров что клиент не сможет купить товар
            */
        }
        try {
            channel.basicAck(tag,false);
        } catch (IOException e) {
            throw new RabbitException("Rabbit's basicReject not sent");
        }
    }
}
