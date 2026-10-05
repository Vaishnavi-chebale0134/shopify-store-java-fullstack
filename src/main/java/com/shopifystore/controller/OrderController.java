package com.shopifystore.controller;

import com.shopifystore.dto.OrderDto;
import com.shopifystore.dto.OrderRequest;
import com.shopifystore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Authentication.getName() is the email of the logged-in user (set by JwtAuthFilter)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto place(@Valid @RequestBody OrderRequest request, Authentication authentication) {
        return orderService.placeOrder(authentication.getName(), request);
    }

    @GetMapping("/my")
    public List<OrderDto> myOrders(Authentication authentication) {
        return orderService.myOrders(authentication.getName());
    }
}
