package ru.larna;

import ru.larna.commands.FakeDataCommand;
import ru.larna.commands.MigrationCommand;

import java.util.Arrays;

public class Main {
    private final static String FAKE_DATA_COMMAND = "--fake-data";

    public static void main(String[] args) {
        if (isFakeDataArg(args)) {
            new FakeDataCommand().execute();
            return;
        }
        new MigrationCommand().execute();
    }

    private static Boolean isFakeDataArg(String... args) {
        return args != null && Arrays.asList(args).contains(FAKE_DATA_COMMAND);
    }

}
