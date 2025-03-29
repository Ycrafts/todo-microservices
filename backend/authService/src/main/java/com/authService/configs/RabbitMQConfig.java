package com.authService.configs;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer(Jackson2JsonMessageConverter jsonMessageConverter) {
        return new RabbitTemplateCustomizer() {
            @Override
            public void customize(RabbitTemplate rabbitTemplate) {
                rabbitTemplate.setMessageConverter(jsonMessageConverter);
            }
        };
    }
}