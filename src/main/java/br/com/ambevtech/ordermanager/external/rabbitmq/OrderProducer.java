package br.com.ambevtech.ordermanager.external.rabbitmq;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderToQueue(OrderRequestDTO order) {
        log.info("Enviando pedido para fila: {}", order);
        rabbitTemplate.convertAndSend("orders.exchange", "orders.routingKey", order);
    }
}
