package ru.larna.services;

import lombok.extern.slf4j.Slf4j;
import ru.larna.model.Email;
import ru.larna.model.User;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserWrongFormatException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    private final Set<Email> emailsSet;
    /**
     * Список актуальных пользователей
     */
    private final Set<User> userSet;

    public UserMigration(IOService ioService, UserParser userParser) {
        this.ioService = ioService;
        this.userParser = userParser;
        this.emailsSet = new HashSet<>();
        this.userSet = new HashSet<>();
    }

    /**
     * Получить кол-во актуальных пользователей
     *
     * @return возвращает кол-во обнаруженных уникальных пользователей
     */
    public Integer getActualUsersCount() {
        return userSet.size();
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
     * @throws IOException в случае IO ошибки выбрасывает исключение
     */
    private void merge() throws IOException {
        String str;
        boolean isExit = false;
        do {
            str = ioService.read();
            isExit = isStopHandle(str);

            if (!isExit) {
                User checkedUser = userParser.parse(str);
                List<Email> existsEmailsList = this.getAllCrossingEmails(checkedUser.getEmails());
                if (existsEmailsList == null || existsEmailsList.isEmpty())
                    addNewUser(checkedUser);
                else
                    mergeUsers(checkedUser, existsEmailsList);
            }
        } while (!isExit);
    }

    /**
     * Сохранить/Вывести результат
     *
     * @throws IOException в случае IO ошибки выбрасывает исключение
     */
    private void saveResult() throws IOException {
        userSet.stream().forEach(user -> ioService.write(user.toString()));
    }

    /**
     * Регистрация нового пользователя
     *
     * @param user - пользователь
     */
    private void addNewUser(User user) {
        user.getEmails().forEach(emailsSet::add);
        userSet.add(user);
    }

    /**
     * Найти пользователя по любому из списка email
     *
     * @param emails список email
     * @return возвращает пользователя если хотя бы один их списка email уже зарегистрирован, null если ни одного email
     * еще не было зарегистрировано
     */
    private List<Email> getAllCrossingEmails(List<Email> emails) {
        return emails.stream()
                .filter(emailsSet::contains)
                .collect(Collectors.toList());
    }

    /**
     * Слить пользователей и их email. Email в списке пользователя не дублируются.
     * В случае обнаружения пользователя, который в своем списке email совместил email пользователей из existsUsersList,
     * производиться слияние этих пользователей в одного.
     * В качестве актуального пользователя берется последний найденный
     *
     * @param existsEmailList список уже существующих email
     * @param user            пользователь который в своем списке email совместил email пользователей из existsUsersList
     */
    private void mergeUsers(User user, List<Email> existsEmailList) {
        existsEmailList.forEach(existEmail -> {
            User existUser = existEmail.getUser();
            userSet.remove(user);
            existUser.getEmails().forEach(email -> email.setUser(user));
        });
        user.getEmails().stream().forEach(emailsSet::add);
        userSet.add(user);
    }

    /**
     * Проверка: Следует ли прекратить обработку?
     *
     * @param str проверяемая строка
     * @return возвращает true - получена пустая строка, false - получили не пустую строку
     */
    private Boolean isStopHandle(String str) {
        return str == null || str.isEmpty();
    }
}
