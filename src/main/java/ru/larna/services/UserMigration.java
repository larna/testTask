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
 * email, для такого пользователя будет взято имя последнего пользователя для которого обнаружатся общие email,
 * остальные имена будут отброшены. В процессе слияния порядок следования пользователей не гарантируется.
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
    private final HashMap<Email, User> emailsMap;
    /**
     * Map актуальных пользователей
     */
    private final HashMap<String, User> userMap;

    public UserMigration(IOService ioService, UserParser userParser) {
        this.ioService = ioService;
        this.userParser = userParser;
        this.emailsMap = new HashMap<>();
        this.userMap = new HashMap<>();
    }

    /**
     * Получить кол-во актуальных пользователей
     *
     * @return возвращает кол-во обнаруженных уникальных пользователей
     */
    public Integer getActualUsersCount() {
        return userMap.size();
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
        boolean isExit;
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
        userMap.values().forEach(user -> ioService.write(user.toString()));
    }

    /**
     * Регистрация нового пользователя
     *
     * @param user - пользователь
     */
    private void addNewUser(User user) {
        user.getEmails().forEach(email -> emailsMap.put(email, user));
        userMap.put(user.getName(), user);
    }

    /**
     * Найти список email которые совпадают с уже учтенными
     *
     * @param emails set of emails
     * @return возвращает список объектов Email из переданного аргумента emails, для которых есть совпадения
     * с уже учтенными email
     */
    private List<Email> getAllCrossingEmails(Set<Email> emails) {
        return emails.stream()
                .filter(emailsMap::containsKey)
                .collect(Collectors.toList());
    }

    /**
     * Слияние пользователей для которых найдены пересечения в email. Email в списке пользователя не дублируются.
     * В качестве актуального пользователя берется последний найденный
     *
     * @param user        пользователь, который останется после слияния, все остальные пользователи будут отброшены,
     *                    а их email будут добавлены в данного пользователя
     * @param crossEmails список email, для которых найдены пересечения с уже имеющимися
     */
    private void mergeUsers(User user, List<Email> crossEmails) {
        crossEmails.forEach(email -> {
            User existUser = emailsMap.get(email);
            userMap.remove(existUser.getName());
            user.getEmails().addAll(existUser.getEmails());
            existUser.getEmails().forEach(existsEmail -> emailsMap.put(existsEmail, user));
        });
        addNewUser(user);
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
