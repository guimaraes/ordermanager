package br.com.ambevtech.ordermanager.dto;

import br.com.ambevtech.ordermanager.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderRequestDTO(
        @NotNull(message = "O ID do cliente é obrigatório.")
        UUID customerId,

        @NotNull(message = "A lista de itens do pedido não pode ser vazia.")
        List<OrderItemRequestDTO> items,

        @NotNull(message = "O status do pedido é obrigatório.")
        OrderStatus status,

        String externalOrderId
) {
}

