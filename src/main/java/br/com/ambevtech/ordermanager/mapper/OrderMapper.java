package br.com.ambevtech.ordermanager.mapper;

import br.com.ambevtech.ordermanager.dto.OrderItemRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderResponseDTO;
import br.com.ambevtech.ordermanager.dto.OrderItemResponseDTO;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.Customer;
import br.com.ambevtech.ordermanager.model.OrderItem;
import br.com.ambevtech.ordermanager.model.Product;
import br.com.ambevtech.ordermanager.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponseDTO toResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(OrderMapper::toOrderItemResponseDTO)
                        .collect(Collectors.toList())
        );
    }

    public static Order toEntity(OrderRequestDTO dto, Customer customer, List<OrderItem> items) {
        return Order.builder()
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status(dto.status() != null ? dto.status() : OrderStatus.PENDING)
                .items(items)
                .totalAmount(items.stream()
                        .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }


    public static OrderItem toEntity(OrderItemRequestDTO dto, Product product, Order order) {
        return OrderItem.builder()
                .product(product)
                .order(order)
                .quantity(dto.quantity())
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(dto.quantity())))
                .build();
    }

    private static OrderItemResponseDTO toOrderItemResponseDTO(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }
}
