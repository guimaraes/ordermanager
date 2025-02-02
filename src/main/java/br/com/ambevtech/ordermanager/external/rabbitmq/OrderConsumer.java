package br.com.ambevtech.ordermanager.external.rabbitmq;

import br.com.ambevtech.ordermanager.config.rabbitmq.RabbitMQConfig;
import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDERS)
    public void processOrder(String orderJson) {
        log.info("Recebendo JSON da fila: {}", orderJson);

        try {
            OrderRequestDTO order = new ObjectMapper().readValue(orderJson, OrderRequestDTO.class);
            orderService.createOrder(order);
        } catch (Exception e) {
            log.error("Erro ao processar pedido: {}", e.getMessage());
        }
    }
}