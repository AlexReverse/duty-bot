package ru.qdutybot.dutybot.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class HelpCommand {
    private String textHelpCommand = helpCommand();
    private String helpCommand() {
        var text = """
                /duty для уточнения дежурного
                
                /setup {Имя дежурного} - для назнечения дежурного
                Пример: /setup Александр
                """;
        return text;
    }
}
