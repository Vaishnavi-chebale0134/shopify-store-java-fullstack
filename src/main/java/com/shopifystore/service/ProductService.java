package com.shopifystore.service;

import com.shopifystore.dto.ProductDto;
import com.shopifystore.dto.ProductRequest;
import com.shopifystore.entity.Category;
import com.shopifystore.entity.Product;
import com.shopifystore.exception.ResourceNotFoundException;
import com.shopifystore.repository.CategoryRepository;
import com.shopifystore.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> search(String category, String keyword) {
        boolean hasCategory = category != null && !category.isBlank() && !category.equalsIgnoreCase("all");
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        List<Product> products;
        if (hasCategory && hasKeyword) {
            products = productRepository.findByCategoryNameIgnoreCaseAndNameContainingIgnoreCase(category.trim(), keyword.trim());
        } else if (hasCategory) {
            products = productRepository.findByCategoryNameIgnoreCase(category.trim());
        } else if (hasKeyword) {
            products = productRepository.findByNameContainingIgnoreCase(keyword.trim());
        } else {
            products = productRepository.findAll();
        }
        return products.stream().map(ProductDto::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductDto getById(Long id) {
        return ProductDto.from(find(id));
    }

    @Transactional
    public ProductDto create(ProductRequest req) {
        Product product = new Product();
        apply(product, req);
        return ProductDto.from(productRepository.save(product));
    }

    @Transactional
    public ProductDto update(Long id, ProductRequest req) {
        Product product = find(id);
        apply(product, req);
        return ProductDto.from(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(find(id));
    }

    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    private void apply(Product product, ProductRequest req) {
        product.setName(req.name().trim());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setStock(req.stock());
        product.setImageUrl(req.imageUrl());
        product.setCategory(findOrCreateCategory(req.category()));
    }

    private Category findOrCreateCategory(String name) {
        String clean = name.trim();
        return categoryRepository.findByNameIgnoreCase(clean)
                .orElseGet(() -> categoryRepository.save(new Category(clean)));
    }
}
