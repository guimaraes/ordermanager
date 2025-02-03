package br.com.ambevtech.ordermanager.external.kafka;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, OrderRequestDTO> kafkaTemplate;
    private static final String TOPIC = "orders-topic";

    public void sendOrder(OrderRequestDTO order) {
        log.info("Enviando pedido para o Kafka: {}", order);

        kafkaTemplate.send(TOPIC, order)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Pedido {} enviado com sucesso! Offset: {}", order.externalOrderId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Erro ao enviar pedido {} para Kafka: {}", order.externalOrderId(), exception.getMessage());
                    }
                });
    }
}

