package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {

    public enum Chronotype {
        OWL("Сова"), LARK("Жаворонок"), DOVE("Голубь");
        private final String displayName;
        Chronotype(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        // Только ночные сессии
        Map<Chronotype, Long> counts = sessions.stream()
                .filter(SleepingSession::isNightSession)
                .collect(Collectors.groupingBy(this::classifyNight, Collectors.counting()));

        long owl = counts.getOrDefault(Chronotype.OWL, 0L);
        long lark = counts.getOrDefault(Chronotype.LARK, 0L);
        long dove = counts.getOrDefault(Chronotype.DOVE, 0L);

        Chronotype result;
        if (owl > lark && owl > dove) {
            result = Chronotype.OWL;
        } else if (lark > owl && lark > dove) {
            result = Chronotype.LARK;
        } else {
            result = Chronotype.DOVE; // включая равенство
        }

        return new SleepAnalysisResult("Хронотип", result);
    }

    private Chronotype classifyNight(SleepingSession session) {
        LocalTime sleepTime = session.getSleepStart().toLocalTime();
        LocalTime wakeTime = session.getSleepEnd().toLocalTime();

        boolean lateSleep = sleepTime.isAfter(LocalTime.of(23, 0));
        boolean lateWake = wakeTime.isAfter(LocalTime.of(9, 0));
        boolean earlySleep = sleepTime.isBefore(LocalTime.of(22, 0));
        boolean earlyWake = wakeTime.isBefore(LocalTime.of(7, 0));

        if (lateSleep && lateWake) return Chronotype.OWL;
        if (earlySleep && earlyWake) return Chronotype.LARK;
        return Chronotype.DOVE;
    }
}