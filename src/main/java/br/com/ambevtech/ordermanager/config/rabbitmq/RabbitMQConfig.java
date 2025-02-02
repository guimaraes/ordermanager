package br.com.ambevtech.ordermanager.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_ORDERS = "orders.queue";
    public static final String EXCHANGE_ORDERS = "orders.exchange";
    public static final String ROUTING_KEY_ORDERS = "orders.routingKey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_ORDERS, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_ORDERS);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_ORDERS);
    }
}
