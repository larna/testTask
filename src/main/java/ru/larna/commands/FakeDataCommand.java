package ru.larna.commands;

import lombok.extern.slf4j.Slf4j;
import ru.larna.services.IOService;
import ru.larna.services.IOServiceImpl;
import ru.larna.services.UserMigration;
import ru.larna.util.generator.FakeUsersDataGenerator;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserParserImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

/**
 * Обработка аргументов командной строки
 * Команда генерации фейковых данных
 */
@Slf4j
public class FakeDataCommand {
    private final static String FILENAME_TEMPLATE = "fakeData-N%d.txt";
    private final static String FILE_OUTPUT_TEMPLATE = "out-N%d.txt";

    public static FakeDataCommand getInstance(){
        return new FakeDataCommand();
    }
    /**
     * Метод производит генерацию фейковых данных для 10,100,1000,10000...пользователей и проверяет алгоритм слияния на них
     * При генерации данные сбрасываются в соответствующий файл fakeData-N%d.txt
     * Метод проверки производит чтение из соответствующего файла и сбрасывает свой результат в файл out-N...txt
     */
    public void execute() {
        final UserParser parser = new UserParserImpl();
        IntStream.rangeClosed(1, 6).map(i -> (int) Math.pow(10, i))
                .forEach(usersCount -> {
                    generate(usersCount);
                    checkOnFakeData(usersCount, parser);
                });
    }

    /**
     * Запуск генерации фейковых данных.
     *
     * @param usersCount ожидаемое кол-во пользователей
     */
    private void generate(Integer usersCount) {
        String filename = String.format(FILENAME_TEMPLATE, usersCount);
        try (OutputStream out = Files.newOutputStream(Path.of(filename));
             IOService ioService = new IOServiceImpl(System.in, new PrintStream(out))) {
            FakeUsersDataGenerator generator = new FakeUsersDataGenerator(usersCount, ioService);
            generator.generate();
        } catch (IOException e) {
            log.error("Generate fake data for {} error", usersCount, e);
        }
    }

    /**
     * Проверка на массивах fake данных
     *
     * @param userCount - кол-во пользователей
     * @param parser    - парсер строки в объект пользователя
     */
    private void checkOnFakeData(Integer userCount, UserParser parser) {
        try {
            String inFilename = String.format(FILENAME_TEMPLATE, userCount);
            String outFilename = String.format(FILE_OUTPUT_TEMPLATE, userCount);

            try (InputStream in = Files.newInputStream(Path.of(inFilename));
                 OutputStream out = Files.newOutputStream(Path.of(outFilename));
                 IOService ioService = new IOServiceImpl(in, out)) {
                log.info("**************************************");
                log.info("migrate userCount {} ", userCount);

                long start = System.currentTimeMillis();

                UserMigration migration = new UserMigration(ioService, parser);
                migration.migrate();

                long stop = System.currentTimeMillis();
                log.info("Execution time - {} result users count {}", (stop - start), migration.getActualUsersCount());
            }
        } catch (IOException e) {
            log.error("migration for userCount = {} error", userCount, e);
        }
    }
}
