package ru.larna.util.generator;

import lombok.extern.slf4j.Slf4j;
import ru.larna.services.IOService;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Генерация фейковых данных
 */
@Slf4j
public class FakeUsersDataGenerator {
    private final Integer usersCount;
    private final IOService ioService;

    public FakeUsersDataGenerator(Integer usersCount, IOService ioService) {
        this.usersCount = usersCount;
        this.ioService = ioService;
    }

    /**
     * Метод генерации фейковых данных
     */
    public void generate() {
        //формирую список пользователей с неповторяющимися email
        final int actualUsersCount = (int) Math.round(usersCount * 0.75);
        final List<String> users = IntStream.rangeClosed(0, actualUsersCount)
                .mapToObj(userIndex -> {
                    return "user_" + userIndex + " -> email_" + userIndex + "@gmail.com, email_0" +
                            userIndex + "@gmail.com, email_00" + userIndex + "@gmail.com";
                })
                .collect(Collectors.toList());
        //формирую список пользователей у которых email будут повторяться
        final int fakeDataCount = (int) Math.round(usersCount * 0.25);
        final List<String> fakeUsers = IntStream.rangeClosed(0, fakeDataCount)
                .mapToObj(userIndex -> {
                    Random random = new Random();
                    int secondEmail = random.nextInt(usersCount);

                    return "user_" + userIndex + " -> email_" + userIndex + "@gmail.com, email_" +
                            secondEmail + "@gmail.com, email_0" + userIndex + "@gmail.com";
                }).collect(Collectors.toList());
        users.addAll(fakeUsers);
        Collections.shuffle(users);
        users.stream().forEach(userString -> ioService.write(userString));
    }
}
