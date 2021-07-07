package ru.javawebinar.topjava;

import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TimingRules {

    private static final Logger log = LoggerFactory.getLogger("result");

    private static final StringBuilder results = new StringBuilder();

    public static final Stopwatch STOPWATCH = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            String result = String.format("%-75s %7d", description.getDisplayName(), TimeUnit.NANOSECONDS.toMillis(nanos));
            results.append(result).append('\n');
            log.info("\n\u001B[32m" +
                    "--------------------------------------------------------------------------------------\n" +
                    result + " ms\n" +
                    "--------------------------------------------------------------------------------------" +
                    "\u001B[0m\n");
        }
    };

    // элемент разметки
    //    https://dzone.com/articles/applying-new-jdk-11-string-methods
//    private static final String DELIM = "-".repeat(103);

    //форматируем красивый вывод в консоль
    public static final ExternalResource SUMMARY = new ExternalResource() {

        //обнуляем перед запуском тестов класса
        @Override
        protected void before() throws Throwable {
            results.setLength(0);
        }

        //выводим отформатированный результат
        @Override
        protected void after() {
            log.info("\n\u001B[35m" +
                    "---------------------------------------------------------------------------------------" +
                    "\nTests                                                                      Duration, ms" +
                    "\n" + "---------------------------------------------------------------------------------------" +
                    "\n" +
                    results +
                    "---------------------------------------------------------------------------------------" +
                    "\u001B[0m\n");
        }
    };
}
