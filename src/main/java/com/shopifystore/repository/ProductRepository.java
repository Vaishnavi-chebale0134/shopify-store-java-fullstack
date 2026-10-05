package com.shopifystore.repository;

import com.shopifystore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryNameIgnoreCase(String category);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByCategoryNameIgnoreCaseAndNameContainingIgnoreCase(String category, String keyword);
}
