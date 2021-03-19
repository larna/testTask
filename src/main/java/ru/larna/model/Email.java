package ru.larna.model;

import lombok.Value;

/**
 * Класс описывающий email.
 */
@Value
public class Email {
    /**
     * Строка email адреса
     */
    private final String email;
}
