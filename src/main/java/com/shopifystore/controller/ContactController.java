package com.shopifystore.controller;

import com.shopifystore.dto.ContactRequest;
import com.shopifystore.entity.ContactMessage;
import com.shopifystore.repository.ContactMessageRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactMessageRepository repository;

    public ContactController(ContactMessageRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> submit(@Valid @RequestBody ContactRequest request) {
        ContactMessage m = new ContactMessage();
        m.setName(request.name());
        m.setEmail(request.email().trim());
        m.setMessage(request.message().trim());
        repository.save(m);
        return Map.of("message", "Thanks! We received your message.");
    }
}
