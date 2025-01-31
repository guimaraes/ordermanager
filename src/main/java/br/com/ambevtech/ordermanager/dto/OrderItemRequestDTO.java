package br.com.ambevtech.ordermanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
        @NotNull(message = "O ID do produto é obrigatório.")
        Long productId,

        @Min(value = 1, message = "A quantidade deve ser no mínimo 1.")
        int quantity
) {
}
