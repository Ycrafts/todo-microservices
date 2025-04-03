package com.profileService.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.user-registered}")
    private String userRegisteredExchange;

    @Value("${rabbitmq.queue.user-registered}")
    private String userRegisteredQueue;

    @Value("${rabbitmq.routing-key.user-registered}")
    private String userRegisteredRoutingKey;

    @Bean
    public TopicExchange userRegisteredExchange() {
        return new TopicExchange(userRegisteredExchange);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(userRegisteredQueue, true);
    }

    @Bean
    public Binding userRegisteredBinding(Queue userRegisteredQueue, TopicExchange userRegisteredExchange) {
        return BindingBuilder.bind(userRegisteredQueue).to(userRegisteredExchange).with(userRegisteredRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter consumerJackson2MessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();

        typeMapper.setIdClassMapping(Map.of(
                "UserRegisteredEvent", com.authService.events.UserRegisteredEvent.class
        ));

        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter consumerJackson2MessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(consumerJackson2MessageConverter);
        return factory;
    }
}