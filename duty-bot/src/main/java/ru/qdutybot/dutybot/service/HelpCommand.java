package ru.qdutybot.dutybot.service;

import lombok.Getter;

@Getter
public class HelpCommand {
    private String textHelpCommand = helpCommand();
    private String helpCommand() {
        var text = """
                /duty для уточнения дежурного
                
                /setup {Имя и Фамилия дежурного} - для назнечения дежурного
                Пример: /setup Александр Евтеев
                """;
        return text;
    }
}
