package ru.qdutybot.dutybot.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.qdutybot.dutybot.Team;

@Slf4j
@Service
@Setter
@Getter
public class InlineKeyboard {
    private final String TEAM1 = Team.TEAM1.getString();
    private final String TEAM2 = Team.TEAM2.getString();
    private final String TEAM3 = Team.TEAM3.getString();
    private final InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboard();
    private InlineKeyboardMarkup InlineKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var button1 = new InlineKeyboardButton();
        button1.setText(TEAM1);
        button1.setCallbackData(TEAM1);
        var button2 = new InlineKeyboardButton();
        button2.setText(TEAM2);
        button2.setCallbackData(TEAM2);
        var button3 = new InlineKeyboardButton();
        button3.setText(TEAM3);
        button3.setCallbackData(TEAM3);
        rowInline.add(button1);
        rowInline.add(button2);
        rowInline.add(button3);
        rowsInline.add(rowInline);
        markup.setKeyboard(rowsInline);
        return markup;
    }
}
