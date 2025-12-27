package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analyzers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SleepTrackerAppTest {

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private SleepingSession s(String start, String end, SleepQuality q) {
        return new SleepingSession(
                LocalDateTime.parse(start, F),
                LocalDateTime.parse(end, F),
                q
        );
    }

    // 1. Базовый: количество сессий
    @Test
    void testTotalSessions() {
        List<SleepingSession> sessions = List.of(s("01.10.25 23:00", "02.10.25 07:00", SleepQuality.GOOD));
        assertEquals(1, new TotalSessionsAnalyzer().apply(sessions).getValue());
    }

    // 2. Длительность: мин/макс/среднее
    @Test
    void testDurations() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:00", "02.10.25 00:00", SleepQuality.GOOD),   // 60 мин
                s("02.10.25 23:00", "03.10.25 02:00", SleepQuality.NORMAL)  // 180 мин
        );
        assertEquals(60L, new MinDurationAnalyzer().apply(sessions).getValue());
        assertEquals(180L, new MaxDurationAnalyzer().apply(sessions).getValue());
        assertEquals(120.0, new AvgDurationAnalyzer().apply(sessions).getValue());
    }

    // 3. Плохие ночи
    @Test
    void testBadSleepCount() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:00", "02.10.25 07:00", SleepQuality.BAD),
                s("02.10.25 23:00", "03.10.25 07:00", SleepQuality.GOOD)
        );
        assertEquals(1L, new BadSleepCountAnalyzer().apply(sessions).getValue());
    }

    // 4. Бессонные ночи (ключевой кейс: 05.10 00:10 → ночь 04/05)
    @Test
    void testSleeplessNights() {
        List<SleepingSession> sessions = List.of(
                s("04.10.25 23:00", "05.10.25 00:00", SleepQuality.GOOD), // ночь 04/05
                s("05.10.25 00:10", "05.10.25 06:20", SleepQuality.GOOD)  // тоже ночь 04/05
        );
        // Обе сессии — одна ночь, ночная → 0 бессонных
        assertEquals(0L, new SleeplessNightsAnalyzer().apply(sessions).getValue());
    }

    // 5. Хронотип: сова (поздно лёг + поздно встал)
    @Test
    void testChronotypeOwl() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:30", "02.10.25 09:30", SleepQuality.GOOD) // 23:30 → 9:30
        );
        assertEquals("Сова", new ChronotypeAnalyzer().apply(sessions).getValue().toString());
    }

    // 6. Хронотип: жаворонок (рано лёг + рано встал)
    @Test
    void testChronotypeLark() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 21:30", "02.10.25 06:30", SleepQuality.GOOD) // 21:30 → 6:30
        );
        assertEquals("Жаворонок", new ChronotypeAnalyzer().apply(sessions).getValue().toString());
    }
}