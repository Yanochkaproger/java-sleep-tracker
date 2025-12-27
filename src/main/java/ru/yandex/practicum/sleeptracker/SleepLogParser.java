package ru.yandex.practicum.sleeptracker;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SleepLogParser {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static List<SleepingSession> parseLog(String filePath) throws IOException {
        return Files.readAllLines(Path.of(filePath)).stream()
                .filter(line -> !line.trim().isEmpty())
                .map(SleepLogParser::parseLine)
                .collect(Collectors.toList());
    }

    private static SleepingSession parseLine(String line) {
        try {
            String[] parts = line.split(";");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Неверный формат");
            }

            LocalDateTime start = LocalDateTime.parse(parts[0], FORMATTER);
            LocalDateTime end = LocalDateTime.parse(parts[1], FORMATTER);
            SleepQuality quality = SleepQuality.valueOf(parts[2].trim().toUpperCase());

            return new SleepingSession(start, end, quality);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в строке: " + line, e);
        }
    }
}