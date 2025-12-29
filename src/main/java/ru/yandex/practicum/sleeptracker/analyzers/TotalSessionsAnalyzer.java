package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class TotalSessionsAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final String DESCRIPTION = "Всего сессий сна";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(DESCRIPTION, sessions.size());
    }
}