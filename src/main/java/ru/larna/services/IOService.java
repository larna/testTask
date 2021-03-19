package ru.larna.services;

import java.io.IOException;

/**
 * Сервис ввода/вывода данных
 */
public interface IOService extends AutoCloseable {
    /**
     * Метод чтения данных
     *
     * @return возвращает прочитанную строку
     * @throws IOException выбрасывает исключение в случае ошибок ввода/вывода
     */
    String read() throws IOException;

    /**
     * Метод вывода данных
     *
     * @param message строка для вывода
     */
    void write(String message);

    /**
     * Закрытие потоков
     */
    void close();
}
