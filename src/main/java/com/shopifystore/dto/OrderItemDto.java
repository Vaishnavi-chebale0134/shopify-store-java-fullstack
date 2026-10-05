package com.shopifystore.dto;

import com.shopifystore.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemDto(Long productId, String productName, String imageUrl,
                           int quantity, BigDecimal unitPrice) {

    public static OrderItemDto from(OrderItem i) {
        return new OrderItemDto(i.getProduct().getId(), i.getProduct().getName(),
                i.getProduct().getImageUrl(), i.getQuantity(), i.getUnitPrice());
    }
}
