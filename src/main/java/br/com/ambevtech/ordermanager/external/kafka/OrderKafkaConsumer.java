package br.com.ambevtech.ordermanager.external.kafka;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.service.ExternalOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final ExternalOrderService externalOrderService;
    private final KafkaTemplate<String, OrderRequestDTO> kafkaTemplate;

    private static final int MAX_RETRIES = 3;
    private static final String DLQ_TOPIC = "orders-topic-dlq";

    @KafkaListener(topics = "orders-topic", groupId = "order-group")
    public void processOrder(OrderRequestDTO order) {
        log.info("Recebendo pedido via Kafka: {}", order);

        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                externalOrderService.processExternalOrder(order);
                return; // Se der certo, sai do loop
            } catch (Exception e) {
                retryCount++;
                log.warn("Erro ao processar pedido {}. Tentativa {}/{}", order.externalOrderId(), retryCount, MAX_RETRIES, e);

                if (retryCount >= MAX_RETRIES) {
                    log.error("Pedido {} falhou após todas as tentativas. Enviando para DLQ.", order.externalOrderId());
                    sendToDLQ(order, e.getMessage()); // Enviar para DLQ com erro
                }
            }
        }
    }

    private void sendToDLQ(OrderRequestDTO order, String errorMessage) {
        log.info("Enviando pedido {} para DLQ devido a erro: {}", order.externalOrderId(), errorMessage);

        try {
            kafkaTemplate.send(DLQ_TOPIC, order)
                    .whenComplete((result, exception) -> {
                        if (exception == null) {
                            log.info("Pedido {} enviado com sucesso para DLQ! Offset: {}",
                                    order.externalOrderId(), result.getRecordMetadata().offset());
                        } else {
                            log.error("Erro ao enviar pedido {} para DLQ: {}", order.externalOrderId(), exception.getMessage());
                        }
                    });
        } catch (Exception ex) {
            log.error("Falha crítica ao tentar enviar pedido {} para DLQ: {}", order.externalOrderId(), ex.getMessage());
        }
    }
}

