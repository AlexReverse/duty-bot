package ru.qdutybot.dutybot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class SetupCommand {
    public SendMessage setupCommand(Long chatId) {
        SendMessage text = new SendMessage();
        text.setChatId(String.valueOf(chatId));
        text.setText("Укажите команду для дежурного");
        InlineKeyboardMarkup markup = new InlineKeyboard().getInlineKeyboardMarkup();
        text.setReplyMarkup(markup);
        return text;
    }
}
