package ru.larna.model;

import lombok.*;
import ru.larna.util.parsers.UserWrongFormatException;

import java.util.List;

/**
 * Класс описывающий пользователя
 */
@Value
@Builder
public class User {
    private final String name;
    private final List<Email> emails;

    @Override
    public String toString() {
        String emailsString = emails.stream()
                .map(Email::getEmail)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElseThrow(UserWrongFormatException::new);
        return name + " -> " + emailsString;
    }
}
