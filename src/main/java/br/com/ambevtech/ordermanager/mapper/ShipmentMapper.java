package br.com.ambevtech.ordermanager.mapper;

import br.com.ambevtech.ordermanager.dto.ShipmentResponseDTO;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.Shipment;
import br.com.ambevtech.ordermanager.model.enums.ShipmentStatus;

public class ShipmentMapper {

    public static ShipmentResponseDTO toResponseDTO(Shipment shipment) {
        return new ShipmentResponseDTO(
                shipment.getId(),
                shipment.getOrder().getId(),
                shipment.getShippedDate(),
                shipment.getTrackingNumber(),
                shipment.getStatus()
        );
    }

    public static Shipment toEntity(Order order, String trackingNumber) {
        return new Shipment(
                null,
                order,
                java.time.LocalDateTime.now(),
                trackingNumber,
                ShipmentStatus.IN_TRANSIT
        );
    }
}
