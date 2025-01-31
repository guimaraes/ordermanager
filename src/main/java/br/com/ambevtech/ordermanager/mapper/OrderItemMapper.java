package br.com.ambevtech.ordermanager.mapper;

import br.com.ambevtech.ordermanager.dto.OrderItemRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderItemResponseDTO;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.OrderItem;
import br.com.ambevtech.ordermanager.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class OrderItemMapper {

    public static OrderItemResponseDTO toResponseDTO(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }

    public static List<OrderItemResponseDTO> toResponseDTOList(List<OrderItem> items) {
        return items.stream().map(OrderItemMapper::toResponseDTO).collect(Collectors.toList());
    }

    public static OrderItem toEntity(OrderItemRequestDTO dto, Product product, Order order) {
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(dto.quantity()));
        return new OrderItem(
                null, order, product, dto.quantity(), product.getPrice(), totalPrice
        );
    }
}
