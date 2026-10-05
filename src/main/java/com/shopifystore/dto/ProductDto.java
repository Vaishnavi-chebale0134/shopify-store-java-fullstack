package com.shopifystore.dto;

import com.shopifystore.entity.Product;

import java.math.BigDecimal;

public record ProductDto(Long id, String name, String description, BigDecimal price,
                         int stock, String imageUrl, String category) {

    public static ProductDto from(Product p) {
        return new ProductDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getStock(), p.getImageUrl(), p.getCategory().getName());
    }
}
