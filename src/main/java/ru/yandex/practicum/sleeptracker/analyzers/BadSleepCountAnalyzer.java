package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class BadSleepCountAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long badCount = sessions.stream()
                .filter(session -> session.getQuality() == ru.yandex.practicum.sleeptracker.SleepQuality.BAD)
                .count();
        return new SleepAnalysisResult("Плохих ночей", badCount);
    }
}