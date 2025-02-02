package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.PaymentRequestDTO;
import br.com.ambevtech.ordermanager.dto.PaymentResponseDTO;
import br.com.ambevtech.ordermanager.model.enums.PaymentStatus;
import br.com.ambevtech.ordermanager.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID id) {
        log.info("Recebendo solicitação para buscar pagamento com ID: {}", id);
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable UUID orderId) {
        log.info("Recebendo solicitação para buscar pagamento do pedido ID: {}", orderId);
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status,
            @RequestParam BigDecimal amountPaid,
            @RequestParam String paymentMethod) {

        log.info("Recebendo solicitação para atualizar status do pagamento ID: {}", id);
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, status, amountPaid, paymentMethod));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO dto) {
        log.info("Recebendo solicitação para criar um novo pagamento.");
        return ResponseEntity.ok(paymentService.createPayment(dto));
    }
}
