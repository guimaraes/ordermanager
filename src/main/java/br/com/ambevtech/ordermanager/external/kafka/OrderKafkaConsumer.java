package br.com.ambevtech.ordermanager.external.kafka;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.service.ExternalOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final ExternalOrderService externalOrderService;

    @KafkaListener(topics = "orders-topic", groupId = "order-group")
    public void processOrder(OrderRequestDTO order) {
        log.info("Pedido recebido via Kafka para processamento assíncrono: {}", order);
        externalOrderService.processExternalOrder(order);
    }
}
