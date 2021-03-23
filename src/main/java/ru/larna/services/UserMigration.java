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
    private final Map<Email, Long> emailsMap;
    /**
     * Set актуальных пользователей.
     */
    private final Map<Long, User> userMap;

    public UserMigration(IOService ioService, UserParser userParser) {
        this.ioService = ioService;
        this.userParser = userParser;
        this.emailsMap = new HashMap<>();
        this.userMap = new LinkedHashMap<>();
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
        long counter = 0;
        String str;
        boolean isExit;
        do {
            str = ioService.read();
            isExit = isStopHandle(str);

            if (!isExit) {
                User checkedUser = userParser.parse(str);
                List<Email> existsEmailsList = this.getAllCrossingEmails(checkedUser.getEmails());
                if (existsEmailsList == null || existsEmailsList.isEmpty())
                    addNewUser(checkedUser, ++counter);
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
        userMap.values().forEach(user -> ioService.write(user.toString()));
    }
    /**
     * Регистрация нового пользователя
     *
     * @param user - пользователь
     * @param userId - назначенный пользователю на время обработки идентификатор.
     */
    private void addNewUser(User user, Long userId) {
        user.getEmails().forEach(email -> emailsMap.put(email, userId));
        userMap.put(userId, user);
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
     * В качестве актуального пользователя берется первый обработанный пользователь, его поиск выполняется на основании
     * списка пересекающихся email и в него будет производиться операция слияния всех email для всех остальных копий
     * этого пользователя. Копии пользователя будут удалены.
     *
     * @param user        новый пользователь
     * @param crossEmails список email, для которых найдены пересечения email нового пользователя с уже имеющимися
     */
    private void mergeUserWithExistsEmails(User user, List<Email> crossEmails) {
        TreeSet<Long> usersIdList = crossEmails.stream().map(emailsMap::get).collect(Collectors.toCollection(TreeSet::new));

        Iterator<Long> it = usersIdList.iterator();
        final Long mergedUserId = it.next();
        while (it.hasNext()) {
            Long userId = it.next();
            User existUser = userMap.get(userId);
            userMap.remove(userId);
            assignEmailsToUser(existUser.getEmails(), mergedUserId);
        }
        assignEmailsToUser(user.getEmails(), mergedUserId);
    }

    /**
     * Назначить пользователю email'ы
     *
     * @param emails set of emails
     * @param userId назначенный для пользователя идентификатор
     */
    private void assignEmailsToUser(Set<Email> emails, Long userId) {
        emails.forEach(email -> emailsMap.put(email, userId));
        userMap.get(userId).getEmails().addAll(emails);
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
