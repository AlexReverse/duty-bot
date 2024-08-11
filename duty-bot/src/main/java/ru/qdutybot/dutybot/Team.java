package ru.qdutybot.dutybot;

import lombok.Getter;

/*
В типе данных Long необходимо указать id чата
 */
@Getter
public enum Team {
    TEAM1("Asti", -0L),
    TEAM2("SPECIAL", -0L),
    TEAM3("LOGOS", -0L);

    private String string;
    private Long l;

    Team(String string, Long l) {
        this.string = string;
        this.l=l;
    }
}
