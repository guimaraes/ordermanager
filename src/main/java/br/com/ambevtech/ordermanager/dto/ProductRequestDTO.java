package br.com.ambevtech.ordermanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "O nome do produto é obrigatório.") String name,
        @NotBlank(message = "A descrição do produto é obrigatória.") String description,
        @NotNull(message = "O preço do produto é obrigatório.") BigDecimal price,
        @NotNull(message = "O ID do fornecedor é obrigatório.") Long supplierId
) {
}
