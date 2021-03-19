package ru.larna.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.larna.model.Email;
import ru.larna.model.User;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserParserImpl;
import ru.larna.util.parsers.UserWrongFormatException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Класс UserParser")
class UserParserImplTest {
    private final UserParser parser;

    UserParserImplTest() {
        this.parser = new UserParserImpl();
    }

    @DisplayName("Должен корректно парсить строку")
    @ParameterizedTest
    @ValueSource(strings = {"user1 -> user1@mail.ru, test@gmail.com",
            "user2 -> user2@mail.ru ,test2@gmail.com , sjdfhskh@vncjd.ru"})
    public void shouldCorrectParseStringWithUserAndEmails(String str) {
        User actual = parser.parse(str);
        assertNotNull(actual);
    }

    @DisplayName("Должен корректно парсить строку и возвращать объект пользователя")
    @Test
    public void shouldCorrectParseAndReturnValidUser() {
        final String string = "user1 -> user1@mail.ru, test@gmail.com";
        User expected = User.builder()
                .name("user1")
                .emails(Set.of(new Email("user1@mail.ru"), new Email("test@gmail.com")))
                .build();

        User actual = parser.parse(string);
        assertEquals(expected, actual);
    }

    @DisplayName("Должен выбрасывать исключение если строка не валидная")
    @ParameterizedTest
    @ValueSource(strings = {" -> user1@mail.ru, test@gmail.com", "user1 -> ", "user user@gmail.com",
            "user1 -> user@normal.com, user\\user@gmail.com", "user1 -> user@normal.com user@gmail.com"})
    public void shouldThrowExceptionIfUserIsEmpty(String str) {
        assertThrows(UserWrongFormatException.class, () -> parser.parse(str));
    }

}