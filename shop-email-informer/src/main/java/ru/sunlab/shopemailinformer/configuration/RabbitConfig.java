package ru.sunlab.shopemailinformer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_EXT_SERVICES = "shop.ext.services";

    public static final String QUEUE_EMAIL = "queue-email";

    @Bean
    TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE_EXT_SERVICES);
    }
    @Bean
    Queue emailQueue(){
        return QueueBuilder.durable(QUEUE_EMAIL).build();
    }
    @Bean
    Binding bindingEmail(Queue emailQueue, TopicExchange topicExchange){
        return BindingBuilder.bind(emailQueue).to(topicExchange).with("*");
    }

    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter(new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true));
    }



}
