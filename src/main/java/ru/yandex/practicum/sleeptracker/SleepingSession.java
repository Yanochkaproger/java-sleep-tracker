package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class SleepingSession {

    private final LocalDateTime sleepStart;
    private final LocalDateTime sleepEnd;
    private final SleepQuality quality;

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

    public long getDurationMinutes() {
        return sleepStart.until(sleepEnd, ChronoUnit.MINUTES);
    }

    public boolean isNightSession() {
        LocalDateTime start = getSleepStart();
        LocalDateTime end = getSleepEnd();

        // Проверяем каждую ночь в диапазоне от начала сна до конца сна
        // Ночь: с 00:00 до 06:00 включительно

        // Начинаем с полуночи дня начала сна
        LocalDateTime nightStart = start.toLocalDate().atStartOfDay();
        while (!nightStart.isAfter(end)) {
            LocalDateTime nightEnd = nightStart.plusHours(6); // до 06:00

            // Проверяем пересечение: [start, end] ∩ [nightStart, nightEnd] ≠ ∅
            boolean overlaps = !end.isBefore(nightStart) && !start.isAfter(nightEnd);
            if (overlaps) {
                return true;
            }
            nightStart = nightStart.plusDays(1);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[%s → %s] %s", sleepStart, sleepEnd, quality);
    }
}
