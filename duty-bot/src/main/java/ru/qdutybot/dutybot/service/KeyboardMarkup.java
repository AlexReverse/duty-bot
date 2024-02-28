package ru.qdutybot.dutybot.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.qdutybot.dutybot.Team;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Setter
@Getter
public class KeyboardMarkup {
    private final String TEAM1 = Team.TEAM1.getString();
    private final String TEAM2 = Team.TEAM2.getString();
    private final String TEAM3 = Team.TEAM3.getString();

    private final ReplyKeyboardMarkup replyKeyboardMarkup = keyboardMarkup();
    private ReplyKeyboardMarkup keyboardMarkup() {
        final ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.getOneTimeKeyboard();
        markup.getKeyboard();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(TEAM1));
        keyboardRows.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(TEAM2));
        keyboardRows.add(row2);
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(TEAM3));
        keyboardRows.add(row3);

        markup.setKeyboard(keyboardRows);
        return markup;
    }
}
