package ru.larna;

import ru.larna.commands.FakeDataCommand;
import ru.larna.commands.MigrationCommand;

import java.util.Arrays;

public class Main {
    private final static String FAKE_DATA_COMMAND = "--fake-data";

    public static void main(String[] args) {
        if (isFakeDataArg(args)) {
            FakeDataCommand.execute();
            return;
        }
        MigrationCommand.execute();
    }

    private static Boolean isFakeDataArg(String... args) {
        if (args != null && Arrays.stream(args).anyMatch(FAKE_DATA_COMMAND::equals))
            return true;
        return false;
    }

}
