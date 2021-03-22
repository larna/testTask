package ru.larna.services;

import lombok.extern.slf4j.Slf4j;
import ru.larna.model.Email;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserWrongFormatException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Сервис слияния пользователей.
 * Если 2 разных пользователя имеют одинаковые email, то они учитываются как один пользователь с объединенным списком
 * email, для такого пользователя будет взято имя первого пользователя для которого обнаружатся общие email,
 * остальные имена будут отброшены. Порядок следования пользователей сохраняется.
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
    private final HashMap<Email, OrderedUser> emailsMap;
    /**
     * Set актуальных пользователей.
     */
    private final LinkedHashSet<OrderedUser> userSet;

    public UserMigration(IOService ioService, UserParser userParser) {
        this.ioService = ioService;
        this.userParser = userParser;
        this.emailsMap = new HashMap<>();
        this.userSet = new LinkedHashSet<>();
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
        long counter = 0;
        String str;
        boolean isExit;
        do {
            str = ioService.read();
            isExit = isStopHandle(str);

            if (!isExit) {
                OrderedUser checkedUser = new OrderedUser(userParser.parse(str), ++counter);
                List<Email> existsEmailsList = this.getAllCrossingEmails(checkedUser.getUser().getEmails());
                if (existsEmailsList == null || existsEmailsList.isEmpty())
                    addNewUser(checkedUser);
                else
                    mergeUserWithExistsEmails(checkedUser, existsEmailsList);
            }
        } while (!isExit);
    }

    /**
     * Сохранить/Вывести результат
     *
     * @throws IOException в случае IO ошибки выбрасывает исключение
     */
    private void saveResult() throws IOException {
        userSet.forEach(user -> ioService.write(user.getUser().toString()));
    }

    /**
     * Регистрация нового пользователя
     *
     * @param user - пользователь
     */
    private void addNewUser(OrderedUser user) {
        user.getUser().getEmails().forEach(email -> emailsMap.put(email, user));
        userSet.add(user);
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
    private void mergeUserWithExistsEmails(OrderedUser user, List<Email> crossEmails) {
        List<OrderedUser> usersList = crossEmails.stream()
                .map(emailsMap::get)
                .distinct()
                .sorted(Comparator.comparing(OrderedUser::getOrderNum))
                .collect(Collectors.toList());

        OrderedUser mergedUser = usersList.get(0);
        IntStream.range(1, usersList.size())
                .forEach(i -> {
                    OrderedUser existUser = usersList.get(i);
                    userSet.remove(existUser);
                    assignEmailsToUser(existUser.getUser().getEmails(), mergedUser);
                });
        assignEmailsToUser(user.getUser().getEmails(), mergedUser);
    }

    /**
     * Назначить пользователю email'ы
     *
     * @param emails set of emails
     * @param user   пользователь в список которого email нужно добавить
     */
    private void assignEmailsToUser(Set<Email> emails, OrderedUser user) {
        emails.forEach(email -> emailsMap.put(email, user));
        user.getUser().getEmails().addAll(emails);
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
