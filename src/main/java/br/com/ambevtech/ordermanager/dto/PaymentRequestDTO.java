package br.com.ambevtech.ordermanager.dto;

import br.com.ambevtech.ordermanager.model.enums.PaymentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestDTO(
        @NotNull(message = "O ID do pedido é obrigatório.")
        UUID orderId,

        @NotNull(message = "O método de pagamento é obrigatório.")
        String paymentMethod,

        @NotNull(message = "O valor pago é obrigatório.")
        @Min(value = 0, message = "O valor não pode ser negativo.")
        BigDecimal amountPaid,

        @NotNull(message = "O status do pagamento é obrigatório.")
        PaymentStatus status
) {
}
