package br.com.ambevtech.ordermanager.mapper;

import br.com.ambevtech.ordermanager.dto.OrderHistoryRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderHistoryResponseDTO;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.OrderHistory;

import java.time.LocalDateTime;

public class OrderHistoryMapper {

    public static OrderHistoryResponseDTO toResponseDTO(OrderHistory orderHistory) {
        return new OrderHistoryResponseDTO(
                orderHistory.getId(),
                orderHistory.getOrder().getId(),
                orderHistory.getTimestamp(),
                orderHistory.getOldStatus(),
                orderHistory.getNewStatus(),
                orderHistory.getUpdatedBy()
        );
    }

    public static OrderHistory toEntity(OrderHistoryRequestDTO dto, Order order) {
        return new OrderHistory(
                null,
                order,
                LocalDateTime.now(),
                dto.oldStatus(),
                dto.newStatus(),
                dto.updatedBy()
        );
    }
}
