package br.com.ambevtech.ordermanager.dto;

import br.com.ambevtech.ordermanager.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDTO(
        @NotNull(message = "O status do pedido é obrigatório.")
        OrderStatus newStatus,

        @NotNull(message = "O usuário responsável pela atualização é obrigatório.")
        String updatedBy
) {
}
