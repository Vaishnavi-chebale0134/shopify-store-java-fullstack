package com.shopifystore.dto;

import com.shopifystore.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull(message = "Status is required") OrderStatus status) {
}
