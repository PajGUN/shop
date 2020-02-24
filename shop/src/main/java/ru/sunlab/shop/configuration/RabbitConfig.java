package ru.sunlab.shop.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_DIRECT = "shop.direct";

    public static final String QUEUE_PRODUCTS = "queue-product-count";
    public static final String QUEUE_BASKET = "queue-basket-count";

    @Bean
    DirectExchange directExchange(){
        return new DirectExchange(EXCHANGE_DIRECT);
    }
    @Bean
    Queue ordersQueue(){
        return QueueBuilder.durable(QUEUE_PRODUCTS).build();
    }
    @Bean
    Queue basketQueue(){
        return QueueBuilder.durable(QUEUE_BASKET).build();
    }
    @Bean
    Binding bindingOrders(Queue ordersQueue, DirectExchange directExchange){
        return BindingBuilder.bind(ordersQueue).to(directExchange).with(QUEUE_PRODUCTS);
    }
    @Bean
    Binding bindingBasket(Queue basketQueue, DirectExchange directExchange){
        return BindingBuilder.bind(basketQueue).to(directExchange).with(QUEUE_BASKET);
    }

    @Bean
    Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter(new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true));
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
