package ru.sunlab.shopbasket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_DIRECT = "shop.direct";
    public static final String EXCHANGE_EXT_SERVICES = "shop.ext.services";

    public static final String QUEUE_PRODUCTS = "queue-product-count";
    public static final String QUEUE_BASKET = "queue-basket-count";
    public static final String QUEUE_BANK = "queue-bank";
    public static final String QUEUE_BANK_ANSWER = "queue-bank-answer";
    public static final String QUEUE_1C = "queue-1c";
    public static final String QUEUE_EMAIL = "queue-email";
    public static final String QUEUE_DELIVERY = "queue-delivery";

    @Bean
    DirectExchange directExchange(){
        return new DirectExchange(EXCHANGE_DIRECT);
    }
    @Bean
    Queue ordersQueue(){
        return QueueBuilder.durable(QUEUE_PRODUCTS).build();
    }
    @Bean
    Binding bindingOrders(Queue ordersQueue, DirectExchange directExchange){
        return BindingBuilder.bind(ordersQueue).to(directExchange).with(QUEUE_PRODUCTS);
    }

    @Bean
    Queue basketQueue(){
        return QueueBuilder.durable(QUEUE_BASKET).build();
    }
    @Bean
    Binding bindingBasket(Queue basketQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(basketQueue).to(directExchange).with(QUEUE_BASKET);
    }


    @Bean
    Queue bankQueue(){
        return QueueBuilder.durable(QUEUE_BANK).build();
    }
    @Bean
    Binding bindingBank(Queue bankQueue, DirectExchange directExchange){
        return BindingBuilder.bind(bankQueue).to(directExchange).with(QUEUE_BANK);
    }

    @Bean
    Queue bankAnswerQueue() {return QueueBuilder.durable(QUEUE_BANK_ANSWER).build();}
    @Bean
    Binding bindingBankAnswer(Queue bankAnswerQueue, DirectExchange directExchange){
        return BindingBuilder.bind(bankAnswerQueue).to(directExchange).with(QUEUE_BANK_ANSWER);
    }



    @Bean
    TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE_EXT_SERVICES);
    }
    @Bean
    Queue oneCQueue(){
        return QueueBuilder.durable(QUEUE_1C).build();
    }
    @Bean
    Queue emailQueue(){
        return QueueBuilder.durable(QUEUE_EMAIL).build();
    }
    @Bean
    Queue deliveryQueue(){
        return QueueBuilder.durable(QUEUE_DELIVERY).build();
    }
    @Bean
    Binding binding1C(Queue oneCQueue, TopicExchange topicExchange){
        return BindingBuilder.bind(oneCQueue).to(topicExchange).with("*");
    }
    @Bean
    Binding bindingEmail(Queue emailQueue, TopicExchange topicExchange){
        return BindingBuilder.bind(emailQueue).to(topicExchange).with("*");
    }
    @Bean
    Binding bindingDelivery(Queue deliveryQueue, TopicExchange topicExchange){
        return BindingBuilder.bind(deliveryQueue).to(topicExchange).with("delivery");
    }


    @Bean
    MessageConverter messageConverter(){
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
