package br.com.ambevtech.ordermanager.dto;

import br.com.ambevtech.ordermanager.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderHistoryRequestDTO(
        @NotNull(message = "O ID do pedido é obrigatório.")
        UUID orderId,

        @NotNull(message = "O status anterior é obrigatório.")
        OrderStatus oldStatus,

        @NotNull(message = "O novo status é obrigatório.")
        OrderStatus newStatus,

        @NotNull(message = "O usuário que realizou a alteração é obrigatório.")
        String updatedBy
) {
}
