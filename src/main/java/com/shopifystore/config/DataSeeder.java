package com.shopifystore.config;

import com.shopifystore.entity.Category;
import com.shopifystore.entity.Product;
import com.shopifystore.entity.Role;
import com.shopifystore.entity.User;
import com.shopifystore.repository.CategoryRepository;
import com.shopifystore.repository.ProductRepository;
import com.shopifystore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Runs at startup: creates the default admin account and sample products
 * (only when the tables are empty, so your own data is never overwritten).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.name}")
    private String adminName;
    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;

    public DataSeeder(UserRepository userRepository, CategoryRepository categoryRepository,
                      ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedProducts();
    }

    private void seedAdmin() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }
        Map<String, Category> categories = new HashMap<>();
        for (String name : new String[]{"Women", "Men", "Shoes", "Accessories"}) {
            categories.put(name, categoryRepository.save(new Category(name)));
        }

        add(categories, "Essential White Tee", "Soft cotton crew-neck tee that goes with everything.", "540", 25, "product-01.jpg.webp", "Women");
        add(categories, "Classic White Shirt", "Crisp cotton shirt with a relaxed fit.", "700", 18, "product-02.jpg.webp", "Women");
        add(categories, "Blue Check Shirt", "Short sleeve gingham shirt for a smart casual look.", "450", 30, "product-03.jpg.webp", "Men");
        add(categories, "Classic Trench Coat", "Belted wool-blend coat with a faux fur collar.", "890", 10, "product-04.jpg.webp", "Women");
        add(categories, "Grey Button Shirt", "Oversized grey shirt, easy to layer.", "340", 22, "product-05.jpg.webp", "Women");
        add(categories, "Checked Blazer", "Tailored checked blazer for work or weekends.", "1023", 8, "product-07.jpg.webp", "Women");
        add(categories, "Cactus Print Tee", "Fun graphic tee with a cactus print.", "890", 15, "product-08.jpg.webp", "Women");
        add(categories, "Converse All Star Hi", "Iconic black high-top plimsolls.", "907", 20, "product-09.jpg.webp", "Shoes");
        add(categories, "Femme Crop Top", "Fitted black crop top with short sleeves.", "560", 27, "product-10.jpg.webp", "Women");
        add(categories, "Light Blue Check Shirt", "Slim-fit long sleeve gingham shirt.", "765", 14, "product-11.jpg.webp", "Men");
        add(categories, "Leather Belt", "Brown leather belt with a metal buckle.", "1200", 12, "product-12.jpg.webp", "Accessories");
        add(categories, "Casual White Tee", "Relaxed white tee, perfect with jeans.", "765", 19, "product-13.jpg.webp", "Women");
        add(categories, "Rose Patch Crop Tee", "Black cropped tee with an embroidered rose.", "480", 16, "product-14.jpg.webp", "Women");
        add(categories, "Minimal Black Watch", "Black dial watch with a mesh strap.", "1500", 9, "product-15.jpg.webp", "Accessories");
        add(categories, "Relaxed Grey Tee", "Soft marl-grey scoop neck tee.", "420", 24, "product-16.jpg.webp", "Women");
    }

    private void add(Map<String, Category> categories, String name, String description, String price,
                     int stock, String image, String category) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        p.setImageUrl("/images/products/" + image);
        p.setCategory(categories.get(category));
        productRepository.save(p);
    }
}
