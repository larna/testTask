package ru.larna.commands;

import lombok.extern.slf4j.Slf4j;
import ru.larna.services.IOService;
import ru.larna.services.IOServiceImpl;
import ru.larna.services.UserMigration;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserParserImpl;

/**
 * Обработка аргументов командной строки
 * Команда запуска процесса слияния
 */
@Slf4j
public class MigrationCommand {
    private MigrationCommand() {
    }

    public static MigrationCommand getInstance() {
        return new MigrationCommand();
    }

    /**
     * Запустить процесс слияния пользователей
     */
    public void execute() {
        final UserParser parser = new UserParserImpl();
        try (IOService ioService = new IOServiceImpl(System.in, System.out)) {
            new UserMigration(ioService, parser).migrate();
        }
    }
}
