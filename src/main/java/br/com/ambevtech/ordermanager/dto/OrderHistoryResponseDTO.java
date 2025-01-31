package br.com.ambevtech.ordermanager.dto;

import br.com.ambevtech.ordermanager.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderHistoryResponseDTO(
        UUID id,
        UUID orderId,
        LocalDateTime timestamp,
        OrderStatus oldStatus,
        OrderStatus newStatus,
        String updatedBy
) {
}
