package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analyzers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SleepTrackerAnalyzerTest {

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private SleepingSession s(String start, String end, SleepQuality q) {
        return new SleepingSession(
                LocalDateTime.parse(start, F),
                LocalDateTime.parse(end, F),
                q
        );
    }

    // ============ Тесты для TotalSessionsAnalyzer (2 теста) ============
    @Test
    void totalSessions_emptyList() {
        assertEquals(0, new TotalSessionsAnalyzer().apply(List.of()).getValue());
    }

    @Test
    void totalSessions_threeSessions() {
        List<SleepingSession> sessions = List.of(s("01.10.25 23:00", "02.10.25 07:00", SleepQuality.GOOD));
        assertEquals(1, new TotalSessionsAnalyzer().apply(sessions).getValue());
    }

    // ============ Тесты для MinDurationAnalyzer (2 теста) ============
    @Test
    void minDuration_singleSession() {
        List<SleepingSession> sessions = List.of(s("01.10.25 23:00", "02.10.25 00:00", SleepQuality.GOOD));
        assertEquals(60L, new MinDurationAnalyzer().apply(sessions).getValue());
    }

    @Test
    void minDuration_multipleSessions() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:00", "02.10.25 00:00", SleepQuality.GOOD),   // 60 мин
                s("02.10.25 23:00", "03.10.25 02:00", SleepQuality.NORMAL)  // 180 мин
        );
        assertEquals(60L, new MinDurationAnalyzer().apply(sessions).getValue());
    }

    // ============ Тесты для MaxDurationAnalyzer (2 теста) ============
    @Test
    void maxDuration_singleSession() {
        List<SleepingSession> sessions = List.of(s("01.10.25 23:00", "02.10.25 07:00", SleepQuality.GOOD));
        assertEquals(480L, new MaxDurationAnalyzer().apply(sessions).getValue());
    }

    @Test
    void maxDuration_multipleSessions() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:00", "02.10.25 02:00", SleepQuality.GOOD),   // 180 мин
                s("02.10.25 23:00", "03.10.25 07:00", SleepQuality.NORMAL)  // 480 мин
        );
        assertEquals(480L, new MaxDurationAnalyzer().apply(sessions).getValue());
    }

    // ============ Тесты для AvgDurationAnalyzer (2 теста) ============
    @Test
    void avgDuration_singleSession() {
        List<SleepingSession> sessions = List.of(s("01.10.25 00:00", "01.10.25 01:00", SleepQuality.GOOD));
        assertEquals(60.0, new AvgDurationAnalyzer().apply(sessions).getValue());
    }

    @Test
    void avgDuration_multipleSessions() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 00:00", "01.10.25 01:00", SleepQuality.GOOD),   // 60 мин
                s("02.10.25 00:00", "02.10.25 02:00", SleepQuality.NORMAL)  // 120 мин
        );
        assertEquals(90.0, new AvgDurationAnalyzer().apply(sessions).getValue());
    }

    // ============ Тесты для BadSleepCountAnalyzer (2 теста) ============
    @Test
    void badSleepCount_noBad() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:00", "02.10.25 07:00", SleepQuality.GOOD),
                s("02.10.25 23:00", "03.10.25 07:00", SleepQuality.NORMAL)
        );
        assertEquals(0L, new BadSleepCountAnalyzer().apply(sessions).getValue());
    }

    @Test
    void badSleepCount_twoBad() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:00", "02.10.25 07:00", SleepQuality.BAD),
                s("02.10.25 23:00", "03.10.25 07:00", SleepQuality.GOOD),
                s("03.10.25 23:00", "04.10.25 07:00", SleepQuality.BAD)
        );
        assertEquals(2L, new BadSleepCountAnalyzer().apply(sessions).getValue());
    }

    // ============ Тесты для SleeplessNightsAnalyzer (4 теста — требования ТЗ!) ============
    @Test
    void sleeplessNights_noSleepless() {
        // Две ночи подряд с ночным сном
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:00", "02.10.25 07:00", SleepQuality.GOOD), // ночь 01/02
                s("02.10.25 23:30", "03.10.25 06:30", SleepQuality.NORMAL)  // ночь 02/03
        );
        assertEquals(0L, new SleeplessNightsAnalyzer().apply(sessions).getValue());
    }

    @Test
    void sleeplessNights_oneSleepless() {
        // Пропущена ночь 02/03
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:00", "02.10.25 07:00", SleepQuality.GOOD), // ночь 01/02
                // ночь 02/03 — пропущена
                s("03.10.25 23:30", "04.10.25 06:30", SleepQuality.NORMAL)  // ночь 03/04
        );
        assertEquals(1L, new SleeplessNightsAnalyzer().apply(sessions).getValue());
    }

    @Test
    void sleeplessNights_earlyMorningSleepNotSleepless() {
        // Сон 05.10 00:10–06:20 → ночь 04/05 — НЕ бессонная!
        List<SleepingSession> sessions = List.of(
                s("04.10.25 23:00", "05.10.25 00:00", SleepQuality.GOOD), // ночь 04/05
                s("05.10.25 00:10", "05.10.25 06:20", SleepQuality.GOOD)   // тоже ночь 04/05
        );
        assertEquals(0L, new SleeplessNightsAnalyzer().apply(sessions).getValue());
    }

    @Test
    void sleeplessNights_oneGap() {
        // Ночь 02/03 пропущена
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:00", "02.10.25 07:00", SleepQuality.GOOD), // ночь 01/02
                s("03.10.25 23:00", "04.10.25 07:00", SleepQuality.GOOD)  // ночь 03/04
        );
        assertEquals(1L, new SleeplessNightsAnalyzer().apply(sessions).getValue());
    }

    // ============ Тесты для ChronotypeAnalyzer (2 теста) ============
    @Test
    void chronotype_owl() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 23:30", "02.10.25 09:30", SleepQuality.GOOD) // поздно/поздно
        );
        assertEquals("Сова", new ChronotypeAnalyzer().apply(sessions).getValue().toString());
    }

    @Test
    void chronotype_lark() {
        List<SleepingSession> sessions = List.of(
                s("01.10.25 21:30", "02.10.25 06:30", SleepQuality.GOOD) // рано/рано
        );
        assertEquals("Жаворонок", new ChronotypeAnalyzer().apply(sessions).getValue().toString());
    }
}