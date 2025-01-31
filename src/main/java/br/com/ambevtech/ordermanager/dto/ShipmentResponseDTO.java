package br.com.ambevtech.ordermanager.dto;

import br.com.ambevtech.ordermanager.model.enums.ShipmentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentResponseDTO(
        UUID id,
        UUID orderId,
        LocalDateTime shippedDate,
        String trackingNumber,
        ShipmentStatus status
) {
}
