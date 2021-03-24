package ru.larna.model;

import lombok.Builder;
import lombok.Value;
import ru.larna.util.parsers.UserWrongFormatException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс описывающий пользователя
 */
@Value
@Builder
public class User {
    /**
     * Имя пользователя
     */
    private final String name;
    /**
     * Set email'ов пользователя
     */
    private final Set<Email> emails;
    /**
     * Метод преобразования объекта в строку
     *
     * @return возвращает строку в следующем формате: username -> email1, email2, ...., emailN
     * В случае возникновения ошибок при преобразовании email'ов пользователя в строку выбрасывает UserWrongFormatException
     * @see UserWrongFormatException
     */
    @Override
    public String toString() {
        String emailsString = emails.stream()
                .map(Email::getEmail)
                .collect(Collectors.joining(", "));
        return name + " -> " + emailsString;
    }
}
