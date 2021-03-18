package ru.larna.services;

import lombok.extern.slf4j.Slf4j;
import ru.larna.model.Email;
import ru.larna.model.User;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserWrongFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис слияния пользователей.
 * Если 2 разных пользователя имеют одинаковые email, то они учитываются как один пользователь с объединенным списком
 * email, для такого пользователя будет взято имя первого пользователя, второе имя будет отброшено.
 */
@Slf4j
public class UserMigration {
    /**
     * Сервис ввода/вывода
     */
    private final IOService ioService;
    /**
     * Парсер строки пользователя
     */
    private final UserParser userParser;
    /**
     * Map для хранения уже встреченных email.
     */
    private final Map<Email, User> emailsMap;
    /**
     * Список актуальных пользователей
     */
    private final List<User> userList;

    public UserMigration(IOService ioService, UserParser userParser) {
        this.ioService = ioService;
        this.userParser = userParser;
        this.emailsMap = new HashMap<>();
        this.userList = new ArrayList<>();
    }

    /**
     * Получить кол-во актуальных пользователей
     *
     * @return
     */
    public Integer getActualUsersCount() {
        return userList.size();
    }

    /**
     * Метод миграции/ слияния пользователей
     */
    public void migrate() {
        try {
            merge();
            saveResult();
        } catch (IOException e) {
            log.error("Migration IOError", e);
        } catch (UserWrongFormatException e) {
            log.error("Migration wrong format", e);
        }
    }

    /**
     * Произвести слияние данных о пользователях
     *
     * @throws IOException
     */
    private void merge() throws IOException {
        String str;
        Boolean isExit = false;
        do {
            str = ioService.read();
            isExit = isStopHandle(str);

            if (!isExit) {
                User checkedUser = userParser.parse(str);
                User existsUser = getUserByEmails(checkedUser.getEmails());
                if (existsUser == null)
                    addNewUser(checkedUser);
                else
                    addNewEmailsToUser(existsUser, checkedUser.getEmails());
            }
        } while (!isExit);
    }

    /**
     * Сохранить/Вывести результат
     *
     * @throws IOException
     */
    private void saveResult() throws IOException {
        userList.stream()
                .forEach(user -> {
                    ioService.write(user.format());
                });
    }

    /**
     * Регистрация нового пользователя
     *
     * @param user - пользователь
     */
    private void addNewUser(User user) {
        user.getEmails().stream()
                .forEach(email -> emailsMap.putIfAbsent(email, user));
        userList.add(user);
    }

    /**
     * Найти пользователя по любому из списка email
     *
     * @param emails список email
     * @return возвращает пользователя если хотя бы один их списка email уже зарегистрирован, null если ни одного email
     * еще не было зарегистрировано
     */
    private User getUserByEmails(List<Email> emails) {
        Email foundEmail = emails.stream()
                .filter(email -> emailsMap.containsKey(email))
                .findFirst()
                .orElse(null);
        return foundEmail == null ? null : emailsMap.get(foundEmail);
    }

    /**
     * Добавить список новых email к пользователю. Email в списке пользователя не дублируются.
     * @param user пользователь
     * @param emails список email
     */
    private void addNewEmailsToUser(User user, List<Email> emails) {
        emails.stream().forEach(email -> {
            if (emailsMap.putIfAbsent(email, user) == null)
                user.getEmails().add(email);
        });
    }

    /**
     * Проверка: Следует ли прекратить обработку?
     * @param str проверяемая строка
     * @return возвращает true - получена пустая строка, false - получили не пустую строку
     */
    private Boolean isStopHandle(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return false;
    }
}
