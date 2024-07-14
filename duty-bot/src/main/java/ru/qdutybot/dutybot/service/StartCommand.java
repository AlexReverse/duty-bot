package ru.qdutybot.dutybot.service;

public class StartCommand {
    public String startCommand(String userName) {
        var text = """
                Добро пожаловать, %s!
                /help - для получения справки
                """;
        return String.format(text, userName);
    }
}
