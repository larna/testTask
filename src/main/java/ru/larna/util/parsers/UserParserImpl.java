package ru.larna.util.parsers;

import ru.larna.model.Email;
import ru.larna.model.User;
import ru.larna.util.validators.EmailValidator;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс UserParserImpl. Разбирает полученную строку в объект User.
 * Ожидает строку в формате: user1 -> xxx@ya.ru, foo@gmail.com, lol@mail.ru
 * Выбрасывает исключение UserWrongFormatException в случае обнаружения некорректных входных значений
 */
public class UserParserImpl implements UserParser {
    /**
     * Регулярное выражение для проверки формата строки
     * ^                                - начало строки
     * (\w+)                            - группа для  имени пользователя
     * \s*->\s*                         - любое кол-во пробелов и между ними ->
     * (([a-z0-9_\.-@]+\s*\,{0,1}\s*)+) - описание групп email, где разрешены латинские символы, цифры, @, '.' пробелы,
     * после группы email может следовать запятая, и email должен быть хотя бы один
     * $                                - окончание строки
     */
    private static final String USER_INFORMATION_PATTERN = "^(\\w+)\\s*->\\s*(([a-z0-9_\\.-@]+\\s*\\,{0,1}\\s*)+)$";
    private final EmailValidator emailValidator;

    public UserParserImpl() {
        emailValidator = EmailValidator.getInstance();
    }

    /**
     * Разбирает строку
     *
     * @param str строка в формате user1 -> xxx@ya.ru, foo@gmail.com, lol@mail.ru
     * @return объект User
     * @throws UserWrongFormatException выбрасывает исключение в случае некорректных данных
     *                                  (нет пользователя, использованы некорректные символы в имени пользователя, нет email,
     *                                  email разделены не запятыми, а например: пробелами, некорректный формат email или
     *                                  используются некорректные для email символы)
     */
    @Override
    public User parse(String str) throws UserWrongFormatException {
        if (str == null || str.isEmpty())
            throw new IllegalArgumentException("String argument can't to be NULL or empty");

        Pattern p = Pattern.compile(USER_INFORMATION_PATTERN);
        Matcher matcher = p.matcher(str.strip());
        if (!matcher.matches())
            throw new UserWrongFormatException("Wrong format - " + str);

        final String userName = matcher.group(1);
        final String emailsString = matcher.group(2);
        return User.builder()
                .name(userName)
                .emails(parseEmails(emailsString))
                .build();
    }

    /**
     * Разбирает подстроку email'ов
     *
     * @param emails - строка содержащая email
     * @return список объектов Email
     */
    private Set<Email> parseEmails(String emails) {
        String[] emailArray = emails.split("\\s*,\\s*");
        if (!isAllEmailsValid(emailArray))
            throw new UserWrongFormatException("Wrong emails - " + emails);

        return Arrays.stream(emailArray)
                .map(Email::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Проверяет все ли email в массиве валидны
     *
     * @param emailsString массив email'ов
     * @return true - если все валидны, false в противном случае
     */
    private Boolean isAllEmailsValid(String[] emailsString) {
        return Arrays.stream(emailsString)
                .allMatch(emailValidator::validate);
    }
}
