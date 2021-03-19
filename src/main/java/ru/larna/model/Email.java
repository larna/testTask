package ru.larna.model;

import lombok.*;

/**
 * Класс описывающий email.
 */
@Data
@Builder
@AllArgsConstructor
public class Email {
    private final String email;
    private User user;
}
