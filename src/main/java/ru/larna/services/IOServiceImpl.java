package ru.larna.services;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Сервис ввода/вывода
 */
@Slf4j
public class IOServiceImpl implements IOService {
    /**
     * Поток чтения
     */
    private final BufferedReader in;
    /**
     * Поток записи
     */
    private final PrintStream out;

    public IOServiceImpl(InputStream in, OutputStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = new PrintStream(out);
    }
    /**
     * Метод чтения данных
     * @return возвращает прочитанную строку
     * @throws IOException выбрасывает исключение в случае ошибки ввода/вывода
     */
    @Override
    public String read() throws IOException {
        return in.readLine();
    }
    /**
     * Метод вывода данных
     * @param message выводимая строка
     */
    @Override
    public void write(String message) {
        this.out.println(message);
    }
    /**
     * Закрытие потоков
     */
    @Override
    public void close() {
        try {
            if (this.in != null)
                this.in.close();
            if (this.out != null)
                this.out.close();
        } catch (IOException e) {
            log.error("Close error", e);
        }
    }
}
