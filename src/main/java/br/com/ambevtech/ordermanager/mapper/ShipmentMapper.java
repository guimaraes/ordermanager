package br.com.ambevtech.ordermanager.mapper;

import br.com.ambevtech.ordermanager.dto.ShipmentResponseDTO;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.Shipment;
import br.com.ambevtech.ordermanager.model.enums.ShipmentStatus;

import java.time.LocalDateTime;

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
        return Shipment.builder()
                .order(order)
                .shippedDate(LocalDateTime.now())
                .trackingNumber(trackingNumber)
                .status(ShipmentStatus.IN_TRANSIT)
                .build();
    }
}
