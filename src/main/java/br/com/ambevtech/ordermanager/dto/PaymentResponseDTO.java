package br.com.ambevtech.ordermanager.dto;

import br.com.ambevtech.ordermanager.model.enums.PaymentStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID id,
        UUID orderId,
        String paymentMethod,
        BigDecimal amountPaid,
        PaymentStatus status
) {
}
