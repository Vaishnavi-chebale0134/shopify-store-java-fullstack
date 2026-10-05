package com.shopifystore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
        @NotEmpty(message = "Cart is empty") @Valid List<OrderItemRequest> items,
        @NotBlank(message = "Shipping address is required") String shippingAddress) {
}
