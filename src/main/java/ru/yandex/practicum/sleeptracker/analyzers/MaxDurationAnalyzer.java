package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class MaxDurationAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    private static final String DESCRIPTION = "Максимальная длительность (мин)";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult(DESCRIPTION, 0);
        }
        long max = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .max()
                .orElse(0);
        return new SleepAnalysisResult(DESCRIPTION, max);
    }
}