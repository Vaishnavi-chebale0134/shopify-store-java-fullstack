package com.shopifystore.dto;

import com.shopifystore.entity.ContactMessage;

import java.time.LocalDateTime;

public record ContactMessageDto(Long id, String name, String email, String message, LocalDateTime createdAt) {

    public static ContactMessageDto from(ContactMessage m) {
        return new ContactMessageDto(m.getId(), m.getName(), m.getEmail(), m.getMessage(), m.getCreatedAt());
    }
}
