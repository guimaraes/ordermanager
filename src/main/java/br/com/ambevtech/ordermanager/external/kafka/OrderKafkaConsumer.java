package br.com.ambevtech.ordermanager.external.kafka;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrderKafkaConsumer {
    private final OrderService orderService;
    @KafkaListener(topics = "orders-topic", groupId = "order-group")
    public void processOrder(OrderRequestDTO order) {
        log.info("Pedido recebido via Kafka: {}", order);
        orderService.createOrder(order);
    }
}
