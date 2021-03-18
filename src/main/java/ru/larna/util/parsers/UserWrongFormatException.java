package ru.larna.util.parsers;

/**
 * Исключение сигнализирующее об ошибке при разборе строки пользователя
 */
public class UserWrongFormatException extends RuntimeException {
    public UserWrongFormatException() {
    }

    public UserWrongFormatException(String message) {
        super(message);
    }
}
