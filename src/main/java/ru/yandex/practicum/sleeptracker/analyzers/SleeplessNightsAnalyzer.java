package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleeplessNightsAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Бессонных ночей", 0);
        }

        // Границы логирования
        LocalDateTime firstStart = sessions.get(0).getSleepStart();
        LocalDateTime lastEnd = sessions.get(sessions.size() - 1).getSleepEnd();

        // Первая ночь: если начали после 12 — следующая ночь, иначе — сегодняшняя
        LocalDate firstNight = firstStart.getHour() >= 12
                ? firstStart.toLocalDate().plusDays(1)
                : firstStart.toLocalDate();

        LocalDate lastNight = lastEnd.toLocalDate();

        // Общее количество ночей в периоде
        long totalNights = ChronoUnit.DAYS.between(firstNight, lastNight.plusDays(1));

        // Ночи, в которые был НОЧНОЙ сон
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(SleepingSession::isNightSession)
                .map(this::getNightDate)
                .collect(Collectors.toSet());

        long sleeplessNights = totalNights - nightsWithSleep.size();
        return new SleepAnalysisResult("Бессонных ночей", Math.max(0, sleeplessNights));
    }

    // ✅ ИСПРАВЛЕНО: для 05.10 00:10 → ночь 04/05
    private LocalDate getNightDate(SleepingSession session) {
        LocalDateTime start = session.getSleepStart();
        return start.getHour() < 12
                ? start.toLocalDate().minusDays(1)
                : start.toLocalDate();
    }
}