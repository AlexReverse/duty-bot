package ru.qdutybot.dutybot.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Monday {
    public String getMonday() {
        LocalDate localDate = LocalDate.now();
        while (!localDate.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            localDate = localDate.minusDays(1);
        }
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
