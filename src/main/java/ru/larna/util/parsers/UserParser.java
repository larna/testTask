package ru.larna.util.parsers;

import ru.larna.model.User;
/**
 * Интерфейс UserParser.
 * Разбирает полученную строку в объект User.
 * Ожидает строку в формате: user1 -> xxx@ya.ru, foo@gmail.com, lol@mail.ru
 * Выбрасывает исключение UserWrongFormatException в случае обнаружения некорректных входных значений
 */
public interface UserParser {
    User parse(String str) throws UserWrongFormatException;
}
