package com.shopifystore.dto;

import java.math.BigDecimal;

public record StatsDto(long products, long orders, long customers, BigDecimal revenue) {
}
