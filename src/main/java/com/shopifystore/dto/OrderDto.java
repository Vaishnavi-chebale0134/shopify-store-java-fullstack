package com.shopifystore.dto;

import com.shopifystore.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(Long id, String status, BigDecimal totalAmount, String shippingAddress,
                       LocalDateTime createdAt, String customerName, String customerEmail,
                       List<OrderItemDto> items) {

    public static OrderDto from(Order o) {
        return new OrderDto(o.getId(), o.getStatus().name(), o.getTotalAmount(), o.getShippingAddress(),
                o.getCreatedAt(), o.getUser().getName(), o.getUser().getEmail(),
                o.getItems().stream().map(OrderItemDto::from).toList());
    }
}
