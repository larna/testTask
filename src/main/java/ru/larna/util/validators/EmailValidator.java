package ru.larna.util.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для проверки email
 */
public class EmailValidator implements Validator {
    /**
     * Регулярное выражение для проверки email
     */
    private static final String EMAIL_PATTERN = "^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$";
    private final Pattern pattern;

    private EmailValidator() {
        this.pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Получить экземпляр класса EmailValidator
     *
     * @return возвращает новый объект валидатора
     */
    public static EmailValidator getInstance() {
        return new EmailValidator();
    }

    /**
     * Проверить валидность email
     *
     * @param o - ожидает строку с email
     * @return true - если email валидный, false - иначе
     */
    public Boolean validate(Object o) {
        if (o != null && o instanceof String) {
            String email = (String) o;
            Matcher matcher = pattern.matcher(email);
            return matcher.find();
        }
        return false;
    }
}
