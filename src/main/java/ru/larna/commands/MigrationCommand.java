package ru.larna.commands;

import lombok.extern.slf4j.Slf4j;
import ru.larna.services.IOService;
import ru.larna.services.IOServiceImpl;
import ru.larna.services.UserMigration;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserParserImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Обработка аргументов командной строки
 * Команда запуска процесса слияния
 */
@Slf4j
public class MigrationCommand {
    private final static String DEFAULT_FILE_OUTPUT = "out.txt";

    /**
     * Запустить процесс слияния пользователей
     */
    public void execute() {
        final UserParser parser = new UserParserImpl();
        log.info("**************************************");
        log.info("Data from stdin...");

        try (OutputStream out = Files.newOutputStream(Path.of(DEFAULT_FILE_OUTPUT));
             IOService ioService = new IOServiceImpl(System.in, out)) {
            UserMigration migration = new UserMigration(ioService, parser);

            long start = System.currentTimeMillis();
            migration.migrate();
            long stop = System.currentTimeMillis();

            log.info("Execution time - {} result users count {}", (stop - start), migration.getActualUsersCount());
        } catch (IOException e) {
            log.error("migrate error for data from stdin", e);
        }
    }
}
