package com.shopifystore.controller;

import com.shopifystore.dto.ContactMessageDto;
import com.shopifystore.dto.OrderDto;
import com.shopifystore.dto.StatsDto;
import com.shopifystore.dto.StatusRequest;
import com.shopifystore.entity.OrderStatus;
import com.shopifystore.entity.Role;
import com.shopifystore.repository.ContactMessageRepository;
import com.shopifystore.repository.ProductRepository;
import com.shopifystore.repository.UserRepository;
import com.shopifystore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/** Everything under /api/admin/** needs the ADMIN role (see SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ContactMessageRepository contactMessageRepository;

    public AdminController(OrderService orderService, ProductRepository productRepository,
                           UserRepository userRepository, ContactMessageRepository contactMessageRepository) {
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.contactMessageRepository = contactMessageRepository;
    }

    @GetMapping("/orders")
    public List<OrderDto> orders() {
        return orderService.allOrders();
    }

    @PatchMapping("/orders/{id}/status")
    public OrderDto updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return orderService.updateStatus(id, request.status());
    }

    @GetMapping("/messages")
    public List<ContactMessageDto> messages() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ContactMessageDto::from).toList();
    }

    @GetMapping("/stats")
    public StatsDto stats() {
        List<OrderDto> all = orderService.allOrders();
        BigDecimal revenue = all.stream()
                .filter(o -> !OrderStatus.CANCELLED.name().equals(o.status()))
                .map(OrderDto::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long customers = userRepository.findAll().stream().filter(u -> u.getRole() == Role.USER).count();
        return new StatsDto(productRepository.count(), all.size(), customers, revenue);
    }
}
