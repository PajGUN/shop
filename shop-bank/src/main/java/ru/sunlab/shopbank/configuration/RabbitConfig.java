package ru.sunlab.shopbank.configuration;

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

    public static final String QUEUE_BANK = "queue-bank";
    public static final String QUEUE_BANK_ANSWER = "queue-bank-answer";

    @Bean
    DirectExchange directExchange(){
        return new DirectExchange(EXCHANGE_DIRECT);
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
