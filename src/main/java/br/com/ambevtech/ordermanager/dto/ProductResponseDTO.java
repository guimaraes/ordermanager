package br.com.ambevtech.ordermanager.dto;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long supplierId
) {
}
