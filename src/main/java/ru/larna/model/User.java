package ru.larna.model;

import lombok.Builder;
import lombok.Value;
import ru.larna.util.parsers.UserWrongFormatException;

import java.util.Set;

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
     * Список email пользователя
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
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElseThrow(UserWrongFormatException::new);
        return name + " -> " + emailsString;
    }
}
