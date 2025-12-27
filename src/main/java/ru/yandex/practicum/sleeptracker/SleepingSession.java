package ru.yandex.practicum.sleeptracker;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SleepingSession {
    private final LocalDateTime sleepStart;
    private final LocalDateTime sleepEnd;
    private final ru.yandex.practicum.sleeptracker.SleepQuality quality;

    public SleepingSession(LocalDateTime sleepStart, LocalDateTime sleepEnd, SleepQuality quality) {
        if (sleepStart == null || sleepEnd == null || sleepEnd.isBefore(sleepStart)) {
            throw new IllegalArgumentException("Некорректные даты сна");
        }
        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
        this.quality = quality;
    }

    public LocalDateTime getSleepStart() {
        return sleepStart;
    }

    public LocalDateTime getSleepEnd() {
        return sleepEnd;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    // Длительность в минутах
    public long getDurationMinutes() {
        return sleepStart.until(sleepEnd, ChronoUnit.MINUTES);
    }

    // ✅ ИСПРАВЛЕНО: корректное определение ночной сессии
    public boolean isNightSession() {
        LocalDateTime start = getSleepStart();
        LocalDateTime end = getSleepEnd();

        // Проверяем ночь, начинающуюся в день начала сна: [DD 00:00, DD 06:00]
        LocalDateTime nightStart = start.toLocalDate().atStartOfDay();
        LocalDateTime nightEnd = nightStart.plusHours(6);

        if (!end.isBefore(nightStart) && !start.isAfter(nightEnd)) {
            return true;
        }

        // Проверяем следующую ночь: [DD+1 00:00, DD+1 06:00]
        nightStart = nightStart.plusDays(1);
        nightEnd = nightEnd.plusDays(1);

        return !end.isBefore(nightStart) && !start.isAfter(nightEnd);
    }

    @Override
    public String toString() {
        return String.format("[%s → %s] %s", sleepStart, sleepEnd, quality);
    }
}