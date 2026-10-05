package com.shopifystore.service;

import com.shopifystore.dto.OrderDto;
import com.shopifystore.dto.OrderItemRequest;
import com.shopifystore.dto.OrderRequest;
import com.shopifystore.entity.*;
import com.shopifystore.exception.BadRequestException;
import com.shopifystore.exception.ResourceNotFoundException;
import com.shopifystore.repository.OrderRepository;
import com.shopifystore.repository.ProductRepository;
import com.shopifystore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Places an order inside ONE transaction: checks stock, reduces stock, calculates the total
     * on the server (never trusting prices sent by the browser) and saves order + items together.
     * If anything fails, everything is rolled back.
     */
    @Transactional
    public OrderDto placeOrder(String email, OrderRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(req.shippingAddress().trim());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest line : req.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + line.productId()));

            if (product.getStock() < line.quantity()) {
                throw new BadRequestException("Not enough stock for '" + product.getName()
                        + "'. Available: " + product.getStock());
            }
            product.setStock(product.getStock() - line.quantity());

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(line.quantity());
            item.setUnitPrice(product.getPrice());
            order.addItem(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(line.quantity())));
        }
        order.setTotalAmount(total);
        return OrderDto.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderDto> myOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream().map(OrderDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> allOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(OrderDto::from).toList();
    }

    @Transactional
    public OrderDto updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
        order.setStatus(status);
        return OrderDto.from(order);
    }
}
