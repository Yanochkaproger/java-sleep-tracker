package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analyzers.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SleepTrackerApp {

    private static final List<Function<List<SleepingSession>, SleepAnalysisResult>> ANALYZERS = Arrays.asList(
            new TotalSessionsAnalyzer(),
            new MinDurationAnalyzer(),
            new MaxDurationAnalyzer(),
            new AvgDurationAnalyzer(),
            new BadSleepCountAnalyzer(),
            new SleeplessNightsAnalyzer(),
            new ChronotypeAnalyzer()
    );

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Укажите путь к файлу: java SleepTrackerApp sleep_log.txt");
            return;
        }

        String logFile = args[0];
        try {
            List<SleepingSession> sessions = SleepLogParser.parseLog(logFile);
            System.out.println("✅ Загружено " + sessions.size() + " сессий сна\n");

            ANALYZERS.stream()
                    .map(analyzer -> analyzer.apply(sessions))
                    .forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Файл не найден: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
        //Анализатор сна, реализован без циклов for/while