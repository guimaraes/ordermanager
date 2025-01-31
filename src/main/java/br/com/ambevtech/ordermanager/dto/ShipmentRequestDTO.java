package br.com.ambevtech.ordermanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ShipmentRequestDTO(
        @NotNull(message = "O ID do pedido é obrigatório.")
        UUID orderId,

        @NotNull(message = "O número de rastreamento é obrigatório.")
        @Size(min = 5, max = 50, message = "O número de rastreamento deve ter entre 5 e 50 caracteres.")
        String trackingNumber
) {
}
