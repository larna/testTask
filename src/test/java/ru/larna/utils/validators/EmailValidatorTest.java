package ru.larna.utils.validators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.larna.util.validators.EmailValidator;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Класс EmailValidator")
class EmailValidatorTest {
    @DisplayName("Должен возвращать true в случае корректного email")
    @ParameterizedTest
    @ValueSource(strings = {"email@gmail.com", "email1@gmail.com","email_1@gmail.com","email_1@gmail_2.com","test@gmail.com"})
    public void shouldCorrectValidateEmail(String email){
        assertEquals(true, EmailValidator.getInstance().validate(email));
    }
    @DisplayName("Должен возвращать false в случае некорректного email")
    @ParameterizedTest
    @ValueSource(strings = {"emailgmail.com", "email@gmailcom","email|&@gmail.com","email|&@gmail,com","email|&@gmail_com"})
    public void shouldReturnFalseIfEmailIsNotValid(String email){
        assertEquals(false, EmailValidator.getInstance().validate(email));
    }

}