package com.profileService.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_REGISTERED_EXCHANGE = "user.registered.exchange";
    public static final String USER_REGISTERED_QUEUE = "user.registered.queue"; // <--- Check this
    public static final String USER_REGISTERED_ROUTING_KEY = "user.registered";

    @Bean
    public DirectExchange userRegisteredExchange() {
        return new DirectExchange(USER_REGISTERED_EXCHANGE);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(USER_REGISTERED_QUEUE);
    }

    @Bean
    public Binding userRegisteredBinding(Queue userRegisteredQueue, DirectExchange userRegisteredExchange) {
        return BindingBuilder.bind(userRegisteredQueue).to(userRegisteredExchange()).with(USER_REGISTERED_ROUTING_KEY);
    }
}