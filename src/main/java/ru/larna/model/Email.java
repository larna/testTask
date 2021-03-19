package ru.larna.model;

import lombok.*;

/**
 * Класс описывающий email.
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Email {
    private final String email;
    private User user;
}
