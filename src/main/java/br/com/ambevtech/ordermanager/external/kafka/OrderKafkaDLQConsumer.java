package br.com.ambevtech.ordermanager.external.kafka;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.model.OrderErrorLog;
import br.com.ambevtech.ordermanager.repository.OrderErrorLogRepository;
import br.com.ambevtech.ordermanager.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaDLQConsumer {

    private final OrderErrorLogRepository orderErrorLogRepository;
    private final MonitoringService monitoringService;

    @KafkaListener(topics = "orders-topic-dlq", groupId = "order-group")
    public void processDeadLetterQueue(OrderRequestDTO order) {
        log.error("Mensagem movida para DLQ! Pedido com ID externo: {}", order.externalOrderId());

        try {
            // Persistindo erro no banco de dados
            OrderErrorLog errorLog = OrderErrorLog.builder()
                    .externalOrderId(order.externalOrderId())
                    .errorMessage("Pedido movido para DLQ após falhas consecutivas.")
                    .timestamp(LocalDateTime.now())
                    .build();

            orderErrorLogRepository.save(errorLog);
            log.info("Erro salvo no banco para pedido: {}", order.externalOrderId());

            // Enviar alerta para ferramenta de monitoramento (Sentry, Grafana, Prometheus)
            monitoringService.sendAlert(
                    "Pedido movido para DLQ",
                    "O pedido com ID " + order.externalOrderId() + " falhou em todas as tentativas e foi enviado para DLQ.",
                    "CRITICAL"
            );

        } catch (Exception e) {
            log.error("Erro ao processar mensagem na DLQ: {}", e.getMessage(), e);
        }
    }
}
