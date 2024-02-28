package ru.qdutybot.dutybot;

import lombok.Getter;

@Getter
public enum Team {
    TEAM1("Asti"),
    TEAM2("SPECIAL"),
    TEAM3("LOGOS");

    private String string;

    Team(String string) {
        this.string = string;
    }
}
