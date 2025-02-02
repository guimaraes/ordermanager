package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.dto.PaymentRequestDTO;
import br.com.ambevtech.ordermanager.dto.PaymentResponseDTO;
import br.com.ambevtech.ordermanager.exception.OrderNotFoundException;
import br.com.ambevtech.ordermanager.exception.PaymentNotFoundException;
import br.com.ambevtech.ordermanager.mapper.PaymentMapper;
import br.com.ambevtech.ordermanager.mapper.ShipmentMapper;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.Payment;
import br.com.ambevtech.ordermanager.model.Shipment;
import br.com.ambevtech.ordermanager.model.enums.PaymentStatus;
import br.com.ambevtech.ordermanager.repository.OrderRepository;
import br.com.ambevtech.ordermanager.repository.PaymentRepository;
import br.com.ambevtech.ordermanager.repository.ShipmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;

    public PaymentResponseDTO getPaymentById(UUID id) {
        log.info("Buscando pagamento com ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento não encontrado: " + id));

        return PaymentMapper.toResponseDTO(payment);
    }

    public PaymentResponseDTO getPaymentByOrderId(UUID orderId) {
        log.info("Buscando pagamento para o pedido ID: {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento não encontrado para o pedido ID: " + orderId));

        return PaymentMapper.toResponseDTO(payment);
    }

    @Transactional
    public PaymentResponseDTO updatePaymentStatus(UUID paymentId, PaymentStatus status, BigDecimal amountPaid, String paymentMethod) {
        log.info("Atualizando pagamento com ID: {} para status: {}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento não encontrado: " + paymentId));

        payment.setStatus(status);
        payment.setAmountPaid(amountPaid);
        payment.setPaymentMethod(paymentMethod);

        paymentRepository.save(payment);

        log.info("Pagamento atualizado com sucesso! ID: {}", payment.getId());

        if (status == PaymentStatus.APPROVED) {
            log.info("Pagamento aprovado! Criando entrega para o pedido ID: {}", payment.getOrder().getId());

            Shipment shipment = ShipmentMapper.toEntity(payment.getOrder(), generateTrackingNumber());
            shipment = shipmentRepository.save(shipment);

            log.info("Entrega criada com sucesso! ID: {} - Rastreamento: {}", shipment.getId(), shipment.getTrackingNumber());
        }

        return PaymentMapper.toResponseDTO(payment);
    }

    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO dto) {
        log.info("Criando pagamento para o pedido ID: {}", dto.orderId());

        Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado: " + dto.orderId()));

        Payment payment = PaymentMapper.toEntity(dto, order);
        payment = paymentRepository.save(payment);

        log.info("Pagamento criado com sucesso! ID: {}", payment.getId());
        return PaymentMapper.toResponseDTO(payment);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
